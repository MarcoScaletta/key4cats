package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class TraceEvent extends AbstractSortedOperator {

    private final static WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceEvent>> START_EV = new WeakHashMap<>();

    public synchronized static TraceEvent getStartEv(Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Pair<Sort,Sort> runEvSig = new Pair<>(methodNameSort,intSort);

        WeakReference<TraceEvent> ref = START_EV.get(runEvSig);
        TraceEvent result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceEvent(new Name("\\startEv"), methodNameSort, intSort);
            START_EV.put(runEvSig, new WeakReference<>(result));
        }

        return result;
    }
    private TraceEvent(Name name, Sort... sorts) {
        super(name, sorts, Sort.FORMULA, false);
    }
}
