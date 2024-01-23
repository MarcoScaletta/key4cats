package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.util.Triple;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class TraceUpdate extends AbstractSortedOperator {

    private final static WeakHashMap<Triple<Sort,Sort,Sort>,WeakReference<TraceUpdate>> RUN_EV = new WeakHashMap<>();
    public synchronized static TraceUpdate getRunEv(Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Triple runEvSig = new Triple(methodNameSort,intSort,Sort.ANY);

        WeakReference<TraceUpdate> ref = RUN_EV.get(runEvSig);
        TraceUpdate result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceUpdate(new Name("run"), methodNameSort, intSort, Sort.ANY);
            RUN_EV.put(runEvSig, new WeakReference<>(result));
        }

        return result;
    }
    private TraceUpdate(Name name, Sort... sorts) {
        super(name, sorts, Sort.UPDATE, false);
    }
}
