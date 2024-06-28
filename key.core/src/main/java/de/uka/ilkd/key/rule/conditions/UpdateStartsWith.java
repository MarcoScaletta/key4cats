package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;

import java.util.*;

public class UpdateStartsWith implements VariableCondition {


    private final SchemaVariable updateFullSV;
    private final SchemaVariable updatePrefixSV;
    private final SchemaVariable updatePostfixSV;
    private final SchemaVariable traceFullSV;
    private final SchemaVariable tracePrefixSV;
    private final SchemaVariable tracePostfixSV;

    public UpdateStartsWith(SchemaVariable updateFull, SchemaVariable updatePrefix, SchemaVariable updatePostfix,
                            SchemaVariable traceFull, SchemaVariable tracePrefix, SchemaVariable tracePostfix) {
        this.updateFullSV = updateFull;
        this.updatePrefixSV = updatePrefix;
        this.updatePostfixSV = updatePostfix;
        this.traceFullSV = traceFull;
        this.tracePrefixSV = tracePrefix;
        this.tracePostfixSV = tracePostfix;
    }

    private List<Term> updateToList(Term update){
        List<Term> updateList = new ArrayList<>();
        if(update.op() instanceof UpdateJunctor op){
            if(op != UpdateJunctor.SEQUENTIAL_UPDATE)
                return null;
            List<Term> updateSub0 = updateToList(update.sub(0));
            if(updateSub0 == null)
                return null;
            updateList.addAll(updateSub0);
            updateList.add(update.sub(1));
        }else {
            updateList.add(update);
        }
        return updateList;
    }
//
    private Pair<Term,Integer> postAssociate(Term trace, Services services){
        if(trace.op() instanceof Junctor junctor && junctor.arity() > 1)
            return postAssociate(junctor, trace.sub(0), trace.sub(1), 1, services);
        else return new Pair<>(trace, 1);
    }
//
    private Pair<Term,Integer> postAssociate(Junctor junctor, Term tracePreAssociated, Term tracePostAssociated, int count, Services services){

        if(tracePreAssociated.op() instanceof Junctor j && j.arity() > 1){
            Term newPostAssociated = services.getTermFactory().createTerm(junctor, tracePreAssociated.sub(1),tracePostAssociated);
            return postAssociate(j, tracePreAssociated.sub(0),  newPostAssociated, count + 1, services);
        }else {
            Term newPostAssociated = services.getTermFactory().createTerm(junctor, tracePreAssociated,tracePostAssociated);
            return new Pair<>(newPostAssociated, count+1);
        }

    }


    private Term traceStartsWith(Term fullTrace, Term prefixTrace, Services services){

        Pair<Term,Integer> pairFullTrace = postAssociate(fullTrace, services);
        Pair<Term,Integer>  pairPrefixTrace = postAssociate(prefixTrace, services);

        if(pairFullTrace.second <= pairPrefixTrace.second)
            // prefix exceeds length of full trace or postfix is empty
            return null;
        Pair<Term,Junctor> pair = traceStartsWithHelper(pairFullTrace.first, pairPrefixTrace.first);
        if(pair != null) {
            Term postfix = pair.first;
            if(postfix.subs().get(0).op() instanceof SchematicTrace)
                return postfix;
            Term lastTerm = (prefixTrace.op() == Junctor.CHOP || prefixTrace.op() == Junctor.CONC) ? prefixTrace.sub(1) : prefixTrace;
            if(lastTerm.op() instanceof SchematicTrace){
                postfix = services.getTermFactory().createTerm(pair.second, lastTerm, postfix);
            }
            return postfix;
        }
        return null;

    }

    private Pair<Term,Junctor> traceStartsWithHelper(Term postAssociatedFullTrace, Term postAssociatedPrefixTrace){
        if(postAssociatedFullTrace.op() instanceof Junctor j){
            // full trace contains more than 1 elem
            if(j == postAssociatedPrefixTrace.op()){
                // also prefix contains more than 1 elem and the junctors coincide
                if(postAssociatedFullTrace.sub(0) == postAssociatedPrefixTrace.sub(0))
                    // the first elements of both traces coincide
                    return traceStartsWithHelper(postAssociatedFullTrace.sub(1), postAssociatedPrefixTrace.sub(1));

            }else if(postAssociatedFullTrace.sub(0) == postAssociatedPrefixTrace){
                // prefix is only one element and it matches the first element of full trace
                return new Pair<>(postAssociatedFullTrace.sub(1), j);
            }

        }
        return null;
    }

    private Term updateStartsWith(Term update, Term prefix, Services services){
        List<Term> updateList = updateToList(update);
        List<Term> prefixList =  updateToList(prefix);
        if(updateList==null || prefixList==null || updateList.size() <= prefixList.size())
            return null;
        int index;
        for(index=0;index<prefixList.size();index++){
            if(prefixList.get(index) != updateList.get(index))
                return null;
        }

        Term updateJunctorTerm = updateList.get(index);
        for (int i = index+1; i < updateList.size(); i++) {
            updateJunctorTerm = services.getTermFactory().createTerm(UpdateJunctor.SEQUENTIAL_UPDATE, updateJunctorTerm, updateList.get(i));
        }
        return updateJunctorTerm;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        Term updateTerm = (Term) svInst.getInstantiation(updateFullSV);
        Term prefixTerm = (Term) svInst.getInstantiation(updatePrefixSV);
        Term traceTerm = (Term) svInst.getInstantiation(traceFullSV);
        Term tracePrefixTerm = (Term) svInst.getInstantiation(tracePrefixSV);



        if(updateTerm == null || prefixTerm == null || traceTerm == null || tracePrefixTerm == null)
            return matchCond;
        Term updatePostfix = updateStartsWith(updateTerm,prefixTerm, services);
        Term tracePostfix = traceStartsWith(traceTerm, tracePrefixTerm, services);
        if(updatePostfix!=null && tracePostfix != null)
            return matchCond.setInstantiations(svInst.add(updatePostfixSV, updatePostfix, services).add(tracePostfixSV, tracePostfix, services));
        return null;
    }
}
