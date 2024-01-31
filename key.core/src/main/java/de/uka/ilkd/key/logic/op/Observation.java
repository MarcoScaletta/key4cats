package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;
import org.key_project.util.collection.ImmutableArray;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class Observation extends AbstractSortedOperator{

    private static final WeakHashMap<UpdateableOperator, WeakReference<Observation>> instances =
            new WeakHashMap<>();

    private Observation(UpdateableOperator lhs) {
        super(new Name("\\obs_" + lhs.name()), new ImmutableArray<>(Sort.ANY), Sort.FORMULA,true);
        this.observedVariable = lhs;
    }

    private final UpdateableOperator observedVariable;


    /**
     * Returns the elementary update operator for the passed left hand side.
     */
    public static Observation getInstance(UpdateableOperator lhs) {
        WeakReference<Observation> ref = instances.get(lhs);
        Observation result = null;
        if (ref != null) {
            result = ref.get();
        }
        if (result == null) {
            result = new Observation(lhs);
            ref = new WeakReference<>(result);
            instances.put(lhs, ref);
        }
        return result;
    }

    public UpdateableOperator observed() {
        return observedVariable;
    }
}
