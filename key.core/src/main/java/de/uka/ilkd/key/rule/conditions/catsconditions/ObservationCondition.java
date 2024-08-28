package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
/**
 * @author Marco Scaletta
 */
public class ObservationCondition implements VariableCondition {


    private final SchemaVariable observation;
    private final SchemaVariable observedSV;
    private final SchemaVariable observingSV;

    public ObservationCondition(SchemaVariable observation,SchemaVariable observedSV, SchemaVariable observingSV) {
        this.observation = observation;
        this.observedSV = observedSV;
        this.observingSV = observingSV;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        Term observationTerm = (Term) svInst.getInstantiation(observation);
        if (observationTerm == null)
            return matchCond;
        if(!(observationTerm.op() instanceof  Observation obs)) {
            return null;
        }
        Term observedVar = services.getTermFactory().createTerm(obs.observed());
        Term observingTerm = observationTerm.sub(0);

        return matchCond.setInstantiations(svInst.add(observingSV,observingTerm,services).add(observedSV, observedVar, services));
    }
}
