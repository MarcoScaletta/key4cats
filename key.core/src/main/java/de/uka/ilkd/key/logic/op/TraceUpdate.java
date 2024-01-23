package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.nparser.KeYLexer;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.lang.ref.WeakReference;
import java.security.Key;
import java.util.WeakHashMap;

public class TraceUpdate extends AbstractSortedOperator {

    private final static WeakHashMap<Triple<Sort,Sort,Sort>,WeakReference<TraceUpdate>> RUN_EV = new WeakHashMap<>();
    private final static WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceUpdate>> START_EV = new WeakHashMap<>();

    public synchronized static TraceUpdate getRunEv(Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Sort boolSort = services.getTypeConverter().getBooleanLDT().targetSort();
        final Triple runEvSig = new Triple(methodNameSort,intSort,boolSort);

        WeakReference<TraceUpdate> ref = RUN_EV.get(runEvSig);
        TraceUpdate result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceUpdate(new Name("\\runEv"), methodNameSort, intSort, boolSort);
            RUN_EV.put(runEvSig, new WeakReference<>(result));
        }

        return result;
    }
    public synchronized static TraceUpdate getStartEv(Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Pair runEvSig = new Pair(methodNameSort,intSort);

        WeakReference<TraceUpdate> ref = START_EV.get(runEvSig);
        TraceUpdate result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceUpdate(new Name("\\startEv"), methodNameSort, intSort);
            START_EV.put(runEvSig, new WeakReference<>(result));
        }

        return result;
    }
    private TraceUpdate(Name name, Sort... sorts) {
        super(name, sorts, Sort.UPDATE, false);
    }
}
