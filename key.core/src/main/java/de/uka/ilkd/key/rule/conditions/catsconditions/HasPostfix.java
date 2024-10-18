package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.UpdateManager;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class HasPostfix implements VariableCondition {


    private final TraceResolver termSV;


    public HasPostfix(TraceResolver term) {
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

        if(term.sort() == Sort.FORMULA && new TraceManager(term,services).getSize() > 1)
            return matchCond;
        if(term.sort() == Sort.UPDATE && new UpdateManager(term,services).getSize() > 1)
            return matchCond;
        return null;
    }
}
