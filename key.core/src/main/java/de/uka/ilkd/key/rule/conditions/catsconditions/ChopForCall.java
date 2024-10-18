package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

//    public static Term unchop(List<Term> traces, Services services) {
//        if(traces.size()>1)
//            return TraceManager.unchopTraceManagers(traces.stream().map(x -> new TraceManager(x,services)).toList(),services);
//        return traces.getFirst();
//    }

    private static boolean containsSchemTr(Term trace){
        if(trace.op() instanceof SchematicTrace)
            return true;
        if(trace.op() == Junctor.CHOP || trace.op() == Junctor.CONC){
            return containsSchemTr(trace.sub(0)) || containsSchemTr(trace.sub(1));
        }
        return false;
    }

    public static boolean containsSchemTr(List<Term> traceList){
        return traceList.stream().anyMatch(ChopForCall::containsSchemTr);
    }


    private static boolean isLastObservation(List<Term> traceList){
        return traceList.getLast().op() instanceof Observation;
    }

    private static boolean isFirstSchemTr(List<Term> traceList){
        return traceList.getFirst().op() instanceof SchematicTrace;
    }
    private static boolean isLastSchemTr(List<Term> traceList){
        return traceList.getLast().op() instanceof SchematicTrace;
    }

    public static List<Pair<Term,Term>> choppingTrail(Term trailTrace, Term lastPreFormula, Services services){
        List<Term> choppedTrace = choppingTrace(trailTrace,services);
        LinkedHashSet<Pair<Term, Term>> pairs = new LinkedHashSet<>();
        if(choppedTrace.size() == 1) {
            //this means that the trailTrace consists of a single atomic trace element or schematic trace
            if (!(choppedTrace.getFirst().op() instanceof SchematicTrace)) {
                // this means that trailTrace consists of a single atomic trace element
                // of course since this element is atomic it cannot be chopped
                // the result is therefore null
                return null;
            }else{
                // this means that trailTrace consists of a single schematic trace
                // both inner- and post-formula are therefore such schematic trace
                return List.of(new Pair<>(choppedTrace.getFirst(),choppedTrace.getFirst()));
            }
        }

        for (int j = 1; j < choppedTrace.size(); j++) {
            LinkedList<Term> innerTraceList =  new LinkedList<>(choppedTrace.subList(0,j));
            LinkedList<Term> postTraceList =   new LinkedList<>(choppedTrace.subList(j,choppedTrace.size()));

            // (pre ** ~~, in, _) --> (pre ** ~~, ~~ ** in, _)
            if(lastPreFormula.op() instanceof SchematicTrace && !containsSchemTr(innerTraceList))
                innerTraceList.addFirst(lastPreFormula);
            // (_, in ** ~~, post) --> (_, in ** ~~, ~~ ** post)
            if(innerTraceList.getLast().op() instanceof SchematicTrace && !containsSchemTr(postTraceList))
                postTraceList.addFirst(innerTraceList.getLast());
            // (_, in, ~~ ** post) --> (_, in ** ~~, ~~ ** post)
            if(postTraceList.getFirst().op() instanceof SchematicTrace && !containsSchemTr(innerTraceList))
                innerTraceList.addLast(postTraceList.getFirst());

            if(containsSchemTr(innerTraceList) && containsSchemTr(postTraceList))
                pairs.add(new Pair<>(
                        unchop(innerTraceList, services),
                        unchop(postTraceList, services)
                ));
        }

        return pairs.stream().toList();

    }

    public static List<Triple<Term, Term, Term>> getChoppings(Term choppedTraceTerm, Services services){
        List<Term> choppedTrace = choppingTrace(choppedTraceTerm,services);
        LinkedHashSet<Triple<Term, Term, Term>> triples = new LinkedHashSet<>();
        if(choppedTrace.size() < 3) {
            if (choppedTrace.getFirst().op() instanceof SchematicTrace) {
                choppedTrace.addFirst(choppedTrace.getFirst());
            } else {
                if (choppedTrace.getLast().op() instanceof SchematicTrace) {
                    choppedTrace.addLast(choppedTrace.getLast());
                } else {
                    return null;
                }
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
        return triples.stream().toList();
    }


    public static List<Triple<Term, Term, Term>> getChoppingsNEW(Term choppedTraceTerm, Services services){
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
                if(preTraceList.getLast().op() instanceof SchematicTrace && !isFirstSchemTr(innerTraceList))
                    innerTraceList.addFirst(preTraceList.getLast());
                // (pre, ~~ ** in, _) --> (pre ** ~~, ~~ ** in, _)
                if(innerTraceList.getFirst().op() instanceof SchematicTrace && !containsSchemTr(preTraceList))
                    preTraceList.addLast(innerTraceList.getFirst());
                // (_, in ** ~~, post) --> (_, in ** ~~, ~~ ** post)
                if(innerTraceList.getLast().op() instanceof SchematicTrace && !containsSchemTr(postTraceList))
                    postTraceList.addFirst(innerTraceList.getLast());
                // (_, in, ~~ ** post) --> (_, in ** ~~, ~~ ** post)
                if(postTraceList.getFirst().op() instanceof SchematicTrace && !isLastSchemTr(innerTraceList))
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

    private static Term getFirstIfSchemTr(List<Term> traceList){
        if(traceList.getFirst().op() instanceof SchematicTrace)
            return traceList.getFirst();
        return null;
    }


    private static Term getLastIfSchemTr(List<Term> traceList){
        if(traceList.getLast().op() instanceof SchematicTrace)
            return traceList.getLast();
        return null;
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
