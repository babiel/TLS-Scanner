/**
 * TLS-Client-Scanner - A TLS configuration and analysis tool based on TLS-Attacker
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsscanner.clientscanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import de.rub.nds.tlsattacker.core.certificate.CertificateByteChooser;
import de.rub.nds.tlsattacker.core.certificate.CertificateKeyPair;
import de.rub.nds.tlsattacker.core.config.delegate.GeneralDelegate;
import de.rub.nds.tlsattacker.core.workflow.NamedThreadFactory;
import de.rub.nds.tlsscanner.clientscanner.client.Orchestrator;
import de.rub.nds.tlsscanner.clientscanner.client.DefaultOrchestrator;
import de.rub.nds.tlsscanner.clientscanner.config.ClientScannerConfig;
import de.rub.nds.tlsscanner.clientscanner.config.ISubcommand;
import de.rub.nds.tlsscanner.clientscanner.config.modes.ScanClientCommandConfig;
import de.rub.nds.tlsscanner.clientscanner.config.modes.StandaloneCommandConfig;
import de.rub.nds.tlsscanner.clientscanner.dispatcher.IDispatcher;
import de.rub.nds.tlsscanner.clientscanner.dispatcher.sni.SNIDispatcher;
import de.rub.nds.tlsscanner.clientscanner.dispatcher.sni.SNINopDispatcher;
import de.rub.nds.tlsscanner.clientscanner.probe.BaseProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.ForcedCompressionProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.FreakProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.IProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.PaddingOracleProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.VersionProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.VersionProbe13Random;
import de.rub.nds.tlsscanner.clientscanner.probe.downgrade.DropConnection;
import de.rub.nds.tlsscanner.clientscanner.probe.downgrade.SendAlert;
import de.rub.nds.tlsscanner.clientscanner.probe.recon.HelloReconProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.recon.SNIProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.recon.SupportedCipherSuitesProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.weak.keyexchange.dhe.DHECompositeModulusProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.weak.keyexchange.dhe.DHEMinimumModulusLengthProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.weak.keyexchange.dhe.DHESmallSubgroupProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.weak.keyexchange.dhe.DHEWeakPrivateKeyProbe;
import de.rub.nds.tlsscanner.clientscanner.report.ClientReport;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        Configurator.setAllLevels("de.rub.nds.tlsattacker", Level.INFO);
        Configurator.setAllLevels("de.rub.nds.tlsscanner.clientscanner", Level.DEBUG);
        Patcher.applyPatches();
        {
            // suppress warnings while loading CKPs
            Level logLevel = LogManager.getLogger(CertificateKeyPair.class).getLevel();
            Configurator.setAllLevels("de.rub.nds.tlsattacker.core.certificate.CertificateKeyPair", Level.ERROR);
            CertificateByteChooser.getInstance();
            Configurator.setAllLevels("de.rub.nds.tlsattacker.core.certificate.CertificateKeyPair", logLevel);
        }

        GeneralDelegate generalDelegate = new GeneralDelegate();
        ClientScannerConfig csConfig = new ClientScannerConfig(generalDelegate);
        JCommander jc = csConfig.jCommander;

        try {
            jc.parse(args);
            csConfig.setParsed();
            if (csConfig.getGeneralDelegate().isHelp()) {
                jc.usage();
                return;
            }
            ISubcommand cmd = csConfig.getSelectedSubcommand();
            // TODO outsource execution into commands themselves
            // probably with an interface like IExecutableSubcommand which has a
            // function
            // execute(ClientScannerConfig)
            if (cmd instanceof StandaloneCommandConfig) {
                runStandalone(csConfig);
            } else if (cmd instanceof ScanClientCommandConfig) {
                runScan(csConfig);
            } else {
                throw new ParameterException("Failed to find method to execute for command " + cmd);
            }
        } catch (ParameterException E) {
            LOGGER.error("Could not parse provided parameters", E);
            jc.usage();
        }
    }

    private static List<IProbe> getProbes(Orchestrator orchestrator) {
        // TODO have probes be configurable from commandline
        List<IProbe> probes = new ArrayList<>();
        // .recon (add first)
        probes.add(new HelloReconProbe(orchestrator));
        probes.add(new SNIProbe());
        probes.add(new SupportedCipherSuitesProbe());
        // .downgrade
        probes.addAll(SendAlert.getDefaultProbes(orchestrator));
        probes.add(new DropConnection(orchestrator));
        // .weak.keyexchange.dhe
        probes.add(new DHEMinimumModulusLengthProbe(orchestrator));
        probes.addAll(DHEWeakPrivateKeyProbe.getDefaultProbes(orchestrator));
        probes.addAll(DHECompositeModulusProbe.getDefaultProbes(orchestrator));
        probes.addAll(DHESmallSubgroupProbe.getDefaultProbes(orchestrator));
        // .
        probes.add(new ForcedCompressionProbe(orchestrator));
        probes.add(new FreakProbe(orchestrator));
        // probes.addAll(PaddingOracleProbe.getDefaultProbes(orchestrator));
        probes.addAll(VersionProbe.getDefaultProbes(orchestrator));
        probes.addAll(VersionProbe13Random.getDefaultProbes(orchestrator));
        // probes that are on todo
        if (false) {
            probes.clear();
            probes.addAll(VersionProbe.getDefaultProbes(orchestrator));
            probes.add(new HelloReconProbe(orchestrator));
            probes.add(new SNIProbe());
            probes.add(new SupportedCipherSuitesProbe());
            probes.addAll(PaddingOracleProbe.getDefaultProbes(orchestrator));
        }
        if (false) {
            probes.clear();
            probes.addAll(VersionProbe.getDefaultProbes(orchestrator));
            probes.addAll(VersionProbe13Random.getDefaultProbes(orchestrator));
        }
        return probes;
    }

    private static IDispatcher getStandaloneDispatcher(ClientScannerConfig csConfig) {
        SNIDispatcher disp = new SNIDispatcher();
        LOGGER.info("Using base URL {}", csConfig.getServerBaseURL());
        disp.registerRule(csConfig.getServerBaseURL(), new SNINopDispatcher());
        List<IProbe> probes = getProbes(null);
        for (IProbe p : probes) {
            if (p instanceof BaseProbe && p instanceof IDispatcher) {
                // TODO create some nice interface instead of expecting
                // BaseProbe
                // possibly also add some other form of configurability...
                String prefix = ((BaseProbe) p).getHostnameForStandalone();
                if (prefix != null) {
                    disp.registerRule(prefix, (IDispatcher) p);
                    LOGGER.info("Adding {} at prefix {}", p.getClass().getSimpleName(), prefix);
                } else {
                    LOGGER.debug("Not adding {} as it did not provide a hostname (returned null)", p.getClass()
                            .getSimpleName());
                }
            } else {
                LOGGER.debug("Not adding {} as it is not extended from BaseProbe", p.getClass().getSimpleName());
            }
        }
        return disp;
    }

    private static void runStandalone(ClientScannerConfig csConfig) {
        Server s = new Server(csConfig, getStandaloneDispatcher(csConfig), 8);
        try {
            s.start();
            s.join();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to wait for server exit due to interrupt", e);
            Thread.currentThread().interrupt();
        } finally {
            s.kill();
        }
    }

    private static void runScan(ClientScannerConfig csConfig) {
        int threads = 1;
        int secondaryThreads = 8;
        ThreadPoolExecutor pool = new ThreadPoolExecutor(threads, threads, 1, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(),
                new NamedThreadFactory("cs-probe-runner"));
        // can't decrease core size without additional hassle
        // https://stackoverflow.com/a/15485841/3578387
        ThreadPoolExecutor secondaryPool = new ThreadPoolExecutor(secondaryThreads, secondaryThreads, 1,
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(),
                new NamedThreadFactory("cs-secondary-pool"));
        // Orchestrator types: DefaultOrchestrator and ThreadLocalOrchestrator
        Orchestrator orchestrator = new DefaultOrchestrator(csConfig, secondaryPool, threads + secondaryThreads);

        ClientScanExecutor executor = new ClientScanExecutor(getProbes(orchestrator), null, orchestrator, pool);
        ClientReport rep = executor.execute();
        secondaryPool.shutdown();
        pool.shutdown();

        try {
            File file = null;
            ScanClientCommandConfig scanCfg = csConfig.getSelectedSubcommand(ScanClientCommandConfig.class);
            if (scanCfg.getReportFile() != null) {
                file = new File(scanCfg.getReportFile());
            }
            JAXBContext ctx;
            ctx = JAXBContext.newInstance(ClientReport.class);
            Marshaller marsh = ctx.createMarshaller();
            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marsh.marshal(rep, System.out);
            if (file != null) {
                marsh.marshal(rep, file);
            }
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
