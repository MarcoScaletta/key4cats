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

        Term traceEl = trace;
        Term concatTrace = null;
        Junctor junctor = null;

        while(traceEl.arity() > 1 && traceEl.op() instanceof Junctor j){
            if(concatTrace == null) {
                concatTrace= traceEl.sub(1);
            }
            else
                concatTrace = services.getTermFactory().createTerm(junctor, traceEl.sub(1), concatTrace);
            if(j == Junctor.CHOP || j== Junctor.CONC){
                tracePair.addFirst(new Pair<>(concatTrace, junctor));
                concatTrace = null;
            }
            junctor = j;
            traceEl = traceEl.sub(0);

        }
        Term lastTerm = concatTrace != null ? services.getTermFactory().createTerm(junctor, traceEl, concatTrace) : traceEl;
        tracePair.addFirst(new Pair<>(lastTerm, junctor));
    }

    public int getSize(){
        return tracePair.size();
    }


    public int hasPrefixOrIsEquals(TraceManager prefix){
        int prefixEndIndex = -1;
        if(this.getSize() >= prefix.getSize()){
            for(int i=0;i<prefix.getSize();i++){
                if(!(prefix.tracePair.get(i).equals(this.tracePair.get(i))))
                    return prefixEndIndex;
                else
                    prefixEndIndex++;
            }
        }
        return prefixEndIndex;
    }

}
