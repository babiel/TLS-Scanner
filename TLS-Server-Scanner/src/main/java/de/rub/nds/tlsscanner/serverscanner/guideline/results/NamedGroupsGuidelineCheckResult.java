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
import de.rub.nds.tlsattacker.core.constants.NamedGroup;
import de.rub.nds.tlsscanner.serverscanner.guideline.GuidelineCheckResult;
import de.rub.nds.tlsscanner.serverscanner.rating.TestResult;

import java.util.List;
import java.util.Set;

public class NamedGroupsGuidelineCheckResult extends GuidelineCheckResult {

    private Set<NamedGroup> nonRecommendedGroups;
    private List<NamedGroup> missingRequired;
    private Integer groupCount;

    public NamedGroupsGuidelineCheckResult(TestResult result) {
        super(result);
    }

    public NamedGroupsGuidelineCheckResult(TestResult result, Set<NamedGroup> nonRecommendedGroups) {
        super(result);
        this.nonRecommendedGroups = nonRecommendedGroups;
    }

    public NamedGroupsGuidelineCheckResult(TestResult result, List<NamedGroup> missingRequired) {
        super(result);
        this.missingRequired = missingRequired;
    }

    public NamedGroupsGuidelineCheckResult(TestResult result, Integer groupCount) {
        super(result);
        this.groupCount = groupCount;
    }

    @Override
    public String display() {
        if (TestResult.UNCERTAIN.equals(getResult())) {
            return "Missing information.";
        }
        if (TestResult.TRUE.equals(getResult())) {
            return "Only listed groups are supported.";
        }
        if (nonRecommendedGroups != null && !nonRecommendedGroups.isEmpty()) {
            return "The following groups were supported but not recommended:\n"
                + Joiner.on('\n').join(nonRecommendedGroups);
        }
        if (missingRequired != null && !missingRequired.isEmpty()) {
            return "Server is missing one of required groups::\n" + Joiner.on('\n').join(missingRequired);
        }
        if (groupCount != null) {
            return "Server only supports " + groupCount + " groups.";
        }
        return null;
    }

    public Set<NamedGroup> getNonRecommendedGroups() {
        return nonRecommendedGroups;
    }

    public List<NamedGroup> getMissingRequired() {
        return missingRequired;
    }

    public Integer getGroupCount() {
        return groupCount;
    }
}
