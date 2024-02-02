package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.JavaBlock;
import de.uka.ilkd.key.logic.ProgramElementName;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FinishCondition implements VariableCondition {


    private final SchemaVariable u;

    public FinishCondition(SchemaVariable u) {
        this.u = u;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions mc, Services services) {

        SVInstantiations svInst = mc.getInstantiations();
        Term update = (Term) svInst.getInstantiation(u);

        if (update == null) {
            return mc;
        }
        Term idlingInvoEv = PendingInvocationCondition.getSchedule(update,services);
        if(idlingInvoEv != null)
            return null;
        return mc;
    }
}
