package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.TraceEvent;
import de.uka.ilkd.key.logic.op.UpdateEvent;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class IsEventWithMethod extends VariableConditionAdapter  {


    private final SchemaVariable termSV;
    private final boolean negated;


    public IsEventWithMethod(SchemaVariable term, boolean negated) {
        this.termSV = term;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations instMap, Services services) {

        Term term = (Term) instMap.getInstantiation(termSV);
        if(term == null || termSV == null)
            return true;

        if((termSV.sort() == Sort.FORMULA && term.op() instanceof TraceEvent) ||
                (termSV.sort() == Sort.UPDATE && term.op() instanceof UpdateEvent))
            return negated != term.sub(0).sort().name().equals(new Name("MethodName"));
        return negated;
    }

}
