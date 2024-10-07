package de.uka.ilkd.key.strategy.termgenerator;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.rule.conditions.catsconditions.ContainsObservations;
import de.uka.ilkd.key.rule.conditions.catsconditions.IsSchematicTraceOverM;
import de.uka.ilkd.key.strategy.feature.MutableState;
import de.uka.ilkd.key.util.Pair;

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



        return Collections.emptyIterator();
    }



    private List<Term> getListOfJudgments(Term update, List<Term> traces, Services services){
        return traces.stream().map(
                trace -> services.getTermFactory().createTerm(UpdateApplication.UPDATE_APPLICATION,
                        update,
                        services.getTermFactory().createTerm(Modality.DIA, trace))).toList();
    }



}
