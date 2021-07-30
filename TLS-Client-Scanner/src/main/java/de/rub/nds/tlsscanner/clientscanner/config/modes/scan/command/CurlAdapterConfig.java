/**
 * TLS-Client-Scanner - A TLS configuration and analysis tool based on TLS-Attacker
 *
 * Copyright 2017-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsscanner.clientscanner.config.modes.scan.command;

import com.beust.jcommander.Parameters;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsscanner.clientscanner.client.adapter.ClientAdapter;
import de.rub.nds.tlsscanner.clientscanner.client.adapter.command.CurlAdapter;
import de.rub.nds.tlsscanner.clientscanner.config.ClientScannerConfig;

@Parameters(commandNames = "curl", commandDescription = "Use a curl based client")
public class CurlAdapterConfig extends BaseCommandAdapterConfig {
    @Override
    protected void applyDelegateInternal(Config config) {
        // nothing to do
    }

    @Override
    public ClientAdapter createClientAdapter(ClientScannerConfig csConfig) {
        return new CurlAdapter(createCommandExecutor(csConfig), csConfig);
    }

}
