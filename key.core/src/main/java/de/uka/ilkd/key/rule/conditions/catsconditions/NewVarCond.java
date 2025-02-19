package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.Observation;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class NewVarCond implements VariableCondition {
    private final SchemaVariable phiSv;
    public NewVarCond(SchemaVariable phiSv) {
        this.phiSv = phiSv;
    }
    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute ic, MatchConditions mc, Services sv) {
        //...
    }
}
