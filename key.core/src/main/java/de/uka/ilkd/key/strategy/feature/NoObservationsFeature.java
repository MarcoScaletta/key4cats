package de.uka.ilkd.key.strategy.feature;

import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.conditions.catsconditions.ContainsObservations;
import de.uka.ilkd.key.strategy.NumberRuleAppCost;
import de.uka.ilkd.key.strategy.RuleAppCost;
import de.uka.ilkd.key.strategy.TopRuleAppCost;
import de.uka.ilkd.key.strategy.termProjection.ProjectionToTerm;

public class NoObservationsFeature implements Feature {
    private final ProjectionToTerm trace;

    public NoObservationsFeature(ProjectionToTerm trace) {
        this.trace = trace;
    }

    @Override
    public RuleAppCost computeCost(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        var traceInst = trace.toTerm(app, pos, goal, mState);
        return ContainsObservations.containsObservations(traceInst) ?
                TopRuleAppCost.INSTANCE :
                NumberRuleAppCost.getZeroCost();
    }
}
