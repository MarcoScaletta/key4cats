package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class ObservationArg extends AbstractSortedOperator{

    public static final ObservationArg OBS_ARG = new ObservationArg(new Name("obsArg"), Sort.ANY, Sort.ANY);

    private ObservationArg(Name name, Sort... sorts) {
        super(name, sorts, Sort.ANY, true);
    }
}
