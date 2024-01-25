package de.uka.ilkd.key.ldt;

import de.uka.ilkd.key.java.Expression;
import de.uka.ilkd.key.java.ProgramElement;
import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.abstraction.Type;
import de.uka.ilkd.key.java.expression.Literal;
import de.uka.ilkd.key.java.expression.Operator;
import de.uka.ilkd.key.java.reference.ExecutionContext;
import de.uka.ilkd.key.java.reference.TypeReference;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.ProgramElementName;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermServices;
import de.uka.ilkd.key.logic.op.Function;
import org.key_project.util.ExtList;

public class MethodNameLDT extends LDT{

    public static final Name NAME = new Name("MethodName");

    public MethodNameLDT(TermServices services) {
        super(NAME, services);
    }

    @Override
    public boolean isResponsible(Operator op, Term[] subs, Services services, ExecutionContext ec) {
        return false;
    }

    @Override
    public boolean isResponsible(Operator op, Term left, Term right, Services services, ExecutionContext ec) {
        return false;
    }

    @Override
    public boolean isResponsible(Operator op, Term sub, TermServices services, ExecutionContext ec) {
        return false;
    }

    @Override
    public Term translateLiteral(Literal lit, Services services) {
        return null;
    }

    @Override
    public Function getFunctionFor(Operator op, Services services, ExecutionContext ec) {
        return null;
    }

    @Override
    public boolean hasLiteralFunction(Function f) {
        return false;
    }

    @Override
    public Expression translateTerm(Term t, ExtList children, Services services) {
        return null;
    }

    @Override
    public Type getType(Term t) {
        return null;
    }

    public Function getUniqueMethodConstant(TypeReference type, ProgramElementName methodName, Services services) {
        return getUniqueMethodConstant(type.getName(), methodName.getProgramName(), services);
    }

    public Function getUniqueMethodConstant(String typeName, String methodName, Services services){
        ProgramElementName uniqueName = new ProgramElementName(methodName, typeName);
        return (Function) services.getNamespaces().functions().lookup(uniqueName);
    }
}
