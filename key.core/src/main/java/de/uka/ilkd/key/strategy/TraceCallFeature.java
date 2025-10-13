package de.uka.ilkd.key.strategy;

import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.proof.Node;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.strategy.feature.AppliedRuleAppsNameCache;
import de.uka.ilkd.key.strategy.feature.Feature;
import de.uka.ilkd.key.strategy.feature.MutableState;

import java.util.List;

/**
 * {@code aaa}
 **/
// to be implemented
//
public class TraceCallFeature implements Feature {
    @Override
    public RuleAppCost computeCost(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        final Node node = goal.node();

        Node current = node;
        while (current.parent() != null) {
            current = current.parent();
            RuleApp currentApp = current.getAppliedRuleApp();
            // check my app

        }

        final AppliedRuleAppsNameCache cache =
                node.proof().getServices().getCaches().getAppliedRuleAppsNameCache();
        List<RuleApp> apps = cache.get(node, app.rule().name());

        // Check all rules with this name
        for (RuleApp a : apps) {

//            if (sameApplication(a, app, pos)) {
//                return false;
//            }
        }

        return null;
    }
}
