package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
/**
 * @author Marco Scaletta
 */
public class ContainsObservations extends VariableConditionAdapter {


    private final SchemaVariable observation;

    private final boolean negated;

    public ContainsObservations(SchemaVariable observation, boolean negated) {
        this.observation = observation;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations svInst, Services services) {
        Term formula = (Term) svInst.getInstantiation(observation);
        return containsObservations(formula) != negated;
    }

    public boolean containsObservations(Term formula){
        if(formula == null)
            return false;
        if(formula.op() == Junctor.CHOP || formula.op() == Junctor.CONC)
            return containsObservations(formula.sub(0)) || containsObservations(formula.sub(1));
        return (formula.op() instanceof Observation);
    }
}
