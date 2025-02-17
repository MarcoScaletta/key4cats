package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.UpdateEvent;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class IsRunEvent extends VariableConditionAdapter  {


    private final TraceResolver traceResolver;
    private final boolean negated;


    public IsRunEvent(TraceResolver traceResolver, boolean negated) {
        this.traceResolver = traceResolver;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations instMap, Services services) {

        if(traceResolver == null)
            return true;
        if(!traceResolver.isVarInstantiated(instMap))
            return true;

        Term term = traceResolver.resolve(instMap,services);

        if(term != null && term.op() instanceof UpdateEvent && term.op() == UpdateEvent.RUN_EV)
            return !negated;
        return negated;
    }

}
