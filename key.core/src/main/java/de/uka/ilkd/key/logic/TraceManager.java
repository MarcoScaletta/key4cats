package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.util.Pair;

import java.util.LinkedList;

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

    private void separateTrace(Term trace){
        this.tracePair = separateTraceRec(trace);
    }

    private LinkedList<Pair<Term,Junctor>> separateTraceRec(Term trace){

        LinkedList<Pair<Term,Junctor>> separatedTrace =  new LinkedList<>();

        if(trace.op() instanceof Junctor j && (j == Junctor.CHOP || j == Junctor.CONC)) {
            separatedTrace = separateTraceRec(trace.sub(0));
            Pair<Term,Junctor> lastL = separatedTrace.getLast();
            separatedTrace.removeLast();
            separatedTrace.addLast(new Pair<>(lastL.first, j));
            separatedTrace.addAll(separateTraceRec(trace.sub(1)));
        }
        else {
            separatedTrace.add(new Pair<>(trace, null));
        }
        return separatedTrace;
    }

    private LinkedList<Pair<Term,Junctor>> aa(Term trace){

        Term traceEl = trace;
        Term concatTrace = null;
        Junctor junctor = null;

        LinkedList<Pair<Term,Junctor>> tracePairLoc = new LinkedList<>();

        while(traceEl.arity() > 1 && traceEl.op() instanceof Junctor j){
            if(concatTrace == null) {
                concatTrace= traceEl.sub(1);
            }
            else
                concatTrace = services.getTermFactory().createTerm(junctor, traceEl.sub(1), concatTrace);
            if(j == Junctor.CHOP || j == Junctor.CONC){
                if(concatTrace.arity() > 1 && traceEl.op() instanceof Junctor j1){

                }
                else {
                    tracePairLoc.addFirst(new Pair<>(concatTrace, junctor));
                    concatTrace = null;
                }
            }
            junctor = j;
            traceEl = traceEl.sub(0);

        }
        Term lastTerm = concatTrace != null ? services.getTermFactory().createTerm(junctor, traceEl, concatTrace) : traceEl;
        tracePairLoc.addFirst(new Pair<>(lastTerm, junctor));
        return tracePairLoc;
    }


    public int getSize(){
        return tracePair.size();
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
