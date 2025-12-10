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

    public final static TraceEvent START_TR_EV = new TraceEvent(new Name("\\startTrEv"), Sort.ANY,Sort.ANY);
    public final static TraceEvent POP_TR_EV = new TraceEvent(new Name("\\popTrEv"), Sort.ANY,Sort.ANY);
    public final static TraceEvent RET_TR_EV = new TraceEvent(new Name("\\retTrEv"), Sort.ANY);

    public final static TraceEvent AWAIT_TR_EV = new TraceEvent(new Name("\\awaitTrEv"));
    public final static TraceEvent REACT_TR_EV = new TraceEvent(new Name("\\reactTrEv"));
    public final static TraceEvent INVOC_TR_EV = new TraceEvent(new Name("\\invocTrEv"), Sort.ANY, Sort.ANY,Sort.ANY);

    private TraceEvent(Name name, Sort... sorts) {
        super(name, sorts, Sort.FORMULA, false);
    }
}
