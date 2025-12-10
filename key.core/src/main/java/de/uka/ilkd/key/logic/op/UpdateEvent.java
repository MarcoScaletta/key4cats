package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.sort.Sort;

public class UpdateEvent extends AbstractSortedOperator {
    // TODO: possible memory leak, need to keep weak hash map

    public final static UpdateEvent RUN_EV = new UpdateEvent(new Name("\\runEv"), Sort.ANY,Sort.ANY, Sort.ANY);

    public final static UpdateEvent START_EV = new UpdateEvent(new Name("\\startEv"), Sort.ANY,Sort.ANY);
    public final static UpdateEvent POP_EV = new UpdateEvent(new Name("\\popEv"), Sort.ANY,Sort.ANY);
    public final static UpdateEvent INVOC_EV = new UpdateEvent(new Name("\\invocEv"), Sort.ANY,Sort.ANY,Sort.ANY);
    public final static UpdateEvent AWAIT_EV = new UpdateEvent(new Name("\\awaitEv"), Sort.ANY);
    public final static UpdateEvent REACT_EV = new UpdateEvent(new Name("\\reactEv"), Sort.ANY);
    public final static UpdateEvent RET_EV = new UpdateEvent(new Name("\\retEv"), Sort.ANY);

//    private final static Set<UpdateEvent> ctxEvents = Set.of(RUN_EV,START_EV,POP_EV,INVOC_EV);

    private UpdateEvent(Name name, Sort... sorts) {
        super(name, sorts, Sort.UPDATE, false);
    }

    public static boolean runEventMatchesTraceEvent(Term updateEv, Term traceEv){
        if(updateEv.op() == RUN_EV &&
            (traceEv.op() == TraceEvent.START_TR_EV || traceEv.op() == TraceEvent.POP_TR_EV )){
            return (updateEv.sub(0) == traceEv.sub(0)  //checking if same method name
                    && (traceEv.sub(1).op() == SpecialCallIds.wildcard  // startTrEv has wildcard ID
                        || updateEv.sub(1) == traceEv.sub(1))); // same callId for runEV and startTrEv
        }
        return false;
    }
}
