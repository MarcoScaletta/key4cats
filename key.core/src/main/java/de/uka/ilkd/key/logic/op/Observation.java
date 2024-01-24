package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class Observation extends AbstractSortedOperator{

    public static final Observation OBS = new Observation(new Name("\\obs"), Sort.ANY);

    private Observation(Name name, Sort... sorts) {
        super(name, sorts, Sort.FORMULA, true);
    }

}
