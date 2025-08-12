package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.SpecialCallIds;
import de.uka.ilkd.key.logic.op.TraceEvent;
import de.uka.ilkd.key.rule.conditions.catsconditions.IsAtomicTraceElem;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class TraceManager extends SeqManager<Pair<Term,Junctor>> {

    private LinkedList<Pair<Term,Junctor>> tracePair;

    private final Services services;


    public TraceManager(Term trace, SVInstantiations instMap, Services services){
        this(trace,List.of(Junctor.CONC,Junctor.CHOP),instMap,services);
    }

    public TraceManager(Term trace,List<Junctor> junctors, SVInstantiations instMap, Services services){
        this.services = services;
        setupTrace(trace, instMap, junctors);
    }

    public TraceManager(Term trace,List<Junctor> junctors, Services services){
        this.services = services;
        setupTrace(trace, SVInstantiations.EMPTY_SVINSTANTIATIONS, junctors);
    }

    public TraceManager(Term trace, Services services){
       this(trace,List.of(Junctor.CONC,Junctor.CHOP),services);
    }

    public TraceManager(List<Pair<Term,Junctor>> tracePair, Services services){
        this.tracePair = new LinkedList<>(tracePair);
        this.services = services;
    }


    private void setupTrace(Term trace, SVInstantiations instMap, List<Junctor> junctors){
        tracePair = new LinkedList<>();
        separateTrace(trace,instMap,junctors);
    }


    public List<Pair<Term,Junctor>> getTracePairs(){return this.tracePair;}

    private void separateTrace(Term trace, SVInstantiations instMap, List<Junctor> junctors){
        this.tracePair = separateTraceRec(trace, instMap, junctors);
    }

    private LinkedList<Pair<Term,Junctor>> separateTraceRec(Term trace, SVInstantiations instMap, List<Junctor> junctors){

        LinkedList<Pair<Term,Junctor>> separatedTrace =  new LinkedList<>();

        if(trace.op() instanceof Junctor j && junctors.contains(j)) {
            separatedTrace = separateTraceRec(trace.sub(0), instMap, junctors);
            Pair<Term,Junctor> lastL = separatedTrace.getLast();
            separatedTrace.removeLast();
            separatedTrace.addLast(new Pair<>(lastL.first, j));
            separatedTrace.addAll(separateTraceRec(trace.sub(1),instMap,junctors));
        }
        else {
            if(trace.op() instanceof SchemaVariable traceSV && instMap.isInstantiated(traceSV))
                separatedTrace.addAll(new TraceManager((Term) instMap.getInstantiation(traceSV),services).getList());
            else
                separatedTrace.add(new Pair<>(trace, null));
        }
        return separatedTrace;
    }

    public SeqManager<Pair<Term,Junctor>> getSeqManagerFromTerm(Term term, Services services){
        return new TraceManager(term,services);
    }

    @Override
    public Pair<Term, Junctor> getFirst() {
        if(tracePair.isEmpty())
            return null;
        return tracePair.getFirst();
    }

    @Override
    public Pair<Term, Junctor> getLast() {
        if(tracePair.isEmpty())
            return null;
        return tracePair.getLast();
    }

    @Override
    public Term toTerm(Pair<Term,Junctor> elem) {
        return elem.first;
    }

    public int getSize(){
        return tracePair.size();
    }

    @Override
    public List<Pair<Term, Junctor>> getList() {
        return tracePair;
    }

    @Override
    public Term getTermFromList() {
        return getTermFromSubList(0, tracePair.size());
    }

    @Override
    public Term getTermFromSubList(int begin, int end) {
        List<Pair<Term,Junctor>> sublist = tracePair.subList(begin,end);
        Term trace = sublist.getFirst().first;
        for (int i = 1; i < sublist.size(); i++) {
            trace = services.getTermFactory().createTerm(sublist.get(i-1).second, trace, sublist.get(i).first);
        }
        return trace;
    }

    public void addLast(Pair<Term, Junctor> elem) {
        Term currentLast = this.getLast().first;
        tracePair.set(tracePair.size()-1, new Pair<>(currentLast,elem.second));
        tracePair.addLast(new Pair<>(elem.first,null));
    }

    @Override
    public boolean hasPrefix(SeqManager<Pair<Term, Junctor>> possiblePrefix) {
        int prefixSize = possiblePrefix.getSize();
        return this.getSize() >= prefixSize &&
                this.tracePair.subList(0,prefixSize-1).equals(possiblePrefix.getList().subList(0,prefixSize-1))
                && this.tracePair.get(prefixSize-1).first.equals(possiblePrefix.getLast().first);
    }

    // returns i >=0 : if possiblePrefixTM is prefix of this where
    //          i is the last index (inclusive) of the common prefix
    //todo: fix this based on the new implementation of hasPrefix and hasStrictPrefix
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

    public TraceManager conc(TraceManager trace){
        return createNewAddAll(trace, Junctor.CONC);
    }

    public TraceManager chop(TraceManager trace){
        return createNewAddAll(trace, Junctor.CHOP);
    }

    public TraceManager createNewAddAll(TraceManager trace, Junctor j){
        var last = tracePair.getLast();
        var list = new LinkedList<>(tracePair);
        list.set(list.size()-1, new Pair<>(last.first, j));
        list.addAll(trace.getList());
        return new TraceManager(list, services);
    }

    public static Term unchop(Term trace1, Term trace2, Services services){
        return services.getTermBuilder().chop(trace1,trace2);
    }
    public static Term conc(Term trace1, Term trace2, Services services){
        return services.getTermBuilder().conc(trace1,trace2);
    }

//todo:optimize
//    public static Term unchop(TraceManager trace1, TraceManager trace2, Services services){
//        TraceManager newTr = new TraceManager(trace1.getTracePairs(),services);
//        newTr.
//        return services.getTermBuilder().chop(trace1,trace2);
//    }


    public static boolean isTrace(Term term){
        return
                term.op() instanceof SchemaVariable ||
                IsAtomicTraceElem.isAtomicTraceElem(term) ||
                term.op() == Junctor.CHOP ||
                term.op() == Junctor.CONC;
    }

    public static int compareTracesLength(Term trace1, Term trace2, Services services){
        TraceManager tm1 = new TraceManager(trace1,services);
        TraceManager tm2 = new TraceManager(trace2,services);
        return tm1.getSize() - tm2.getSize();
    }

//    public boolean equalsWithWildcards(TraceManager otherTM){
//        if(otherTM.getSize() != this.getSize())
//            return false;
//        for (int i=0; i<this.getSize(); i++){
//            Pair<Term,Junctor>  thisElem = this.getList().get(i);
//            Pair<Term,Junctor>  otherElem = otherTM.getList().get(i);
//
//            // if the junctor or operator are NOT equals, the two traces are for sure different
//            if (thisElem.first.op() != otherElem.first.op())
//                return false;
//            if(i < this.getSize()-1 && !thisElem.second.equals(otherElem.second))
//                return false;
//            // if the two trace formulas are exactly the same, the next part is skipped
//            if(!thisElem.first.equals(otherElem.first)) {
//                Term wildcard = services.getTermFactory().createTerm(SpecialCallIds.wildcard);
//                int idIndex = -1;
//
//                if (thisElem.first.op() == TraceEvent.START_TR_EV ||  thisElem.first.op() == TraceEvent.POP_TR_EV) {
//                    if(!thisElem.first.subs().get(0).equals(otherElem.first.subs().get(1)))
//                        return false;
//                    idIndex = 1;
//                }
//                if (thisElem.first.op() == TraceEvent.RET_TR_EV)
//                    idIndex = 0;
//                // if the operator is not a trace event, then there cannot be any wildcard, therefore the index is -1
//                if(idIndex == -1)
//                    return false;
//                if(!(thisElem.first.subs().get(idIndex).equals(wildcard)) && !(otherElem.first.subs().get(idIndex).equals(wildcard)))
//                    return false;
//                else{
//                    System.out.println("Wildcard!");
//                }
//            }
//        }
//        return true;
//    }


}
