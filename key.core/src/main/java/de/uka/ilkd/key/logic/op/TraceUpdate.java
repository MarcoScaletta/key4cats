package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class TraceUpdate extends AbstractSortedOperator {

    public static final TraceUpdate RUN_EV = new TraceUpdate(new Name("run"),3);

    private static Sort[] createUpdateSortArray(int arity) {
        Sort[] result = new Sort[arity];
        for (int i = 0; i < arity; i++) {
            result[i] = Sort.ANY;
        }
        return result;
    }


    private TraceUpdate(Name name, int arity) {
        super(name, createUpdateSortArray(arity), Sort.UPDATE, false);
    }
}
