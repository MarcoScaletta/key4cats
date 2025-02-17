package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.HavocUpdate;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class IsSameHavoc implements VariableCondition {


    private final TraceResolver term1SV;
    private final TraceResolver term2SV;


    public IsSameHavoc(TraceResolver term1, TraceResolver term2) {
        this.term1SV = term1;
        this.term2SV = term2;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        if(term1SV == null || term2SV == null)
            return matchCond;
        if(!term1SV.isVarInstantiated(svInst) || !term2SV.isVarInstantiated(svInst))
            return matchCond;
        Term term1 = term1SV.resolve(svInst,services);
        Term term2 = term2SV.resolve(svInst,services);
        if(term1!=null && term2!=null &&
                term1.op() instanceof HavocUpdate && term2.op() instanceof HavocUpdate &&
                term1.equals(term2)
        )
            return matchCond;
        return null;
    }
}
