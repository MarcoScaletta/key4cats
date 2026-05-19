package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.UpdateManager;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;

/**
 * @author Marco Scaletta
 */
public class HasPostfix implements VariableCondition {


    private final TraceResolver termSV;

    private final SchemaVariable headSV;

    private final SchemaVariable tailSV;

    private final boolean inst;



    public HasPostfix(TraceResolver term) {
        this.termSV = term;
        this.headSV = null;
        this.tailSV = null;
        this.inst = false;
    }

    public HasPostfix(TraceResolver term, SchemaVariable head, SchemaVariable tail) {
        this.termSV = term;
        this.headSV = head;
        this.tailSV = tail;
        this.inst = true;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        if(termSV == null)
            return matchCond;
        if(!termSV.isVarInstantiated(svInst))
            return matchCond;

        Term term = termSV.resolve(svInst,services);

        if(!inst){
            if(term.sort() == Sort.FORMULA && new TraceManager(term,services).getSize() > 1)
                return matchCond;
            if(term.sort() == Sort.UPDATE && new UpdateManager(term,services).getSize() > 1)
                return matchCond;
            return null;
        }

        Term head = null, tail =null;

        if(term.sort() == Sort.FORMULA){
            TraceManager tM = new TraceManager(term, services);
            if (tM.getSize() > 1){
                Pair<Term, Junctor> first = tM.getFirst();
                if(!first.second.equals(Junctor.CHOP))
                    return null;
                head = first.first;
                tail = tM.getPostfixTerm(1);

            }
        }
        if(term.sort() == Sort.UPDATE){
            UpdateManager uM = new UpdateManager(term, services);
            if (uM.getSize() > 1){
                head = uM.getFirst();
                tail = uM.getPostfixTerm(1);
            }
        }
        if (head != null && tail != null)
            return matchCond.setInstantiations(
                svInst.add(headSV, head, services)
                        .add(tailSV, tail, services));
        return null;
    }
}
