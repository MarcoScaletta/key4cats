package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
/**
 * @author Marco Scaletta
 */
public class FinishCondition implements VariableCondition {


    private final SchemaVariable u;
    private final SchemaVariable methodNameSV;

    public FinishCondition(SchemaVariable u, SchemaVariable methodNameSV) {
        this.u = u;
        this.methodNameSV = methodNameSV;
    }

    public Term getStartEvent(Term update, Services services){
        if(update.op() == UpdateJunctor.SEQUENTIAL_UPDATE) {
            if (update.sub(0).op() == HavocUpdate.getHavocUpdate(services)) {
                if (update.sub(1).op() == UpdateEvent.START_EV)
                    return update.sub(1);
            } else
                return getStartEvent(update.sub(0), services);
        }
        return null;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions mc, Services services) {

        SVInstantiations svInst = mc.getInstantiations();
        Term update = (Term) svInst.getInstantiation(u);

        if (update == null) {
            return mc;
        }
        Term idlingInvoEv = PendingInvocationCondition.getSchedule(update,services);
        Term startEv = getStartEvent(update, services);

        if(idlingInvoEv != null || startEv == null)
            return null;

        return mc.setInstantiations(svInst.add(this.methodNameSV, startEv.sub(0), services));
    }
}
