package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
/**
 * @author Marco Scaletta
 */
public class IsSchematicTrace extends VariableConditionAdapter {


    private final TraceResolver traceResolver;
    private final boolean negated;


    public IsSchematicTrace(TraceResolver traceResolver, boolean negated) {
        this.traceResolver = traceResolver;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations instMap, Services services) {
        if(!traceResolver.isVarInstantiated(instMap,services))
            return true;
        Term formulaTerm = traceResolver.resolve(instMap,services);
        return negated != formulaTerm.op() instanceof SchematicTrace;
    }

}
