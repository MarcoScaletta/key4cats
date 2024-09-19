package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.UpdateJunctor;
import de.uka.ilkd.key.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class TraceManager  {

    private LinkedList<Pair<Term,Junctor>> tracePair;

    private final Services services;


    public TraceManager(Term trace, Services services){
       this.services = services;
        setupTrace(trace);
    }

    private void setupTrace(Term trace){
        tracePair = new LinkedList<>();
        separateTrace(trace);
    }


    public List<Pair<Term,Junctor>> getTracePairs(){return this.tracePair;}

    private void separateTrace(Term trace){
        this.tracePair = separateTraceRec(trace, List.of(Junctor.CONC,Junctor.CHOP));
    }

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


    public int hasPrefixOrIsEquals(TraceManager prefix){
        int prefixEndIndex = -1;
        if(this.getSize() >= prefix.getSize()){


            for (int i = 0; i < prefix.getSize(); i++) {
                Pair<Term,Junctor> pairPre = prefix.tracePair.get(i);
                Pair<Term,Junctor> pairThis = this.tracePair.get(i);
                if (pairPre.first != pairThis.first || (pairPre.second != null && pairPre.second != pairThis.second))
                    return prefixEndIndex;
                else
                    prefixEndIndex++;
            }
        }
        return prefixEndIndex;
    }

    @Override
    public String toString(){
        return this.tracePair.toString();
    }
}
