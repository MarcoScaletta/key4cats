package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
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

    private List<Term> choppingTrace(Term trace, Services services){

        LinkedList<Term> chops = new LinkedList<>();
        Term traceEl = trace;
        Term concatTrace = null;
        Junctor junctor = null;


        while(traceEl.arity() > 1 && traceEl.op() instanceof Junctor j){
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

    private boolean containsSchemTr(Term trace){
        if(trace.op() instanceof SchematicTrace)
            return true;
        if(trace.op() == Junctor.CHOP || trace.op() == Junctor.CONC){
            return containsSchemTr(trace.sub(0)) || containsSchemTr(trace.sub(1));
        }
        return false;
    }

    private boolean containsSchemTr(List<Term> traceList){
        return traceList.stream().anyMatch(this::containsSchemTr);
    }

    private List<Triple<Term, Term, Term>> getChoppings(List<Term> choppedTrace, Services services){
//        System.out.println("Total chops: " + choppedTrace.size());
        List<Triple<Term, Term, Term>> triples = new ArrayList<>();
        if(choppedTrace.size() < 3)
            return null;
        for (int i = 0; i < choppedTrace.size()-2; i++) {
            for (int j = i+2; j < choppedTrace.size(); j++) {
                List<Term> preTraceList = choppedTrace.subList(0,i+1);
                List<Term> innerTraceList = choppedTrace.subList(i+1,j);
                List<Term> postTraceList = choppedTrace.subList(j,choppedTrace.size());

                if(containsSchemTr(preTraceList) && containsSchemTr(innerTraceList) && containsSchemTr(postTraceList))
                    triples.add(new Triple<>(
                            unchop(preTraceList,services),
                            unchop(innerTraceList, services),
                            unchop(postTraceList, services)
                    ));
            }
        }
        return triples;
    }



    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();

        Term fullFmlTerm = (Term) svInst.getInstantiation(fullFmlSV);


        List<Triple<Term,Term,Term>> choppings = getChoppings(choppingTrace(fullFmlTerm,services),services);
        if(choppings == null  || choppings.isEmpty())
            return null;

//        System.out.println("Possible suitable choppings: " + choppings.size());


        return matchCond.setInstantiations(
                svInst.add(preFmlSV,choppings.get(0).first,services)
                        .add(innerFmlSV,choppings.get(0).second,services)
                        .add(postFmlSV,choppings.get(0).third,services)
        );
    }
}
