package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.UpdateManager;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.SchematicTrace;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

/**
 * @author Marco Scaletta
 */
public class FirstOf implements VariableCondition {


    private final SchemaVariable termSV;
    private final SchemaVariable firstElemSV;


    public FirstOf(SchemaVariable term, SchemaVariable firstElem) {
        this.termSV = term;
        this.firstElemSV = firstElem;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();

        Term term = (Term) svInst.getInstantiation(termSV);
        Term firstElem = null;
        if(term == null)
            return matchCond;
        if(termSV.sort() == Sort.FORMULA)
            firstElem = (new TraceManager(term,services)).getTracePairs().getFirst().first;
        if(termSV.sort() == Sort.UPDATE)
            firstElem = (new UpdateManager(term,services)).getFirst();

        if(firstElem == null)
            return null;
        return matchCond.setInstantiations(svInst.add(firstElemSV,firstElem,services));
    }
}
