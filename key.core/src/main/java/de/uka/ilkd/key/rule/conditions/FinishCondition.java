package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.JavaBlock;
import de.uka.ilkd.key.logic.ProgramElementName;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FinishCondition implements VariableCondition {


    private final SchemaVariable u;
    private final SchemaVariable modality;
    private final SchemaVariable post;

    public FinishCondition(SchemaVariable u, SchemaVariable modality, SchemaVariable post) {
        this.u = u;
        this.modality = modality;
        this.post = post;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions mc, Services services) {

        SVInstantiations svInst = mc.getInstantiations();
        Term update = (Term) svInst.getInstantiation(u);
        Term postTerm = (Term) svInst.getInstantiation(post);

        if (update == null) {
            return mc;
        }
        Term idlingInvoEv = PendingInvocationCondition.getSchedule(update,services);
        if(idlingInvoEv != null)
            return null;
        Term localJudge = services.getTermBuilder().prog(Modality.DIA, JavaBlock.EMPTY_JAVABLOCK,postTerm) ;
        return mc.setInstantiations(svInst.add(modality,localJudge, services));
    }
}
