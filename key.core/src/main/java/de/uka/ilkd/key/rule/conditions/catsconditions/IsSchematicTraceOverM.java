package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

import java.util.*;

/**
 * @author Marco Scaletta
 */
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
        Term schemTrTerm = (Term) svInst.getInstantiation(formulaSV);
        return this.negated != isSchematicTraceOverM(schemTrTerm,methodNameInst);
    }

    public static boolean isSchematicTraceOverM(Term schemTrTerm, Term methodNameInst){
        if((!(schemTrTerm.op() instanceof SchematicTrace)) || schemTrTerm.arity() == 0)
            return false;
        Set<Term> forbProcs = getForbiddenProcsRec(schemTrTerm.sub(0));
        return forbProcs.contains(methodNameInst);
    }

    public static Set<Term> getForbiddenProcsRec(Term schemTrTerm){
        Set<Term> separatedTrace = new HashSet<>();
            if (schemTrTerm.op() instanceof SchematicTraceJunctor) {
                separatedTrace.add(schemTrTerm.sub(1));
                separatedTrace.addAll(getForbiddenProcsRec(schemTrTerm.sub(0)));
            } else
                separatedTrace.add(schemTrTerm);
        return separatedTrace;
    }
}
