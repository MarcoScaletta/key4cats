package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.util.Pair;
import recoder.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TraceManager  {

    private LinkedList<Pair<Term,Junctor>> tracePair;

    private final Services services;


    public TraceManager(Term trace, Services services){
       this(trace,List.of(Junctor.CONC,Junctor.CHOP),services);
    }

    public TraceManager(Term trace,List<Junctor> junctors, Services services){
        this.services = services;
        setupTrace(trace, junctors);
    }

    private void setupTrace(Term trace, List<Junctor> junctors){
        tracePair = new LinkedList<>();
        separateTrace(trace,junctors);
    }

    public List<Pair<Term,Junctor>> getTracePairs(){return this.tracePair;}

    private void separateTrace(Term trace, List<Junctor> junctors){
        this.tracePair = separateTraceRec(trace, junctors);
    }

    private LinkedList<Pair<Term,Junctor>> separateTraceRec(Term trace, List<Junctor> junctors){

        LinkedList<Pair<Term,Junctor>> separatedTrace =  new LinkedList<>();

        if(trace.op() instanceof Junctor j && junctors.contains(j)) {
            separatedTrace = separateTraceRec(trace.sub(0), junctors);
            Pair<Term,Junctor> lastL = separatedTrace.getLast();
            separatedTrace.removeLast();
            separatedTrace.addLast(new Pair<>(lastL.first, j));
            separatedTrace.addAll(separateTraceRec(trace.sub(1),junctors));
        }
        else {
            separatedTrace.add(new Pair<>(trace, null));
        }
        return separatedTrace;
    }

    public Pair<Term, Junctor> lastElem() {
        if(tracePair.isEmpty())
            return null;
        return tracePair.getLast();
    }


    public int getSize(){
        return tracePair.size();
    }

    public static Term getTraceFromList(List<Pair<Term,Junctor>> traceAsList, Services services){

        Term trace = traceAsList.getFirst().first;
        for (int i = 1; i < traceAsList.size(); i++) {
            trace = services.getTermFactory().createTerm(traceAsList.get(i-1).second, trace, traceAsList.get(i).first);
        }
        return trace;
    }

    public static List<Pair<Term, Junctor>> addLast(List<Pair<Term, Junctor>> list,Term el, Junctor junctor){
        List<Pair<Term,Junctor>> l =  new ArrayList<>(list.subList(0,list.size()-1));
        l.addLast(new Pair<>(list.getLast().first,junctor));
        l.addLast(new Pair<>(el,null));
        return l;
    }


    // returns i >=0 : if possiblePrefixTM is prefix of this where
    //          i is the last index (inclusive) of the common prefix
    public int hasCommonPrefixOrIsEquals(TraceManager possiblePrefixTM){
        int prefixEndIndex = -1;
        if(this.getSize() >= possiblePrefixTM.getSize()){


            for (int i = 0; i < possiblePrefixTM.getSize(); i++) {
                Pair<Term,Junctor> pairPre = possiblePrefixTM.tracePair.get(i);
                Pair<Term,Junctor> pairThis = this.tracePair.get(i);
                if (pairPre.first != pairThis.first || (pairPre.second != null && pairPre.second != pairThis.second))
                    return prefixEndIndex;
                else
                    prefixEndIndex++;
            }
        }
        return prefixEndIndex;
    }
//
//    public boolean hasStrictPrefix(TraceManager possiblePrefixTM){
//        int index = hasCommonPrefixOrIsEquals(possiblePrefixTM);
//        return index == (possiblePrefixTM.tracePair.size()-1);
//    }

    public boolean hasStrictPrefix(TraceManager possiblePrefix){
        if(possiblePrefix.tracePair.size() >= this.tracePair.size())
            return false;
        return
                this.tracePair.subList(0,possiblePrefix.tracePair.size()-1).equals(
                        possiblePrefix.tracePair.subList(0,possiblePrefix.tracePair.size()-1))
                && this.tracePair.get(possiblePrefix.tracePair.size()-1).first.equals(possiblePrefix.tracePair.getLast().first)
                ;
    }

    public static Term unchop(List<Term> traces, Services services){

        return traces.subList(1, traces.size()).stream().reduce(traces.getFirst(),
                (subUnchopped, trace) ->
                        services.getTermFactory().createTerm(Junctor.CHOP, subUnchopped, trace) );
    }

    public static Term unchop(Term trace1, Term trace2, Services services){
        return services.getTermFactory().createTerm(Junctor.CHOP, trace1, trace2);
    }

    @Override
    public String toString(){
        return this.tracePair.toString();
    }
}
