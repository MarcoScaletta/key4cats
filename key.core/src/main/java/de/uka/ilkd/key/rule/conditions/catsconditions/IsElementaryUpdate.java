package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class IsElementaryUpdate implements VariableCondition {


    private final TraceResolver termSV;


    public IsElementaryUpdate(TraceResolver term) {
        this.termSV = term;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        if(termSV == null)
            return null;
        if(!termSV.isVarInstantiated(svInst))
            return matchCond;

        Term term = termSV.resolve(svInst,services);
        if((term.sort() == Sort.UPDATE && term.op() instanceof ElementaryUpdate))
            return matchCond;
        return null;
    }
}
