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


    private final SchemaVariable formulaSV;
    private final boolean negated;


    public IsSchematicTrace(SchemaVariable formula, boolean negated) {
        this.formulaSV = formula;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations instMap, Services services) {
        Term formulaTerm = (Term) instMap.getInstantiation(formulaSV);
        if(formulaTerm == null)
            return true;
        return negated != formulaTerm.op() instanceof SchematicTrace;
    }

}
