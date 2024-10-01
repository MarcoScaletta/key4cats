package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.TraceEvent;
import de.uka.ilkd.key.logic.op.UpdateEvent;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class IsEvent implements VariableCondition {


    private final SchemaVariable termSV;


    public IsEvent(SchemaVariable term) {
        this.termSV = term;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();

        Term term = (Term) svInst.getInstantiation(termSV);
        if((termSV.sort() == Sort.FORMULA && term.op() instanceof TraceEvent) ||
                (termSV.sort() == Sort.UPDATE && term.op() instanceof UpdateEvent))
            return matchCond;
        return null;
    }
}
