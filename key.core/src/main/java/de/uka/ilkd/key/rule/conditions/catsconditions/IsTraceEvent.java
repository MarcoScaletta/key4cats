package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.TraceEvent;
import de.uka.ilkd.key.logic.op.UpdateEvent;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class IsTraceEvent implements VariableCondition {


    private final SchemaVariable formulaSV;


    public IsTraceEvent(SchemaVariable formula) {
        this.formulaSV = formula;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();

        Term formulaTerm = (Term) svInst.getInstantiation(formulaSV);
        if(formulaTerm.op() instanceof TraceEvent)
            return matchCond;
        return null;
    }
}
