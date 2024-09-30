package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.UpdateJunctor;
import de.uka.ilkd.key.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UpdateManager {

    private List<Term> updateList;

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

    public int getSize(){
        return updateList.size();
    }

    public List<Term> getUpdateList(){
        return updateList;
    }

    public boolean hasStrictPrefix(UpdateManager possiblePrefix){
        if(possiblePrefix.updateList.size() >= this.updateList.size())
            return false;
        return this.updateList.subList(0,possiblePrefix.updateList.size()).equals(possiblePrefix.updateList);
    }

    public static Term getUpdateFromList(List<Term> updateAsList, Services services){
        Term update = updateAsList.getFirst();
        for (int i = 1; i < updateAsList.size(); i++) {
            update = getSequentialUpdate(update, updateAsList.get(i), services);
        }
        return update;
    }

    public static Term getSequentialUpdate(Term update1, Term update2, Services services){
        return services.getTermFactory().createTerm(UpdateJunctor.SEQUENTIAL_UPDATE, update1, update2);
    }


}
