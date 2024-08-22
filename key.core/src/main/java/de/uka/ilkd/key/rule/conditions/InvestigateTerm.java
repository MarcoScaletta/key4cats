package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.StatementBlock;
import de.uka.ilkd.key.java.statement.Return;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

public class InvestigateTerm implements VariableCondition {


    private final SchemaVariable schemaVariable;

    public InvestigateTerm(SchemaVariable schemaVariable) {
        this.schemaVariable = schemaVariable;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        if(svInst.getInstantiation(schemaVariable) instanceof Return ret)
            return matchCond;
        return null;
    }
}
