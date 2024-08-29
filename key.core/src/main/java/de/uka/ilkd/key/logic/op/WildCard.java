package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

import java.util.HashMap;

public class WildCard extends AbstractSortedOperator {

    private final static HashMap<Sort, WildCard> wildcards = new HashMap<>();

    public synchronized static WildCard getWildCard(){
        if(wildcards.isEmpty())
            wildcards.put(Sort.ANY, new WildCard());
        return wildcards.get(Sort.ANY);
    }

    private WildCard() {
        super(new Name("$?"), Sort.ANY, true);
    }

}
