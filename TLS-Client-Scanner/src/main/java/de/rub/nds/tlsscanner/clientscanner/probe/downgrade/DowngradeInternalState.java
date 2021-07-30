/**
 * TLS-Client-Scanner - A TLS configuration and analysis tool based on TLS-Attacker
 *
 * Copyright 2017-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsscanner.clientscanner.probe.downgrade;

import de.rub.nds.tlsattacker.core.protocol.message.ClientHelloMessage;
import de.rub.nds.tlsscanner.clientscanner.probe.BaseStatefulProbe;
import de.rub.nds.tlsscanner.clientscanner.probe.Probe;
import de.rub.nds.tlsscanner.clientscanner.report.result.ClientProbeResult;

public class DowngradeInternalState implements BaseStatefulProbe.InternalProbeState {
    protected ClientHelloMessage firstCHLO;
    protected ClientHelloMessage secondCHLO;
    protected final Class<? extends Probe> clazz;

    public DowngradeInternalState(Class<? extends Probe> clazz) {
        this.clazz = clazz;
    }

    public void putCHLO(ClientHelloMessage chlo) {
        if (!isFirstDone()) {
            firstCHLO = chlo;
        } else if (!isDone()) {
            secondCHLO = chlo;
        } else {
            throw new IllegalStateException("Got more than two client hellos");
        }
    }

    public boolean isFirstDone() {
        return firstCHLO != null;
    }

    @Override
    public boolean isDone() {
        return firstCHLO != null && secondCHLO != null;
    }

    @Override
    public ClientProbeResult toResult() {
        return new DowngradeResult(clazz, firstCHLO, secondCHLO);
    }
}
