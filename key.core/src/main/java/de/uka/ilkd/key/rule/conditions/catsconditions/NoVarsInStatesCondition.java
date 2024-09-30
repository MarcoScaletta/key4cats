package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.SchematicTrace;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

import java.util.List;

/**
 * @author Marco Scaletta
 */
public class NoVarsInStatesCondition implements VariableCondition {


    private final SchemaVariable formulaSV;


    public NoVarsInStatesCondition(SchemaVariable formula) {
        this.formulaSV = formula;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();

        Term formulaTerm = (Term) svInst.getInstantiation(formulaSV);
        if(areAllStatesTrue(formulaTerm,services))
            return matchCond;
        return null;
    }

    private boolean areAllStatesTrue(Term trace, Services services){
        TraceManager tm = new TraceManager(trace, List.of(Junctor.CHOP,Junctor.CONC,Junctor.AND,Junctor.OR),services);
        return tm.getTracePairs().stream().noneMatch(x -> (x.first.op() == Junctor.STATEFML) && x.first.sub(0).op() != Junctor.TRUE);
    }
}
