package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.nparser.KeYLexer;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class TraceEvent extends AbstractSortedOperator {

    private final static WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceEvent>> START_EV = new WeakHashMap<>();
    private final static WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceEvent>> POP_EV = new WeakHashMap<>();
    private final static WeakHashMap<Sort,WeakReference<TraceEvent>> RET_EV = new WeakHashMap<>();

    public synchronized static TraceEvent getMethodIntSortEv(int eventId, Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Pair<Sort,Sort> runEvSig = new Pair<>(methodNameSort,intSort);

        WeakHashMap<Pair<Sort,Sort>,WeakReference<TraceEvent>> eventMap = null;
        String nameStr = null;

        switch(eventId){
            case(KeYLexer.START_TR_EV)->{eventMap = START_EV; nameStr="\\startEv";}
            case(KeYLexer.POP_TR_EV)->{eventMap = POP_EV; nameStr="\\popEv";}
        }

        if(eventMap == null)
            return null;

        WeakReference<TraceEvent> ref = eventMap.get(runEvSig);
        TraceEvent result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceEvent(new Name(nameStr), methodNameSort, intSort);
            eventMap.put(runEvSig, new WeakReference<>(result));
        }

        return result;
    }


    public synchronized static TraceEvent getStartEv(Services services){
        return getMethodIntSortEv(KeYLexer.START_TR_EV,services);
    }
    public synchronized static TraceEvent getPopEv(Services services){
        return getMethodIntSortEv(KeYLexer.POP_TR_EV,services);
    }

    public synchronized static TraceEvent getRetEv(Services services){

        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();

        WeakReference<TraceEvent> ref = RET_EV.get(intSort);
        TraceEvent result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new TraceEvent(new Name("\\retEv"), intSort);
            RET_EV.put(intSort, new WeakReference<>(result));
        }

        return result;
    }

    private TraceEvent(Name name, Sort... sorts) {
        super(name, sorts, Sort.FORMULA, false);
    }
}
