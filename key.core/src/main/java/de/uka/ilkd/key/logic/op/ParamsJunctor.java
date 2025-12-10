package de.uka.ilkd.key.logic.op;


import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class ParamsJunctor extends AbstractSortedOperator{

    public static final ParamsJunctor PARAMS_JUNCTOR = new ParamsJunctor(new Name(","), Sort.ANY,Sort.ANY);
    public static final ParamsJunctor EMPTY_JUNCTOR = new ParamsJunctor(new Name("none"));
    private ParamsJunctor(Name name, Sort... sorts) {
        super(name ,  sorts, Sort.ANY, true);
    }
}