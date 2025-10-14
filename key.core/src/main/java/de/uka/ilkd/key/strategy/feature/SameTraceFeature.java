package de.uka.ilkd.key.strategy.feature;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.strategy.NumberRuleAppCost;
import de.uka.ilkd.key.strategy.RuleAppCost;
import de.uka.ilkd.key.strategy.TopRuleAppCost;
import de.uka.ilkd.key.strategy.termProjection.ProjectionToTerm;

public class SameTraceFeature implements Feature {
    private final ProjectionToTerm fullTrace;
    private final ProjectionToTerm prefix;
    private final ProjectionToTerm postfix;

    public SameTraceFeature(ProjectionToTerm fullTrace1, ProjectionToTerm prefix1, ProjectionToTerm postfix1) {
        this.fullTrace = fullTrace1;
        this.prefix = prefix1;
        this.postfix = postfix1;
    }

    @Override
    public RuleAppCost computeCost(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        var fullTraceInst = fullTrace.toTerm(app, pos, goal, mState);
        var prefixInst = prefix.toTerm(app, pos, goal, mState);
        var postfixInst = postfix.toTerm(app, pos, goal, mState);
        var services = goal.proof().getServices();


        if(app instanceof TacletApp tacletApp) {

            TraceManager traceTM   = new TraceManager(fullTraceInst, tacletApp.instantiations(), services);
            TraceManager prefixTM  = new TraceManager(prefixInst, tacletApp.instantiations(), services);
            TraceManager postfixTM = new TraceManager(postfixInst, tacletApp.instantiations(), services);
            if (app.rule().name().compareTo(new Name("elimPrefixNoSchemTr")) == 0) {
                return NumberRuleAppCost.getZeroCost();
            }
            if (app.rule().name().compareTo(new Name("elimPrefixWithSchemTrChop")) == 0 ||
                    app.rule().name().compareTo(new Name("elimPrefixNoSchemTrChop")) == 0) {
                if (traceTM.equals(prefixTM.chop(postfixTM)))
                    return NumberRuleAppCost.getZeroCost();
            } else if (app.rule().name().compareTo(new Name("elimPrefixWithSchemTrConc")) == 0||
                    app.rule().name().compareTo(new Name("elimPrefixNoSchemTrConc")) == 0) {
                if (traceTM.equals(prefixTM.conc(postfixTM)))
                    return NumberRuleAppCost.getZeroCost();
            }
        }
        return TopRuleAppCost.INSTANCE;

    }
}
