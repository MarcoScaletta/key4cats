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
public class IsAtomicTraceElem implements VariableCondition {


    private final TraceResolver extractor;


    public IsAtomicTraceElem(TraceResolver extractor) {

        this.extractor = extractor;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        Term formulaTerm = extractor.resolve(svInst,services);

        if(formulaTerm == null || formulaTerm.op() instanceof TraceEvent || formulaTerm.op() instanceof SchematicTrace || formulaTerm.op() == Junctor.STATEFML)
            return matchCond;
        return null;
    }

    public static boolean isAtomicTraceElem(Term trace){
        return trace.op() instanceof TraceEvent
                || trace.op() instanceof SchematicTrace
                || trace.op() == Junctor.STATEFML;
    }
}
