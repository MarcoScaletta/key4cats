package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.SchematicTrace;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

import java.util.Set;

/**
 * @author Marco Scaletta
 */
public class SchematicTraceInclusion extends VariableConditionAdapter {


    private final SchemaVariable schemTr1SV;
    private final SchemaVariable schemTr2SV;

    private final boolean negated;


    public SchematicTraceInclusion(SchemaVariable schemTr1, SchemaVariable schemTr2, boolean negated) {
        this.schemTr1SV = schemTr1;
        this.schemTr2SV = schemTr2;
        this.negated = negated;
    }


    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations svInst, Services services) {
        Term schemTr1Term = (Term) svInst.getInstantiation(schemTr1SV);
        Term schemTr2Term = (Term) svInst.getInstantiation(schemTr2SV);

        if(schemTr1Term == null || schemTr2Term == null)
            return true;
        if(schemTr1Term.op() != SchematicTrace.SCHEM_TRACE || schemTr2Term.op() != SchematicTrace.SCHEM_TRACE)
            return negated;
        Set<Term> forbProcs1 = IsSchematicTraceOverM.getForbiddenProcsRec(schemTr1Term.sub(0));
        Set<Term> forbProcs2 = IsSchematicTraceOverM.getForbiddenProcsRec(schemTr2Term.sub(0));
        return
                negated != forbProcs2.containsAll(forbProcs1);
    }

}
