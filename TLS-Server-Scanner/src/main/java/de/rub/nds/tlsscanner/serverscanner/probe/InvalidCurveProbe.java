/**
 * TLS-Server-Scanner - A TLS configuration and analysis tool based on TLS-Attacker
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsscanner.serverscanner.probe;

import de.rub.nds.scanner.core.constants.ScannerDetail;
import de.rub.nds.scanner.core.constants.TestResult;
import de.rub.nds.scanner.core.constants.TestResults;
import de.rub.nds.tlsscanner.core.vector.statistics.DistributionTest;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.AlgorithmResolver;
import de.rub.nds.tlsattacker.core.constants.CertificateKeyType;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.ECPointFormat;
import de.rub.nds.tlsattacker.core.constants.KeyExchangeAlgorithm;
import de.rub.nds.tlsattacker.core.constants.NamedGroup;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.constants.PskKeyExchangeMode;
import de.rub.nds.tlsattacker.core.crypto.ec.CurveFactory;
import de.rub.nds.tlsattacker.core.crypto.ec.EllipticCurveOverFp;
import de.rub.nds.tlsattacker.core.crypto.ec.Point;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.workflow.ParallelExecutor;
import de.rub.nds.tlsattacker.core.workflow.factory.WorkflowTraceType;
import de.rub.nds.tlsscanner.core.constants.TlsAnalyzedProperty;
import de.rub.nds.tlsscanner.core.constants.TlsProbeType;
import de.rub.nds.tlsscanner.core.probe.result.VersionSuiteListPair;
import de.rub.nds.tlsscanner.serverscanner.leak.InvalidCurveTestInfo;
import de.rub.nds.tlsscanner.serverscanner.probe.invalidcurve.InvalidCurveAttacker;
import de.rub.nds.tlsscanner.serverscanner.probe.invalidcurve.InvalidCurveResponse;
import de.rub.nds.tlsscanner.serverscanner.probe.invalidcurve.constants.InvalidCurveScanType;
import de.rub.nds.tlsscanner.serverscanner.probe.invalidcurve.point.InvalidCurvePoint;
import de.rub.nds.tlsscanner.serverscanner.probe.invalidcurve.point.TwistedCurvePoint;
import de.rub.nds.tlsscanner.serverscanner.probe.invalidcurve.vector.InvalidCurveVector;
import de.rub.nds.tlsscanner.serverscanner.probe.namedgroup.NamedGroupWitness;
import de.rub.nds.tlsscanner.serverscanner.probe.result.InvalidCurveResult;
import de.rub.nds.tlsscanner.serverscanner.report.ServerReport;
import de.rub.nds.tlsscanner.serverscanner.selector.ConfigSelector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InvalidCurveProbe extends TlsServerProbe<ConfigSelector, ServerReport, InvalidCurveResult> {

    private static final int CURVE_TWIST_MAX_ORDER = 23;

    private final ScannerDetail scanDetail;

    private boolean supportsRenegotiation;

    private TestResult supportsSecureRenegotiation;

    private TestResult issuesTls13SessionTickets;

    private TestResult supportsTls13PskDhe;

    private List<ProtocolVersion> supportedProtocolVersions;

    private List<NamedGroup> supportedFpGroups;

    private List<NamedGroup> supportedTls13FpGroups;

    private HashMap<ProtocolVersion, List<CipherSuite>> supportedECDHCipherSuites;

    private List<ECPointFormat> fpPointFormatsToTest;

    private List<ECPointFormat> tls13FpPointFormatsToTest;

    private Map<NamedGroup, NamedGroupWitness> namedCurveWitnesses;

    private Map<NamedGroup, NamedGroupWitness> namedCurveWitnessesTls13;

    public InvalidCurveProbe(ConfigSelector configSelector, ParallelExecutor parallelExecutor) {
        super(parallelExecutor, TlsProbeType.INVALID_CURVE, configSelector);
        scanDetail = configSelector.getScannerConfig().getScanDetail();
    }

    @Override
    public InvalidCurveResult executeTest() {
        List<InvalidCurveVector> vectors = prepareVectors();
        List<InvalidCurveResponse> responses = new LinkedList<>();
        for (InvalidCurveVector vector : vectors) {
            if (benignHandshakeSuccessful(vector)) {
                InvalidCurveResponse scanResponse = executeSingleScan(vector, InvalidCurveScanType.REGULAR);
                if (scanResponse.getVectorResponses().size() > 0) {
                    DistributionTest distTest =
                        new DistributionTest(new InvalidCurveTestInfo(vector), scanResponse.getVectorResponses(),
                            getInfinityProbability(vector, InvalidCurveScanType.REGULAR));
                    if (distTest.isDistinctAnswers()
                        && scanResponse.getShowsPointsAreNotValidated() != TestResults.TRUE) {
                        testForSidechannel(distTest, vector, scanResponse);
                    }
                }
                responses.add(scanResponse);
            }
        }
        return evaluateResponses(responses);
    }

    @Override
    public boolean canBeExecuted(ServerReport report) {
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_SECURE_RENEGOTIATION_EXTENSION)
            == TestResults.NOT_TESTED_YET
            || report.getResult(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_INSECURE_RENEGOTIATION)
                == TestResults.NOT_TESTED_YET
            || !report.isProbeAlreadyExecuted(TlsProbeType.PROTOCOL_VERSION)
            || !report.isProbeAlreadyExecuted(TlsProbeType.CIPHER_SUITE)
            || !report.isProbeAlreadyExecuted(TlsProbeType.NAMED_GROUPS)
            || !report.isProbeAlreadyExecuted(TlsProbeType.RESUMPTION)) {
            return false; // dependency is missing
        } else if (report.getResult(TlsAnalyzedProperty.SUPPORTS_ECDHE) != TestResults.TRUE
            && report.getResult(TlsAnalyzedProperty.SUPPORTS_STATIC_ECDH) != TestResults.TRUE
            && report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS_1_3) != TestResults.TRUE) {
            return false; // can actually not be executed
        } else {
            return true;
        }
    }

    @Override
    public void adjustConfig(ServerReport report) {
        supportsRenegotiation =
            (report.getResult(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_SECURE_RENEGOTIATION_EXTENSION)
                == TestResults.TRUE
                || report.getResult(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_INSECURE_RENEGOTIATION)
                    == TestResults.TRUE);
        supportsSecureRenegotiation =
            report.getResult(TlsAnalyzedProperty.SUPPORTS_CLIENT_SIDE_SECURE_RENEGOTIATION_EXTENSION);
        issuesTls13SessionTickets = report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS13_SESSION_TICKETS);
        supportsTls13PskDhe = report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS13_PSK_DHE);

        supportedFpGroups = new LinkedList<>();
        if (report.getSupportedNamedGroups() != null) {
            for (NamedGroup group : report.getSupportedNamedGroups()) {
                if (NamedGroup.getImplemented().contains(group) && group.isCurve()
                    && CurveFactory.getCurve(group) instanceof EllipticCurveOverFp) {
                    supportedFpGroups.add(group);
                }
            }
        } else {
            LOGGER.warn("Supported Named Groups list has not been initialized");
        }

        HashMap<ProtocolVersion, List<CipherSuite>> cipherSuitesMap = new HashMap<>();
        if (report.getVersionSuitePairs() != null) {
            for (VersionSuiteListPair pair : report.getVersionSuitePairs()) {
                if (!cipherSuitesMap.containsKey(pair.getVersion())) {
                    cipherSuitesMap.put(pair.getVersion(), new LinkedList<>());
                }
                for (CipherSuite cipherSuite : pair.getCipherSuiteList()) {
                    if (cipherSuite.name().contains("TLS_ECDH")) {
                        cipherSuitesMap.get(pair.getVersion()).add(cipherSuite);
                    }
                }

            }
        } else {
            LOGGER.warn("Supported CipherSuites list has not been initialized");
        }

        List<ECPointFormat> fpPointFormats = new LinkedList<>();
        fpPointFormats.add(ECPointFormat.UNCOMPRESSED);
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_UNCOMPRESSED_POINT) != TestResults.TRUE) {
            LOGGER.warn("Server did not list uncompressed points as supported");
        }
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_ANSIX962_COMPRESSED_PRIME) == TestResults.TRUE
            || scanDetail == ScannerDetail.ALL) {
            fpPointFormats.add(ECPointFormat.ANSIX962_COMPRESSED_PRIME);
        }

        List<ProtocolVersion> protocolVersions = new LinkedList<>();
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS_1_0) == TestResults.TRUE) {
            protocolVersions.add(ProtocolVersion.TLS10);
        }
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS_1_1) == TestResults.TRUE) {
            protocolVersions.add(ProtocolVersion.TLS11);
        }
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS_1_2) == TestResults.TRUE) {
            protocolVersions.add(ProtocolVersion.TLS12);
        }
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_DTLS_1_0) == TestResults.TRUE) {
            protocolVersions.add(ProtocolVersion.DTLS10);
        }
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_DTLS_1_2) == TestResults.TRUE) {
            protocolVersions.add(ProtocolVersion.DTLS12);
        }
        supportedTls13FpGroups = new LinkedList();
        if (report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS_1_3) == TestResults.TRUE) {
            protocolVersions.add(ProtocolVersion.TLS13);
            for (NamedGroup group : report.getSupportedTls13Groups()) {
                if (NamedGroup.getImplemented().contains(group) && group.isCurve()
                    && CurveFactory.getCurve(group) instanceof EllipticCurveOverFp) {
                    supportedTls13FpGroups.add(group);
                }
            }

            List<CipherSuite> tls13CipherSuites = new LinkedList<>();
            for (VersionSuiteListPair pair : report.getVersionSuitePairs()) {
                if (pair.getVersion().isTLS13()) {
                    for (CipherSuite cipherSuite : pair.getCipherSuiteList()) {
                        if (cipherSuite.isImplemented()) {
                            tls13CipherSuites.add(cipherSuite);
                        }
                    }
                }
            }

            List<ECPointFormat> tls13FpPointFormats = new LinkedList<>();
            tls13FpPointFormats.add(ECPointFormat.UNCOMPRESSED);
            if (report.getResult(TlsAnalyzedProperty.SUPPORTS_TLS13_SECP_COMPRESSION) == TestResults.TRUE) {
                tls13FpPointFormats.add(ECPointFormat.ANSIX962_COMPRESSED_PRIME);
            }

            cipherSuitesMap.put(ProtocolVersion.TLS13, tls13CipherSuites);
            tls13FpPointFormatsToTest = tls13FpPointFormats;
        }

        // sometimes we found more versions while testing cipher suites
        if (cipherSuitesMap.keySet().size() > protocolVersions.size()) {
            for (ProtocolVersion version : cipherSuitesMap.keySet()) {
                if (!protocolVersions.contains(version)) {
                    protocolVersions.add(version);
                }
            }
        }

        fpPointFormatsToTest = fpPointFormats;
        supportedProtocolVersions = protocolVersions;
        supportedECDHCipherSuites = cipherSuitesMap;
        namedCurveWitnesses = report.getSupportedNamedGroupsWitnesses();
        namedCurveWitnessesTls13 = report.getSupportedNamedGroupsWitnessesTls13();

    }

    @Override
    public InvalidCurveResult getCouldNotExecuteResult() {
        return new InvalidCurveResult(TestResults.COULD_NOT_TEST, TestResults.COULD_NOT_TEST,
            TestResults.COULD_NOT_TEST, null);
    }

    private List<InvalidCurveVector> prepareVectors() {
        LinkedList<InvalidCurveVector> vectors = new LinkedList<>();

        List<ProtocolVersion> pickedProtocolVersions = pickProtocolVersions();
        for (ProtocolVersion protocolVersion : supportedProtocolVersions) {
            List<NamedGroup> groupList;
            List<ECPointFormat> formatList;
            if (protocolVersion == ProtocolVersion.TLS13) {
                groupList = supportedTls13FpGroups;
                formatList = tls13FpPointFormatsToTest;
            } else {
                groupList = supportedFpGroups;
                formatList = fpPointFormatsToTest;
            }
            for (NamedGroup group : groupList) {

                for (ECPointFormat format : formatList) {
                    if (supportedECDHCipherSuites.get(protocolVersion) == null) {
                        LOGGER.warn("Protocol Version " + protocolVersion
                            + " had no entry in CipherSuite map - omitting from InvalidCurve scan");
                    } else {
                        if (scanDetail == ScannerDetail.ALL) {
                            // individual scans for every cipher suite
                            for (CipherSuite cipherSuite : supportedECDHCipherSuites.get(protocolVersion)) {
                                if (legitInvalidCurveVector(group, format)
                                    && groupQualifiedForCipherSuite(group, cipherSuite)) {
                                    vectors.add(new InvalidCurveVector(protocolVersion, cipherSuite, group, format,
                                        false, false, getRequiredGroups(group, cipherSuite)));
                                }
                                if (legitTwistVector(group, format)
                                    && groupQualifiedForCipherSuite(group, cipherSuite)) {
                                    vectors.add(new InvalidCurveVector(protocolVersion, cipherSuite, group, format,
                                        true, false, getRequiredGroups(group, cipherSuite)));
                                }
                            }
                        } else {
                            // reduced list of cipher suites (varying by
                            // ScannerDetail)
                            HashMap<ProtocolVersion, List<CipherSuite>> filteredCipherSuites =
                                filterCipherSuites(group);
                            if (pickedProtocolVersions.contains(protocolVersion)
                                || scanDetail.isGreaterEqualTo(ScannerDetail.DETAILED)) {
                                List<CipherSuite> versionSuiteList = filteredCipherSuites.get(protocolVersion);
                                for (CipherSuite cipherSuite : versionSuiteList) {
                                    if (legitInvalidCurveVector(group, format)) {
                                        vectors.add(new InvalidCurveVector(protocolVersion, cipherSuite, group, format,
                                            false, false, getRequiredGroups(group, cipherSuite)));
                                    }
                                    if (legitTwistVector(group, format) && TwistedCurvePoint.isTwistVulnerable(group)
                                        && TwistedCurvePoint.smallOrder(group).getOrder().intValue()
                                            <= CURVE_TWIST_MAX_ORDER) {
                                        vectors.add(new InvalidCurveVector(protocolVersion, cipherSuite, group, format,
                                            true, false, getRequiredGroups(group, cipherSuite)));
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        // repeat scans in renegotiation
        if (scanDetail.isGreaterEqualTo(ScannerDetail.DETAILED)) {
            ProtocolVersion renegotiationVersion = pickRenegotiationVersion();
            int vectorCount = vectors.size();
            if (scanDetail == ScannerDetail.ALL) {
                // scan all possible combinations in renegotiation
                for (int i = 0; i < vectorCount; i++) {
                    InvalidCurveVector vector = vectors.get(i);
                    if ((vector.getProtocolVersion() == ProtocolVersion.TLS13
                        && (issuesTls13SessionTickets == TestResults.TRUE && supportsTls13PskDhe == TestResults.TRUE))
                        || supportsRenegotiation) {
                        vectors.add(new InvalidCurveVector(vector.getProtocolVersion(), vector.getCipherSuite(),
                            vector.getNamedGroup(), vector.getPointFormat(), vector.isTwistAttack(), true,
                            vector.getEcdsaRequiredGroups()));
                    }
                }
            } else if (renegotiationVersion != null) {
                // scan only one version in renegotiation
                for (int i = 0; i < vectorCount; i++) {
                    InvalidCurveVector vector = vectors.get(i);
                    if (vector.getProtocolVersion() == renegotiationVersion) {
                        vectors.add(new InvalidCurveVector(vector.getProtocolVersion(), vector.getCipherSuite(),
                            vector.getNamedGroup(), vector.getPointFormat(), vector.isTwistAttack(), true,
                            vector.getEcdsaRequiredGroups()));
                    }
                }
            }

        }
        return vectors;
    }

    private InvalidCurveResponse executeSingleScan(InvalidCurveVector vector, InvalidCurveScanType scanType) {
        LOGGER.debug("Executing Invalid Curve scan for " + vector.toString());
        try {
            Config config;
            if (vector.getProtocolVersion().isTLS13()) {
                config = configSelector.getTls13BaseConfig();
            } else {
                config = configSelector.getBaseConfig();
            }
            InvalidCurveAttacker attacker = new InvalidCurveAttacker(config, getParallelExecutor(), vector, scanType,
                getInfinityProbability(vector, scanType));
            Boolean foundCongruence = attacker.isVulnerable();
            TestResult showsPointsAreNotValidated = TestResults.NOT_TESTED_YET;
            if (foundCongruence == null) {
                LOGGER.warn("Was unable to determine if points are validated for " + vector.toString());
                showsPointsAreNotValidated = TestResults.ERROR_DURING_TEST;
            } else if (foundCongruence == true) {
                showsPointsAreNotValidated = TestResults.TRUE;
            } else {
                showsPointsAreNotValidated = TestResults.FALSE;
            }
            TestResult dirtyKeysWarning;
            if (attacker.isDirtyKeysWarning()) {
                dirtyKeysWarning = TestResults.TRUE;
            } else {
                dirtyKeysWarning = TestResults.FALSE;
            }
            return new InvalidCurveResponse(vector, attacker.getResponsePairs(), showsPointsAreNotValidated,
                attacker.getReceivedEcPublicKeys(), attacker.getFinishedKeys(), dirtyKeysWarning, scanType);
        } catch (Exception e) {
            if (e.getCause() instanceof InterruptedException) {
                LOGGER.error("Timeout on " + getProbeName());
                throw new RuntimeException(e);
            } else {
                LOGGER.warn("Was unable to get results for " + vector.toString() + " Message: " + e.getMessage());
            }
            return new InvalidCurveResponse(vector, TestResults.ERROR_DURING_TEST);
        }
    }

    private InvalidCurveResult evaluateResponses(List<InvalidCurveResponse> responses) {
        TestResult vulnerableClassic = TestResults.FALSE;
        TestResult vulnerableEphemeral = TestResults.FALSE;
        TestResult vulnerableTwist = TestResults.FALSE;

        evaluateKeyBehavior(responses);

        for (InvalidCurveResponse response : responses) {
            if (response.getShowsPointsAreNotValidated() == TestResults.TRUE
                && response.getChosenGroupReusesKey() == TestResults.TRUE) {
                if (response.getVector().isTwistAttack()
                    && TwistedCurvePoint.isTwistVulnerable(response.getVector().getNamedGroup())) {
                    response.setShowsVulnerability(TestResults.TRUE);
                    vulnerableTwist = TestResults.TRUE;
                } else if (!response.getVector().isTwistAttack()) {
                    response.setShowsVulnerability(TestResults.TRUE);
                    if (response.getVector().getCipherSuite().isEphemeral()) {
                        vulnerableEphemeral = TestResults.TRUE;
                    } else {
                        vulnerableClassic = TestResults.TRUE;
                    }
                }
            } else {
                response.setShowsVulnerability(TestResults.FALSE);
            }
        }

        return new InvalidCurveResult(vulnerableClassic, vulnerableEphemeral, vulnerableTwist, responses);
    }

    private void evaluateKeyBehavior(List<InvalidCurveResponse> responses) {
        for (InvalidCurveResponse response : responses) {
            if (response.getReceivedEcPublicKeys() == null || response.getReceivedEcPublicKeys().isEmpty()) {
                response.setChosenGroupReusesKey(TestResults.ERROR_DURING_TEST);
            } else {
                TestResult foundDuplicate = TestResults.FALSE;
                TestResult foundDuplicateFinished = TestResults.FALSE;
                for (Point point : response.getReceivedEcPublicKeys()) {
                    for (Point pointC : response.getReceivedEcPublicKeys()) {
                        if (point != pointC
                            && (point.getFieldX().getData().compareTo(pointC.getFieldX().getData()) == 0)
                            && point.getFieldY().getData().compareTo(pointC.getFieldY().getData()) == 0) {
                            foundDuplicate = TestResults.TRUE;
                        }
                    }
                }

                // Compare again for keys from handshakes that lead to a
                // Finished message
                for (Point point : response.getReceivedFinishedEcKeys()) {
                    for (Point pointC : response.getReceivedEcPublicKeys()) {
                        if (point != pointC
                            && (point.getFieldX().getData().compareTo(pointC.getFieldX().getData()) == 0)
                            && point.getFieldY().getData().compareTo(pointC.getFieldY().getData()) == 0) {
                            foundDuplicateFinished = TestResults.TRUE;
                        }
                    }
                }
                response.setChosenGroupReusesKey(foundDuplicate);
                response.setFinishedHandshakeHadReusedKey(foundDuplicateFinished);
            }
        }
    }

    private boolean legitInvalidCurveVector(NamedGroup group, ECPointFormat format) {
        if (format != ECPointFormat.UNCOMPRESSED) {
            return false; // not applicable for compressed point
        } else if (group == NamedGroup.ECDH_X25519 || group == NamedGroup.ECDH_X448) {
            return false; // not applicable for compressed point
        } else if (InvalidCurvePoint.smallOrder(group) == null) {
            return false; // no suitable point configured
        } else {
            return true;
        }
    }

    private boolean legitTwistVector(NamedGroup group, ECPointFormat format) {
        if (TwistedCurvePoint.smallOrder(group) == null) {
            return false; // no suitable point configured
        } else if (format == ECPointFormat.ANSIX962_COMPRESSED_PRIME
            && (group == NamedGroup.ECDH_X25519 || group == NamedGroup.ECDH_X448)) {
            // X-curves are neither uncompressed nor ANSIX962, we schedule them
            // as uncompressed as it is the default format (format is ignored)
            return false;
        } else {
            return true;
        }
    }

    /**
     * Picks one version for which we run scans in renegotiation
     */
    private ProtocolVersion pickRenegotiationVersion() {
        if (supportedProtocolVersions.contains(ProtocolVersion.TLS12) && supportsRenegotiation) {
            return ProtocolVersion.TLS12;
        } else if (supportedProtocolVersions.contains(ProtocolVersion.TLS11) && supportsRenegotiation) {
            return ProtocolVersion.TLS11;
        } else if (supportedProtocolVersions.contains(ProtocolVersion.TLS10) && supportsRenegotiation) {
            return ProtocolVersion.TLS10;
        } else if (supportedProtocolVersions.contains(ProtocolVersion.TLS13)
            && (issuesTls13SessionTickets == TestResults.TRUE && supportsTls13PskDhe == TestResults.TRUE)) {
            return ProtocolVersion.TLS13;
        } else if (supportedProtocolVersions.contains(ProtocolVersion.DTLS12) && supportsRenegotiation) {
            return ProtocolVersion.DTLS12;
        } else if (supportedProtocolVersions.contains(ProtocolVersion.DTLS10) && supportsRenegotiation) {
            return ProtocolVersion.DTLS10;
        }
        LOGGER.info("Could not find a suitable version for Invalid Curve renegotiation scans");
        return null;
    }

    /**
     * Select highest pre-Tls13 version and Tls13 if available
     */
    private List<ProtocolVersion> pickProtocolVersions() {
        List<ProtocolVersion> picked = new LinkedList<>();
        if (supportedProtocolVersions.contains(ProtocolVersion.TLS12)) {
            picked.add(ProtocolVersion.TLS12);
        } else if (supportedProtocolVersions.contains(ProtocolVersion.TLS11)) {
            picked.add(ProtocolVersion.TLS11);
        } else if (supportedProtocolVersions.contains(ProtocolVersion.TLS10)) {
            picked.add(ProtocolVersion.TLS10);
        }

        if (supportedProtocolVersions.contains(ProtocolVersion.TLS13)) {
            picked.add(ProtocolVersion.TLS13);
        }

        if (supportedProtocolVersions.contains(ProtocolVersion.DTLS12)) {
            picked.add(ProtocolVersion.DTLS12);
        } else if (supportedProtocolVersions.contains(ProtocolVersion.DTLS10)) {
            picked.add(ProtocolVersion.DTLS10);
        }
        return picked;
    }

    /**
     * Groups cipher suites per Version in a hopefully sensible way that reduces the probe count but still provides
     * enough accuracy
     */
    private HashMap<ProtocolVersion, List<CipherSuite>> filterCipherSuites(NamedGroup group) {
        HashMap<ProtocolVersion, List<CipherSuite>> groupedMap = new HashMap<>();
        for (ProtocolVersion protocolVersion : supportedProtocolVersions) {
            List<CipherSuite> coveredSuites = new LinkedList<CipherSuite>();
            boolean gotStatic = false;
            boolean gotEphemeral = false;
            boolean gotGCM = false;
            boolean gotCBC = false;
            boolean gotSHA = false;
            boolean gotSHA256 = false;
            boolean gotSHA384 = false;
            boolean gotSHA512 = false;
            boolean gotECDSA = false;
            boolean gotRSA = false;
            boolean gotWeak = false; // very wide ranged
            if (supportedECDHCipherSuites.get(protocolVersion) != null) {
                for (CipherSuite cipherSuite : supportedECDHCipherSuites.get(protocolVersion)) {
                    boolean addCandidate = false;
                    if (groupQualifiedForCipherSuite(group, cipherSuite)) {
                        if (!cipherSuite.isEphemeral() && gotStatic == false) {
                            addCandidate = true;
                            gotStatic = true;
                        }
                        if (cipherSuite.isEphemeral() && gotEphemeral == false) {
                            addCandidate = true;
                            gotEphemeral = true;
                        }

                        if (scanDetail.isGreaterEqualTo(ScannerDetail.DETAILED)) {
                            if (cipherSuite.isGCM() && gotGCM == false) {
                                addCandidate = true;
                                gotGCM = true;
                            } else if (cipherSuite.isCBC() && gotCBC == false) {
                                addCandidate = true;
                                gotCBC = true;
                            }

                            if (cipherSuite.isSHA() && gotSHA == false) {
                                addCandidate = true;
                                gotSHA = true;
                            } else if (cipherSuite.isSHA256() && gotSHA256 == false) {
                                addCandidate = true;
                                gotSHA256 = true;
                            } else if (cipherSuite.isSHA384() && gotSHA384 == false) {
                                addCandidate = true;
                                gotSHA384 = true;
                            } else if (cipherSuite.isSHA512() && gotSHA512 == false) {
                                addCandidate = true;
                                gotSHA512 = true;
                            }

                            if (cipherSuite.isECDSA() && gotECDSA == false) {
                                addCandidate = true;
                                gotECDSA = true;
                            } else if (cipherSuite.name().contains("RSA") && gotRSA == false) {
                                addCandidate = true;
                                gotRSA = true;
                            }

                            if (cipherSuite.isWeak() && gotWeak == false) {
                                addCandidate = true;
                                gotWeak = true;
                            }
                        }
                        if (addCandidate) {
                            coveredSuites.add(cipherSuite);
                        }
                    }

                }
            }
            groupedMap.put(protocolVersion, coveredSuites);
        }

        return groupedMap;
    }

    private boolean groupQualifiedForCipherSuite(NamedGroup testGroup, CipherSuite testCipher) {
        if (!testCipher.isTLS13()) {
            if (namedCurveWitnesses.containsKey(testGroup) == false) {
                return false;
            } else if ((AlgorithmResolver.getCertificateKeyType(testCipher) == CertificateKeyType.RSA
                && !namedCurveWitnesses.get(testGroup).isFoundUsingRsaCipher())
                || (AlgorithmResolver.getKeyExchangeAlgorithm(testCipher) == KeyExchangeAlgorithm.ECDHE_ECDSA
                    && !namedCurveWitnesses.get(testGroup).isFoundUsingEcdsaEphemeralCipher())
                || (AlgorithmResolver.getKeyExchangeAlgorithm(testCipher) == KeyExchangeAlgorithm.ECDH_ECDSA
                    && !namedCurveWitnesses.get(testGroup).isFoundUsingEcdsaStaticCipher())) {
                return false;
            }
        }
        return true;
    }

    private List<NamedGroup> getRequiredGroups(NamedGroup testGroup, CipherSuite testCipher) {
        Set<NamedGroup> requiredGroups = new HashSet<>();
        if (testCipher.isTLS13()) {
            if (namedCurveWitnessesTls13.get(testGroup).getEcdsaPkGroupEphemeral() != null
                && namedCurveWitnessesTls13.get(testGroup).getEcdsaPkGroupEphemeral() != testGroup) {
                requiredGroups.add(namedCurveWitnessesTls13.get(testGroup).getEcdsaPkGroupEphemeral());
            }
            if (namedCurveWitnessesTls13.get(testGroup).getEcdsaSigGroupEphemeral() != null
                && namedCurveWitnessesTls13.get(testGroup).getEcdsaSigGroupEphemeral() != testGroup) {
                requiredGroups.add(namedCurveWitnessesTls13.get(testGroup).getEcdsaSigGroupEphemeral());
            }
        } else {
            // RSA cipher suites don't require any additional groups
            if (AlgorithmResolver.getKeyExchangeAlgorithm(testCipher) == KeyExchangeAlgorithm.ECDHE_ECDSA) {
                if (namedCurveWitnesses.get(testGroup).getEcdsaPkGroupEphemeral() != null
                    && namedCurveWitnesses.get(testGroup).getEcdsaPkGroupEphemeral() != testGroup) {
                    requiredGroups.add(namedCurveWitnesses.get(testGroup).getEcdsaPkGroupEphemeral());
                }
                if (namedCurveWitnesses.get(testGroup).getEcdsaSigGroupEphemeral() != null
                    && namedCurveWitnesses.get(testGroup).getEcdsaSigGroupEphemeral() != testGroup) {
                    requiredGroups.add(namedCurveWitnesses.get(testGroup).getEcdsaSigGroupEphemeral());
                }
            } else if (AlgorithmResolver.getKeyExchangeAlgorithm(testCipher) == KeyExchangeAlgorithm.ECDH_ECDSA) {
                if (namedCurveWitnesses.get(testGroup).getEcdsaSigGroupStatic() != null
                    && namedCurveWitnesses.get(testGroup).getEcdsaSigGroupStatic() != testGroup) {
                    requiredGroups.add(namedCurveWitnesses.get(testGroup).getEcdsaSigGroupStatic());
                }
            }
        }
        return new LinkedList<>(requiredGroups);
    }

    private boolean benignHandshakeSuccessful(InvalidCurveVector vector) {
        Config tlsConfig;
        if (vector.getProtocolVersion().isTLS13()) {
            tlsConfig = configSelector.getTls13BaseConfig();
            List<NamedGroup> keyShareGroups = new LinkedList<>();
            keyShareGroups.add(vector.getNamedGroup());
            tlsConfig.setDefaultClientKeyShareNamedGroups(keyShareGroups);
            tlsConfig.setAddPSKKeyExchangeModesExtension(true);
            List<PskKeyExchangeMode> pskKex = new LinkedList<>();
            pskKex.add(PskKeyExchangeMode.PSK_DHE_KE);
            tlsConfig.setPSKKeyExchangeModes(pskKex);
        } else {
            tlsConfig = configSelector.getBaseConfig();
        }
        tlsConfig.setHighestProtocolVersion(vector.getProtocolVersion());
        tlsConfig.setDefaultClientSupportedCipherSuites(vector.getCipherSuite());
        tlsConfig.setDefaultClientNamedGroups(vector.getNamedGroup());
        // avoid cases where the server requires an additional group
        // to sign a PK of our test group using ECDSA
        if (!vector.getEcdsaRequiredGroups().isEmpty()) {
            tlsConfig.getDefaultClientNamedGroups().addAll(vector.getEcdsaRequiredGroups());
        }
        tlsConfig.setAddRenegotiationInfoExtension(
            supportsSecureRenegotiation == TestResults.FALSE && supportsRenegotiation);
        tlsConfig.setWorkflowTraceType(WorkflowTraceType.DYNAMIC_HANDSHAKE);

        State state = new State(tlsConfig);
        executeState(state);

        if (!state.getWorkflowTrace().executedAsPlanned()) {
            LOGGER.warn("Benign handshake failed for " + vector.toString() + " - omitting from Invalid Curve");
            return false;
        } else if (state.getTlsContext().getSelectedGroup() != vector.getNamedGroup()) {
            LOGGER.warn("Benign handshake used wrong group (" + state.getTlsContext().getSelectedGroup() + ") for "
                + vector.toString() + " - omitting from Invalid Curve");
            return false;
        }

        return true;
    }

    private double getInfinityProbability(InvalidCurveVector vector, InvalidCurveScanType scanType) {
        double order;

        if (scanType == InvalidCurveScanType.REDUNDANT) {
            if (vector.isTwistAttack()) {
                order = TwistedCurvePoint.alternativeOrder(vector.getNamedGroup()).getOrder().doubleValue();
            } else {
                order = InvalidCurvePoint.alternativeOrder(vector.getNamedGroup()).getOrder().doubleValue();
            }
        } else if (scanType == InvalidCurveScanType.LARGE_GROUP) {
            if (vector.isTwistAttack()) {
                order = TwistedCurvePoint.largeOrder(vector.getNamedGroup()).getOrder().doubleValue();
            } else {
                order = InvalidCurvePoint.largeOrder(vector.getNamedGroup()).getOrder().doubleValue();
            }
        } else {
            if (vector.isTwistAttack()) {
                order = TwistedCurvePoint.smallOrder(vector.getNamedGroup()).getOrder().doubleValue();
            } else {
                order = InvalidCurvePoint.smallOrder(vector.getNamedGroup()).getOrder().doubleValue();
            }
        }

        return (double) (1 / order);
    }

    private void testForSidechannel(DistributionTest initialTest, InvalidCurveVector vector,
        InvalidCurveResponse initialResponse) {
        initialResponse.setHadDistinctFps(TestResults.TRUE);
        InvalidCurveResponse largeGroupResponse = executeSingleScan(vector, InvalidCurveScanType.LARGE_GROUP);
        if (!largeGroupResponse.getVectorResponses().isEmpty()) {
            DistributionTest rejectionDistTest =
                new DistributionTest(new InvalidCurveTestInfo(vector), largeGroupResponse.getVectorResponses(),
                    getInfinityProbability(vector, InvalidCurveScanType.LARGE_GROUP));
            if (rejectionDistTest.isDistinctAnswers() == false) {
                InvalidCurveResponse extendedResponse = executeSingleScan(vector, InvalidCurveScanType.EXTENDED);
                initialTest.extendTestWithVectorResponses(extendedResponse.getVectorResponses());
                initialResponse.mergeResponse(extendedResponse);

                if (initialTest.isSignificantDistinctAnswers() == false && initialResponse.getVectorResponses().size()
                    >= (initialResponse.getFingerprintSecretPairs().size() / 2)) {
                    if (scanDetail == ScannerDetail.ALL) {
                        // perform second test immediately
                        InvalidCurveResponse redundantResponse =
                            executeSingleScan(vector, InvalidCurveScanType.REDUNDANT);
                        if (!redundantResponse.getVectorResponses().isEmpty()) {
                            DistributionTest redundantDistTest = new DistributionTest(new InvalidCurveTestInfo(vector),
                                redundantResponse.getVectorResponses(),
                                getInfinityProbability(vector, InvalidCurveScanType.REDUNDANT));
                            if (redundantDistTest.isDistinctAnswers()
                                && redundantDistTest.isSignificantDistinctAnswers()) {
                                initialResponse.setSideChannelSuspected(TestResults.TRUE);
                            }
                        }
                    } else {
                        initialResponse.setSideChannelSuspected(TestResults.TRUE);
                    }
                }
            }
        }
    }
}
