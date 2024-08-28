package de.uka.ilkd.key.rule.conditions.catsconditions;

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
/**
 * @author Marco Scaletta
 */
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

    private static List<Term> choppingTrace(Term trace, Services services){

        LinkedList<Term> chops = new LinkedList<>();
        Term traceEl = trace;
        Term concatTrace = null;
        Junctor junctor = null;

        while(traceEl.arity() > 1 && traceEl.op() instanceof Junctor j && (j==Junctor.CHOP || j == Junctor.CONC)){
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

    private static Term unchop(List<Term> traces, Services services){

        return traces.subList(1, traces.size()).stream().reduce(traces.getFirst(),
                (subUnchopped, trace) ->
                services.getTermFactory().createTerm(Junctor.CHOP, subUnchopped, trace) );
    }

    private static boolean containsSchemTr(Term trace){
        if(trace.op() instanceof SchematicTrace)
            return true;
        if(trace.op() == Junctor.CHOP || trace.op() == Junctor.CONC){
            return containsSchemTr(trace.sub(0)) || containsSchemTr(trace.sub(1));
        }
        return false;
    }

    private static boolean containsSchemTr(List<Term> traceList){
        return traceList.stream().anyMatch(ChopForCall::containsSchemTr);
    }

    public static List<Triple<Term, Term, Term>> getChoppings(Term choppedTraceTerm, Services services){
        List<Term> choppedTrace = choppingTrace(choppedTraceTerm,services);
        List<Triple<Term, Term, Term>> triples = new ArrayList<>();
        if(choppedTrace.size() < 3) {
            if (choppedTrace.getFirst().op() instanceof SchematicTrace) {
                choppedTrace.addFirst(choppedTrace.getFirst());
            } else {
                return null;
            }
        }
        for (int i = 0; i < choppedTrace.size()-2; i++) {
            for (int j = i+2; j < choppedTrace.size(); j++) {
                LinkedList<Term> preTraceList = new LinkedList<>(choppedTrace.subList(0,i+1));
                LinkedList<Term> innerTraceList =  new LinkedList<>(choppedTrace.subList(i+1,j));
                LinkedList<Term> postTraceList =   new LinkedList<>(choppedTrace.subList(j,choppedTrace.size()));

                // (pre ** ~~, in, _) --> (pre ** ~~, ~~ ** in, _)
                if(preTraceList.getLast().op() instanceof SchematicTrace && !containsSchemTr(innerTraceList))
                    innerTraceList.addFirst(preTraceList.getLast());
                // (pre, ~~ ** in, _) --> (pre ** ~~, ~~ ** in, _)
                if(innerTraceList.getFirst().op() instanceof SchematicTrace && !containsSchemTr(preTraceList))
                    preTraceList.addLast(innerTraceList.getFirst());
                // (_, in ** ~~, post) --> (_, in ** ~~, ~~ ** post)
                if(innerTraceList.getLast().op() instanceof SchematicTrace && !containsSchemTr(postTraceList))
                    postTraceList.addFirst(innerTraceList.getLast());
                // (_, in, ~~ ** post) --> (_, in ** ~~, ~~ ** post)
                if(postTraceList.getFirst().op() instanceof SchematicTrace && !containsSchemTr(innerTraceList))
                    innerTraceList.addLast(postTraceList.getFirst());


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

        List<Triple<Term,Term,Term>> choppings = getChoppings(fullFmlTerm,services);
        if(choppings == null  || choppings.isEmpty())
            return null;

        return matchCond.setInstantiations(
                svInst.add(preFmlSV,choppings.getFirst().first,services)
                        .add(innerFmlSV,choppings.getFirst().second,services)
                        .add(postFmlSV,choppings.getFirst().third,services)
        );
    }
}
