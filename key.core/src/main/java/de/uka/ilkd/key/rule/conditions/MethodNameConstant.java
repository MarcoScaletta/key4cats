package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.abstraction.KeYJavaType;
import de.uka.ilkd.key.java.reference.MethodName;
import de.uka.ilkd.key.java.reference.TypeReference;
import de.uka.ilkd.key.logic.ProgramElementName;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.Function;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

public class MethodNameConstant implements VariableCondition {


    private final SchemaVariable methodName;
    private final SchemaVariable typeSV;
    private final SchemaVariable methodConstant;

    public MethodNameConstant(SchemaVariable typeSV, SchemaVariable methodName, SchemaVariable methodConstant) {
        this.typeSV = typeSV;
        this.methodName = methodName;
        this.methodConstant = methodConstant;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        ProgramElementName methodNameInst = (ProgramElementName) svInst.getInstantiation(methodName);
        TypeReference typeInst = (TypeReference) svInst.getInstantiation(typeSV);
        Term methodConstantInst = (Term) svInst.getInstantiation(methodConstant);

        if (methodNameInst == null || typeInst == null) {
            if(methodConstantInst == null)
                return matchCond;
            else {
                ProgramElementName methodNameConstant = (ProgramElementName) methodConstantInst.op().name();
                KeYJavaType kjt = services.getJavaInfo().getKeYJavaType(methodNameConstant.getQualifier());
                TypeReference tf = services.getJavaInfo().createTypeReference(kjt);
                ProgramElementName mn = new ProgramElementName(((ProgramElementName) methodConstantInst.op().name()).getProgramName());
                return matchCond.setInstantiations(svInst.add(typeSV, tf, services).add(methodName, mn,services));
            }
        }

        Term result = services.getTermBuilder().func(
                services.getTypeConverter().getMethodNameLDT().getUniqueMethodConstant(typeInst, methodNameInst, services));
        if (methodConstantInst != null && !result.equals(methodConstantInst)) {
            return null; //failed, strange
        } else {
            return matchCond.setInstantiations(svInst.add(methodConstant, result, services));
        }
    }
}
