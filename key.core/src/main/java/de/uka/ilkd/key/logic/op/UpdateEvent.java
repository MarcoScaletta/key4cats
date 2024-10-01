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

    public final static UpdateEvent RUN_EV = new UpdateEvent(new Name("\\runEv"), Sort.ANY,Sort.ANY, Sort.ANY);

    public final static UpdateEvent START_EV = new UpdateEvent(new Name("\\startEv"), Sort.ANY,Sort.ANY);
    public final static UpdateEvent POP_EV = new UpdateEvent(new Name("\\popEv"), Sort.ANY,Sort.ANY);
    public final static UpdateEvent INVOC_EV = new UpdateEvent(new Name("\\invocEv"), Sort.ANY,Sort.ANY);

    public final static UpdateEvent RET_EV = new UpdateEvent(new Name("\\retEv"), Sort.ANY);

    private UpdateEvent(Name name, Sort... sorts) {
        super(name, sorts, Sort.UPDATE, false);
    }
}
