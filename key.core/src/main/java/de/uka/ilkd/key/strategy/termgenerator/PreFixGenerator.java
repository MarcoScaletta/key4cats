package de.uka.ilkd.key.strategy.termgenerator;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.rule.conditions.catsconditions.IsSchematicTraceOverM;
import de.uka.ilkd.key.strategy.feature.MutableState;

import java.util.*;
public class PreFixGenerator implements TermGenerator {


    @Override
    public Iterator<Term> generate(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        TacletApp tApp = (TacletApp) app;
        Term updateTerm = (Term) tApp.instantiations().lookupValue(new Name("update"));
        Term traceTerm = (Term) tApp.instantiations().lookupValue(new Name("traceFormula"));
        Services services = goal.proof().getServices();

        UpdateManager updateManager = new UpdateManager(updateTerm, services);
        TraceManager traceManager = new TraceManager(traceTerm, services);
        return matchRunEv(updateManager,traceManager,services).iterator();
    }

    public List<Term> matchRunEv(UpdateManager updateManager, TraceManager traceManager, Services services){

        if(traceManager.getSize() > 1 &&
            updateManager.getLast().op() == UpdateEvent.RUN_EV &&
            traceManager.getLast().first.op() == TraceEvent.POP_TR_EV &&
            traceManager.get(traceManager.getSize()-2).first.op() instanceof SchematicTrace){

            List<Term> candidates = new ArrayList<>();
            Term runUpEv = updateManager.getLast();
            Term popTrEv = traceManager.getLast().first;
            Term schemTr = traceManager.get(traceManager.getSize()-2).first;

            if(UpdateEvent.runEventMatchesTraceEvent(runUpEv,popTrEv) && // same procName,callId (or wildcard)
                    !IsSchematicTraceOverM.isSchematicTraceOverM(schemTr, runUpEv.sub(0))){ // procName not forbidden by schemTr
                Term updateTerm = updateManager.getPrefixButLast(); // update without last  event (run event)
                candidates.add(traceManager.getPrefixButLast());
                if(traceManager.getSize() > 2)
                    candidates.add(traceManager.getPrefixTerm(traceManager.getSize()-2));
                return getListOfJudgments(updateTerm, candidates,services);
            }
        }
        return List.of();
    }



    private List<Term> getListOfJudgments(Term update, List<Term> traces, Services services){
        return traces.stream().map(
                trace -> services.getTermFactory().createTerm(UpdateApplication.UPDATE_APPLICATION,
                        update,
                        services.getTermFactory().createTerm(Modality.DIA, trace))).toList();
    }



}
