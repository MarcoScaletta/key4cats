package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Named;
import de.uka.ilkd.key.logic.ProgramElementName;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

public class IsSchematicTraceOverM extends VariableConditionAdapter {


    private final SchemaVariable formulaSV;
    private final SchemaVariable methodNameSV;

    private final boolean negated;


    public IsSchematicTraceOverM(SchemaVariable formula, SchemaVariable methodName, boolean negated) {
        this.formulaSV = formula;
        this.methodNameSV = methodName;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations svInst, Services services) {
        Term methodNameInst = (Term) svInst.getInstantiation(methodNameSV);

        Term formulaTerm = (Term) svInst.getInstantiation(formulaSV);

        return (formulaTerm.op() instanceof SchematicTrace && formulaTerm.subs().contains(methodNameInst)) != this.negated;
    }
}
