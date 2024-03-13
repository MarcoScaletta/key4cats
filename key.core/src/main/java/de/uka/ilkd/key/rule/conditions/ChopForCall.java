package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.UpdateJunctor;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChopForCall implements VariableCondition {


    private final SchemaVariable fullFmlSV;
    private final SchemaVariable preFmlSV;
    private final SchemaVariable innerFmlSV;
    private final SchemaVariable postFmlSV;

    public ChopForCall(SchemaVariable fullFml, SchemaVariable preFml, SchemaVariable innerFml, SchemaVariable postFml) {
        this.fullFmlSV = fullFml;
        this.preFmlSV = preFml;
        this.innerFmlSV = innerFml;
        this.postFmlSV = postFml;
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


    private Term traceStartsWithHelper(Term postAssociatedFullTrace, Term postAssociatedPrefixTrace){
        if(postAssociatedFullTrace.op() instanceof Junctor){
            // full trace contains more than 1 elem
            if(postAssociatedFullTrace.op() == postAssociatedPrefixTrace.op()){
                // also prefix contains more than 1 elem and the junctors coincide
                if(postAssociatedFullTrace.sub(0) == postAssociatedPrefixTrace.sub(0))
                    // the first elements of both traces coincide
                    return traceStartsWithHelper(postAssociatedFullTrace.sub(1), postAssociatedPrefixTrace.sub(1));

            }else if(postAssociatedFullTrace.sub(0) == postAssociatedPrefixTrace){
                // prefix is only one element and it matches the first element of full trace
                return postAssociatedFullTrace.sub(1);
            }

        }
        return null;
    }

    private List<Term> choppingTrace(Term trace, Services services){


        LinkedList<Term> chops = new LinkedList<>();
        Term traceEl = trace;
        Term concatTrace = null;
        Junctor junctor = null;


        while(traceEl.arity() > 1 && traceEl.op() instanceof Junctor j){
            System.out.println("AA");
            if(concatTrace == null) {
                concatTrace= traceEl.sub(1);
            }
            else
                concatTrace = services.getTermFactory().createTerm(junctor, traceEl.sub(1), concatTrace);
            if(j == Junctor.CHOP){
                chops.addFirst(concatTrace);
                concatTrace = null;
            }
            junctor = j;
            traceEl = traceEl.sub(0);
        }
        if(concatTrace != null)
            chops.addFirst(services.getTermFactory().createTerm(junctor, traceEl, concatTrace) );
        else
            chops.addFirst(traceEl);
        return chops;
    }

    private Term unchop(List<Term> traces, Services services){
        return traces.subList(1, traces.size()).stream().reduce(traces.get(0),
                (subUnchopped, trace) ->
                services.getTermFactory().createTerm(Junctor.CHOP, subUnchopped, trace) );
    }

    private List<Triple<Term, Term, Term>> getChoppings(List<Term> choppedTrace, Services services){
        List<Triple<Term, Term, Term>> triples = new ArrayList<>();
        for (int i = 0; i < choppedTrace.size()-2; i++) {
            for (int j = i+2; j < choppedTrace.size(); j++) {
                triples.add(new Triple<>(
                        unchop(choppedTrace.subList(0,i),services),
                        unchop(choppedTrace.subList(i+1,j-1),services),
                        unchop(choppedTrace.subList(j,choppedTrace.size()-1),services)));
            }
        }
        return triples;
    }

//    private Term disjunctChops(Term trace, Services services){
//        List<Term> t = choppingTrace(trace,services);
//        List<Triple<Term, Term, Term>>  triples = getChoppings(t, services);
//    }


    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();

        Term fullFmlTerm = (Term) svInst.getInstantiation(fullFmlSV);


        List<Term> t = choppingTrace(fullFmlTerm,services);
        if(t.size() < 3)
            return null;
        return matchCond.setInstantiations(
                svInst.add(preFmlSV,t.get(0),services)
                        .add(innerFmlSV,t.get(1),services)
                        .add(postFmlSV,t.get(2),services)
        );
    }
}
