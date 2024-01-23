package de.uka.ilkd.key.logic.op;


import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class SchematicTraceJunctor extends AbstractSortedOperator{

    public static final SchematicTraceJunctor SCHEM_TRACE_JUNCTOR = new SchematicTraceJunctor(new Name(","), Sort.ANY,Sort.ANY);
    private SchematicTraceJunctor(Name name, Sort... sorts) {
        super(name ,  sorts, Sort.ANY, true);
    }
}