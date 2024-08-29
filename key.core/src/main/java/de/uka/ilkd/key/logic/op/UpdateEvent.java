package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.nparser.KeYLexer;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class UpdateEvent extends AbstractSortedOperator {
    // TODO: possible memory leak, need to keep weak hash map
    private final static HashMap<Triple<Sort,Sort,Sort>,WeakReference<UpdateEvent>> RUN_EV = new HashMap<>();
    private final static HashMap<Pair<Sort,Sort>,WeakReference<UpdateEvent>> START_EV = new HashMap<>();
    private final static HashMap<Pair<Sort,Sort>,WeakReference<UpdateEvent>> INVOC_EV = new HashMap<>();
    private final static HashMap<Pair<Sort,Sort>,WeakReference<UpdateEvent>> POP_EV = new HashMap<>();
    private final static HashMap<Sort,WeakReference<UpdateEvent>> RET_EV = new HashMap<>();


    public synchronized static UpdateEvent getRunEv(Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Sort boolSort = services.getTypeConverter().getBooleanLDT().targetSort();
        final Triple runEvSig = new Triple(methodNameSort,intSort,boolSort);

        WeakReference<UpdateEvent> ref = RUN_EV.get(runEvSig);
        UpdateEvent result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new UpdateEvent(new Name("\\runEv"), methodNameSort, intSort, boolSort);
            RUN_EV.put(runEvSig, new WeakReference<>(result));
        }

        return result;
    }
    private synchronized static UpdateEvent getProcNameIdEvWrapper(int evId, Services services){

        final Sort methodNameSort = services.getTypeConverter().getMethodNameLDT().targetSort();
        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();
        final Pair<Sort,Sort> runEvSig = new Pair<>(methodNameSort,intSort);
        String nameStr = null;
        HashMap<Pair<Sort,Sort>,WeakReference<UpdateEvent>> event = null;
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
        WeakReference<UpdateEvent> ref = event.get(runEvSig);
        UpdateEvent result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new UpdateEvent(new Name(nameStr), methodNameSort, intSort);
            event.put(runEvSig, new WeakReference<>(result));
        }
        return result;
    }

    public synchronized static UpdateEvent getStartEv(Services services){
        return getProcNameIdEvWrapper(KeYLexer.START_EV, services);
    }

    public synchronized static UpdateEvent getInvocEv(Services services){
        return getProcNameIdEvWrapper(KeYLexer.INVOC_EV, services);
    }

    public synchronized static UpdateEvent getPopEv(Services services){
        return getProcNameIdEvWrapper(KeYLexer.POP_EV, services);
    }

    public synchronized static UpdateEvent getRetEv(Services services){

        final Sort intSort = services.getTypeConverter().getIntegerLDT().targetSort();

        WeakReference<UpdateEvent> ref = RET_EV.get(intSort);
        UpdateEvent result = null;
        if (ref != null) {
            result = ref.get();
        }
        if(result == null){
            result = new UpdateEvent(new Name("\\retEv"), intSort);
            RET_EV.put(intSort, new WeakReference<>(result));
        }

        return result;
    }
    private UpdateEvent(Name name, Sort... sorts) {
        super(name, sorts, Sort.UPDATE, false);
    }
}
