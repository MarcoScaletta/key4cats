package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.Observation;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
/**
 * @author Marco Scaletta
 */
public class IsObservation extends VariableConditionAdapter {


    private final SchemaVariable observation;
    private final boolean negated;


    public IsObservation(SchemaVariable observation, boolean negated) {
        this.observation = observation;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations svInst, Services services) {
        Term observationTerm = (Term) svInst.getInstantiation(observation);
        boolean isObservation = observationTerm != null && observationTerm.op() instanceof Observation obs;
        return isObservation != this.negated;
    }

}
