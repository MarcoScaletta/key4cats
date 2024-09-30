package de.uka.ilkd.key.strategy.termgenerator;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.Modality;
import de.uka.ilkd.key.logic.op.SchematicTrace;
import de.uka.ilkd.key.logic.op.UpdateApplication;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.rule.conditions.catsconditions.ChopForCall;
import de.uka.ilkd.key.rule.conditions.catsconditions.ContainsObservations;
import de.uka.ilkd.key.strategy.feature.MutableState;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.util.*;

public class PostFixGenerator implements TermGenerator {


    @Override
    public Iterator<Term> generate(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        TacletApp tApp = (TacletApp) app;
        Term updateTerm = (Term) tApp.instantiations().lookupValue(new Name("update"));
        Term traceTerm = (Term) tApp.instantiations().lookupValue(new Name("traceFormula"));
        Services services = goal.proof().getServices();


        UpdateManager updateManager = new UpdateManager(updateTerm, services);
        TraceManager traceManager = new TraceManager(traceTerm, services);

        Sequent seq = goal.sequent();
        List<Pair<UpdateManager,TraceManager>> judgmentsAnte =
                seq.antecedent().asList().stream().filter(
                        x -> x.formula().op() instanceof UpdateApplication && x.formula().sub(1).op() instanceof Modality)
                        .map(x -> new Pair<>(
                                new UpdateManager(x.formula().sub(0).sub(0),services),
                                new TraceManager(x.formula().sub(1).sub(0),services)
                                )
                        ).filter(
                                judgment -> updateManager.hasStrictPrefix(judgment.first)
                                        && traceManager.hasStrictPrefix(judgment.second)
                        ).toList();

        Optional<Pair<UpdateManager,TraceManager>> optionalJugdmentWithLongestUpdate =
                judgmentsAnte.stream().max(
                        Comparator.comparingInt(o -> o.first.getSize())
                );

        if(optionalJugdmentWithLongestUpdate.isEmpty())
            return Collections.emptyIterator();

        UpdateManager updatePrefix = optionalJugdmentWithLongestUpdate.get().first;
        TraceManager tracePrefix = optionalJugdmentWithLongestUpdate.get().second;

        Pair<Term,Junctor> lastPrefix = traceManager.getTracePairs().get(tracePrefix.getSize());


        // if isPostfixExtandable then we have the rule applied to
        //      u1 : phi1 ~~ |- u1;u2 : phi1 ~~ ** phi2
        boolean isPostfixExtandable = lastPrefix.second == Junctor.CHOP && lastPrefix.first.op() instanceof SchematicTrace;

        Term updatePostfix = UpdateManager.getUpdateFromList(
                updateManager.getUpdateList().subList(updatePrefix.getSize(), updateManager.getSize()),
                services);
        Term tracePostFix = TraceManager.getTraceFromList(
                traceManager.getTracePairs().subList(tracePrefix.getSize(), traceManager.getSize()),
                services);

        if(ContainsObservations.containsObservations(tracePostFix))
            return Collections.emptyIterator();

        List<Term> jugdmentPostFixes = new ArrayList<>();


        jugdmentPostFixes.add(
                services.getTermFactory().createTerm(UpdateApplication.UPDATE_APPLICATION,
                        updatePostfix,
                        services.getTermFactory().createTerm(Modality.DIA, tracePostFix)));
        //If the rule is applied to
        // u1 : phi1 ~~ |- u1;u2 : phi1 ~~ ** phi2
        // there are 2 possible postfixes
        //      -> u2 : phi2
        //      -> u2 : ~~ ** phi2
        if(isPostfixExtandable)
                jugdmentPostFixes.add(
                        services.getTermFactory().createTerm(UpdateApplication.UPDATE_APPLICATION,
                                updatePostfix,
                                services.getTermFactory().createTerm(Modality.DIA, TraceManager.unchop(lastPrefix.first,tracePostFix,services))));
        return jugdmentPostFixes.iterator();
    }



}
