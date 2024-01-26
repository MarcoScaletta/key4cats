package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class TraceContract extends AbstractSortedOperator {

    public final static TraceContract TRACE_CONTRACT = new TraceContract(new Name("\\traceContract"), Sort.ANY, Sort.FORMULA,Sort.FORMULA,Sort.FORMULA);

    private TraceContract(Name name, Sort... sorts) {
        super(name, sorts, Sort.FORMULA, false);
    }
}
