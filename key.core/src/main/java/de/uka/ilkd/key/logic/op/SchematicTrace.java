package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;
import org.key_project.util.collection.ImmutableArray;

import java.util.ArrayList;

public class SchematicTrace extends AbstractSortedOperator{

    public static final SchematicTrace SCHEM_TRACE = new SchematicTrace(new Name("\\schemTr"), Sort.ANY);
    public static final SchematicTrace SCHEM_TRACE_TRIV = new SchematicTrace(new Name("\\schemTr"));

    private SchematicTrace(Name name, Sort... sorts) {
        super(name, sorts, Sort.FORMULA, true);
    }
}
