package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.op.UpdateJunctor;

import java.util.ArrayList;
import java.util.List;

public class UpdateManager extends SeqManager<Term> {

    private final List<Term> updateList;

    private final Services services;


    public UpdateManager(Term updateTerm, Services services){
        this.services = services;
        updateList = fromUpdateToListOfUpdates(updateTerm);
    }

    public static List<Term> fromUpdateToListOfUpdates(Term update){
        List<Term> updateList = new ArrayList<>();
        if(update.op() instanceof UpdateJunctor op){
            if(op != UpdateJunctor.SEQUENTIAL_UPDATE)
                return null;
            List<Term> updateSub0 = fromUpdateToListOfUpdates(update.sub(0));
            if(updateSub0 == null)
                return null;
            updateList.addAll(updateSub0);
            updateList.add(update.sub(1));
        }else {
            updateList.add(update);
        }
        return updateList;
    }

    @Override
    public Term toTerm(Term el) {
        return el;
    }

    @Override
    public Term getFirst() {
        return updateList.getFirst();
    }

    @Override
    public Term getLast() {
        return updateList.getLast();
    }

    public int getSize(){
        return updateList.size();
    }

    @Override
    public List<Term> getList() {
        return updateList;
    }

    @Override
    public Term getTermFromList() {
        Term update = this.updateList.getFirst();
        for (int i = 1; i < this.updateList.size(); i++) {
            update = getSequentialUpdate(update, this.updateList.get(i), services);
        }
        return update;
    }

    @Override
    public Term getTermFromSubList(int begin, int end) {
        if(begin < 0)
            throw new RuntimeException("Negative index for list");
        if (end > this.updateList.size())
            throw new RuntimeException("Index out of bound: " + end + " for list of length " + this.updateList.size());
        List<Term> sublist =this.updateList.subList(begin, end);
        Term update = sublist.getFirst();
        for (int i = 1; i < sublist.size(); i++) {
            update = getSequentialUpdate(update, sublist.get(i), services);
        }
        return update;
    }
//
//    @Override
//    public void addLast(Term elem) {
//
//    }



    public boolean hasStrictPrefix(UpdateManager possiblePrefix){
        if(possiblePrefix.updateList.size() >= this.updateList.size())
            return false;
        return this.updateList.subList(0,possiblePrefix.updateList.size()).equals(possiblePrefix.updateList);
    }

    public static Term getSequentialUpdate(Term update1, Term update2, Services services){
        return services.getTermFactory().createTerm(UpdateJunctor.SEQUENTIAL_UPDATE, update1, update2);
    }


}
