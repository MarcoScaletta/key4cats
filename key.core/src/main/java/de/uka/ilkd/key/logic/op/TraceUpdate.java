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
    private final static WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceUpdate>> INVOC_EV = new WeakHashMap<>();
    private final static WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceUpdate>> POP_EV = new WeakHashMap<>();
    private final static WeakHashMap<Sort,WeakReference<TraceUpdate>> RET_EV = new WeakHashMap<>();


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
    private synchronized static TraceUpdate getProcNameIdEvWrapper(int evId, Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Pair runEvSig = new Pair(methodNameSort,intSort);
        String nameStr = null;
        WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceUpdate>> event = null;
            switch(evId){
                case KeYLexer.START_EV -> {
                    event = START_EV;
                    nameStr = "\\startEv";
                }
                case KeYLexer.INVOC_EV -> {
                    event = INVOC_EV;
                    nameStr = "\\invocEv";
                }
                case KeYLexer.POP_EV -> {
                    event = POP_EV;
                    nameStr = "\\popEv";
                }
            };
        if(event == null)
            return null;
        WeakReference<TraceUpdate> ref = event.get(runEvSig);
        TraceUpdate result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceUpdate(new Name(nameStr), methodNameSort, intSort);
            event.put(runEvSig, new WeakReference<>(result));
        }

        return result;
    }

    public synchronized static TraceUpdate getStartEv(Services services){
        return getProcNameIdEvWrapper(KeYLexer.START_EV, services);
    }

    public synchronized static TraceUpdate getInvocEv(Services services){
        return getProcNameIdEvWrapper(KeYLexer.INVOC_EV, services);
    }

    public synchronized static TraceUpdate getPopEv(Services services){
        return getProcNameIdEvWrapper(KeYLexer.POP_EV, services);
    }

    public synchronized static TraceUpdate getRetEv(Services services){

        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();

        WeakReference<TraceUpdate> ref = RET_EV.get(intSort);
        TraceUpdate result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceUpdate(new Name("\\runEv"), intSort);
            RET_EV.put(intSort, new WeakReference<>(result));
        }

        return result;
    }
    private TraceUpdate(Name name, Sort... sorts) {
        super(name, sorts, Sort.UPDATE, false);
    }
}
