package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class ObservationArgJunctor extends AbstractSortedOperator{

    public static final ObservationArgJunctor OBS_ARG_JUNCTOR = new ObservationArgJunctor(new Name(","), Sort.ANY, Sort.ANY);

    private ObservationArgJunctor(Name name, Sort... sorts) {
        super(name, sorts, Sort.ANY, true);
    }
}
