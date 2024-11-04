package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.SeqManager;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.UpdateManager;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;

import javax.xml.validation.Schema;

/**
 * @author Marco Scaletta
 */
public class PrefixPostfixCondition implements VariableCondition {


    private final Term termSV;
    private final Term prefixSV;
    private final Term postfixSV;


    public PrefixPostfixCondition(Term term, Term prefix,Term postfix) {
        this.termSV = term;
        this.prefixSV = prefix;
        this.postfixSV = postfix;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        Term term = termSV.op() instanceof SchemaVariable ? (Term) svInst.getInstantiation((SchemaVariable) termSV.op()) : termSV;
        Term prefixTerm = prefixSV.op() instanceof SchemaVariable ? (Term) svInst.getInstantiation((SchemaVariable) prefixSV.op()) : prefixSV;
        Term postfixTerm = postfixSV.op() instanceof SchemaVariable ? (Term) svInst.getInstantiation((SchemaVariable) postfixSV.op()) : postfixSV;

        if(term == null || prefixTerm == null || postfixTerm == null) {
            return matchCond;
        }else{
            boolean check = true;
            if(term.sort() == Sort.FORMULA) {
                check = prefixPostfixCondition(new TraceManager(term, services), new TraceManager(prefixTerm, services), new TraceManager(postfixTerm, services));
            }
            else if(term.sort() == Sort.UPDATE)
                check = prefixPostfixCondition(new UpdateManager(term, services),new UpdateManager(prefixTerm, services),new UpdateManager(postfixTerm, services));
            else return null;
            if(check)
                return matchCond;
            return null;
        }
    }

    private <T> boolean prefixPostfixCondition(SeqManager<T> seq, SeqManager<T> prefixSeq, SeqManager<T> postfixSeq){
        return seq.getSize() == prefixSeq.getSize() + postfixSeq.getSize()
                && seq.hasStrictPrefix(prefixSeq) && seq.hasStrictPostfix(postfixSeq);
    }
}
