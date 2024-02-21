package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class HavocUpdate extends AbstractSortedOperator {
    // TODO: possible memory leak, need to keep weak hash map
    private final static HashMap<Sort,WeakReference<HavocUpdate>> ANON = new HashMap<>();

    public synchronized static HavocUpdate getHavocUpdate(Services services){

        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();

        WeakReference<HavocUpdate> ref = ANON.get(intSort);
        HavocUpdate result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new HavocUpdate(new Name("\\havoc"), intSort);
            ANON.put(intSort, new WeakReference<>(result));
        }

        return result;
    }
    private HavocUpdate(Name name, Sort... sorts) {
        super(name, sorts, Sort.UPDATE, true);
    }
}
