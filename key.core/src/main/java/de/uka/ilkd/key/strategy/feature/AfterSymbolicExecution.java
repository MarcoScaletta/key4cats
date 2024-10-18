package de.uka.ilkd.key.strategy.feature;

import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.op.Modality;
import de.uka.ilkd.key.logic.op.UpdateApplication;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.strategy.NumberRuleAppCost;
import de.uka.ilkd.key.strategy.RuleAppCost;
import de.uka.ilkd.key.strategy.TopRuleAppCost;

public class AfterSymbolicExecution implements Feature {

    @Override
    public RuleAppCost computeCost(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        return goal.sequent().succedent().asList().stream().noneMatch(
                x ->
                        x.formula().op() instanceof UpdateApplication
                        &&  x.formula().sub(1).op() instanceof Modality mod
                        && (mod == Modality.BOX || mod == Modality.BOX_TRANSACTION)
        ) ?  NumberRuleAppCost.getZeroCost() : TopRuleAppCost.INSTANCE;

    }
}
