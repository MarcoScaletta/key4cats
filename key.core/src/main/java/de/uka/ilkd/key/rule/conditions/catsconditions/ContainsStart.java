package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.UpdateManager;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

import java.util.List;
import java.util.Objects;

/**
 * @author Marco Scaletta
 */
public class ContainsStart extends VariableConditionAdapter {


    private final TraceResolver traceResolver;
    private final boolean negated;

    public ContainsStart(TraceResolver traceResolver, boolean negated) {
        this.traceResolver = traceResolver;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations instMap, Services services) {

        if(!traceResolver.isVarInstantiated(instMap))
            return true;
        Term updateTerm = traceResolver.resolve(instMap,services);
        return negated == (FinishCondition.getStartEvent(updateTerm, services) == null);
    }

}
