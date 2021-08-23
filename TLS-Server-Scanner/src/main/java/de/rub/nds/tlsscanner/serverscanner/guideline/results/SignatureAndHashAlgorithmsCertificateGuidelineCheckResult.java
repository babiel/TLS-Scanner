/**
 * TLS-Server-Scanner - A TLS configuration and analysis tool based on TLS-Attacker
 *
 * Copyright 2017-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsscanner.serverscanner.guideline.results;

import com.google.common.base.Joiner;
import de.rub.nds.tlsattacker.core.constants.SignatureAndHashAlgorithm;
import de.rub.nds.tlsscanner.serverscanner.guideline.GuidelineCheckResult;
import de.rub.nds.tlsscanner.serverscanner.rating.TestResult;

import java.util.Set;

public class SignatureAndHashAlgorithmsCertificateGuidelineCheckResult extends GuidelineCheckResult {

    private final Set<SignatureAndHashAlgorithm> nonRecommendedAlgorithms;

    public SignatureAndHashAlgorithmsCertificateGuidelineCheckResult(TestResult result,
        Set<SignatureAndHashAlgorithm> nonRecommendedAlgorithms) {
        super(result);
        this.nonRecommendedAlgorithms = nonRecommendedAlgorithms;
    }

    @Override
    public String display() {
        if (TestResult.UNCERTAIN.equals(getResult())) {
            return "Missing Information";
        }
        if (TestResult.TRUE.equals(getResult())) {
            return "Only listed Signature and Hash Algorithms are supported.";
        } else {
            return "The following Signature and Hash Algorithms were supported but not recommended:\n"
                + Joiner.on('\n').join(nonRecommendedAlgorithms);
        }
    }

    public Set<SignatureAndHashAlgorithm> getNonRecommendedAlgorithms() {
        return nonRecommendedAlgorithms;
    }
}
