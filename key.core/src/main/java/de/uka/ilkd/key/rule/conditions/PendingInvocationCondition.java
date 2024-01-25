package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.java.reference.MethodName;
import de.uka.ilkd.key.java.reference.TypeReference;
import de.uka.ilkd.key.ldt.BooleanLDT;
import de.uka.ilkd.key.ldt.MethodNameLDT;
import de.uka.ilkd.key.logic.ProgramElementName;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermFactory;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import de.uka.ilkd.key.util.Pair;
import org.key_project.util.collection.ImmutableArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PendingInvocationCondition implements VariableCondition {


    private final SchemaVariable u;
    private final SchemaVariable methodName;
    private final SchemaVariable callId;

    public PendingInvocationCondition(SchemaVariable u, SchemaVariable methodName, SchemaVariable callId) {
        this.u = u;
        this.methodName = methodName;
        this.callId = callId;
    }

    private Map<Term, Pair<Term, Boolean>> getScheduleHelper(Term update, Map<Term, Pair<Term, Boolean>> map, Services services){
        if(update.op() == UpdateJunctor.SEQUENTIAL_UPDATE)
            map = getScheduleHelper(update.sub(1), getScheduleHelper(update.sub(0), map, services ), services);
        else if (update.op() == TraceUpdate.getInvocEv(services)) {
            Term callId = update.sub(1);
            if (map.containsKey(callId))
                throw new RuntimeException("There is more than one asynchronous invocation with same id \"" + update + "\".");
            map.put(callId, new Pair<>(update, true));
        } else if (update.op() == TraceUpdate.getRunEv(services)) {
            Term runEvMethod = update.sub(0);
            Term runEvCallId = update.sub(1);
            Term runEvflag = update.sub(2);
            Pair<Term, Boolean> pair = map.get(runEvCallId);
            if (pair != null) {
                Term invocEvTerm = pair.first;
                if (runEvflag.op() == services.getTypeConverter().getBooleanLDT().getTrueTerm())
                    throw new RuntimeException("Synchronous run event with same id as an asynchronous invocation: " + update.op() + ", " + invocEvTerm);
                Term invocEvMethod = invocEvTerm.sub(0);
                if (runEvMethod != invocEvMethod)
                    throw new RuntimeException("Mismatch of method name for run event "+ update.op() +" and invoc events "+invocEvTerm+" with same callId:" + runEvCallId);
                Term invocEvCallId = invocEvTerm.sub(1);
                if(runEvCallId != invocEvCallId)
                    throw new RuntimeException("Mismatch of callId for invoc event " + invocEvTerm + ", saved with callId " + runEvCallId);
                if(!pair.second)
                    throw new RuntimeException("Invoc event " + invocEvTerm + " is bound to multiple run events " + update.op());
                map.put(runEvCallId, new Pair<>(map.get(runEvCallId).first, false));
            }
        }
        return map;
    }

    private Term getSchedule(Term update, Services services){
        Map<Term, Pair<Term, Boolean>> mapSchedule = getScheduleHelper(update, new HashMap<>(), services);

        List<Pair<Term,Boolean>> idlingInvocactions = mapSchedule.values().stream().filter(pair -> pair.second).toList();
        if(idlingInvocactions.isEmpty())
            return null;
        return idlingInvocactions.get(0).first;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions mc, Services services) {

        SVInstantiations svInst = mc.getInstantiations();
        Term update = (Term) svInst.getInstantiation(u);

        if (update == null || methodName==null || callId==null) {
            return mc;
        }
        TermFactory tf = services.getTermFactory();
        Term idlingInvoEv = getSchedule(update,services);
        if(idlingInvoEv == null)
            return null;
        Term t = (Term) idlingInvoEv.sub(0);
        Term t1 = idlingInvoEv.sub(1);
        Term result = services.getTermBuilder().func(services.getTypeConverter().getMethodNameLDT().getUniqueMethodConstant(
                ((ProgramElementName) t.op().name()).getQualifier(),
                ((ProgramElementName) t.op().name()).getProgramName(),services));

        return mc.setInstantiations(svInst.add(methodName, result, services).add(callId, t1, services));
    }
}
