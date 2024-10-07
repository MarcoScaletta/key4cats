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
        List<Pair<UpdateManager,TraceManager>> judgmentsAnte = getMatchingJudgmentsFromAnte(seq,updateManager,traceManager,services);
        Optional<Pair<UpdateManager,TraceManager>> optionalJugdmentWithLongestUpdate =
                judgmentsAnte.stream().max(Comparator.comparingInt(o -> o.first.getSize()));
        if(optionalJugdmentWithLongestUpdate.isEmpty()) {
            //there is no matching prefix in the antencedent
            //we can check here if: {runEv(m,i);U} : startEv(m,_) ** phi
            return postFixLocally(traceManager,updateManager,services);

        }
        return getIteratorFromStrictPrefixes(
                traceManager,
                updateManager,
                optionalJugdmentWithLongestUpdate.get(),
                services);
    }

    private List<Pair<UpdateManager,TraceManager>> getMatchingJudgmentsFromAnte(
            Sequent seq,
            UpdateManager updateManager,
            TraceManager traceManager,
            Services services
            ){
        return seq.antecedent().asList().stream().filter(
                                x -> x.formula().op() instanceof UpdateApplication && x.formula().sub(1).op() instanceof Modality)
                        .map(x -> new Pair<>(
                                        new UpdateManager(x.formula().sub(0).sub(0),services),
                                        new TraceManager(x.formula().sub(1).sub(0),services)
                                )
                        ).filter(
                                judgment -> updateManager.hasStrictPrefix(judgment.first)
                                        && traceManager.hasStrictPrefix(judgment.second)
                        ).toList();
    }

    private Iterator<Term> elimSchemTrPrefixMatchedWithRunEvPrefix(
            TraceManager traceManager,
            UpdateManager updateManager,
            Services services){

            Term runEv = updateManager.getFirst();
            Term schemTr = traceManager.getTracePairs().getFirst().first;
            if(IsSchematicTraceOverM.isSchematicTraceOverM(schemTr, runEv.sub(0)))
                return getListOfJudgments(
                        updateManager.getTermFromList(),
                        List.of(traceManager.getTermFromSubList(1,traceManager.getTracePairs().size())),
                        services).iterator();

        return Collections.emptyIterator();
    }

    private Iterator<Term> postFixLocally(
            TraceManager traceManager,
            UpdateManager updateManager,
            Services services
    ){
        //todo:check if the sizes of trace manager can be lower
        if(updateManager.getSize() > 1
            && traceManager.getSize() > 1
            && updateManager.getFirst().op() == UpdateEvent.RUN_EV
        ){
            if(traceManager.getTracePairs().getFirst().first.op() instanceof SchematicTrace)
                return elimSchemTrPrefixMatchedWithRunEvPrefix(traceManager, updateManager, services);
            else if(traceManager.getTracePairs().getFirst().first.op() == TraceEvent.START_TR_EV
                && traceManager.getTracePairs().get(1).first.op() instanceof SchematicTrace) {
                //elimSchemTrPrefixMatchedWithRunEvPrefix
                Term runEv = updateManager.getFirst();
                Term startTrEv = traceManager.getTracePairs().getFirst().first;
                Term schemTr = traceManager.getTracePairs().get(1).first;
                if (runEv.sub(0) == startTrEv.sub(0)  //checking if same method name
                        && !IsSchematicTraceOverM.isSchematicTraceOverM(schemTr, runEv.sub(0)) //schemTr not forbidding the run method
                        && (startTrEv.sub(1).op() == SpecialCallIds.wildcard  // startTrEv has wildcard ID
                        || runEv.sub(1) == startTrEv.sub(1)) // same callId for runEV and startTrEv
                ) {
                    List<Term> tracePostfixes = new ArrayList<>();
                    tracePostfixes.add(traceManager.getTermFromSubList(1, traceManager.getTracePairs().size()));
                    if (traceManager.getSize() > 2)
                        tracePostfixes.add(traceManager.getTermFromSubList(2, traceManager.getTracePairs().size()));
                    Term updatePostfix = updateManager.getTermFromSubList(1, updateManager.getSize());
                    return getListOfJudgments(updatePostfix, tracePostfixes, services).iterator();
                }
            }
        }
        return Collections.emptyIterator();
    }

    private Iterator<Term> getIteratorFromStrictPrefixes(
            TraceManager traceManager,
            UpdateManager updateManager,
            Pair<UpdateManager,TraceManager> judgment,
            Services services
            ){

        TraceManager tracePrefix = judgment.second;
        UpdateManager updatePrefix = judgment.first;
        Pair<Term,Junctor> lastPrefix = traceManager.getTracePairs().get(tracePrefix.getSize());


        // if isPostfixExtandable then we have the rule applied to
        //      u1 : phi1 ~~ |- u1;u2 : phi1 ~~ ** phi2
        boolean isPostfixExtandable = lastPrefix.second == Junctor.CHOP && lastPrefix.first.op() instanceof SchematicTrace;

        Term updatePostfix = updateManager.getTermFromSubList(updatePrefix.getSize(), updateManager.getSize());
        Term tracePostFix = traceManager.getTermFromSubList(tracePrefix.getSize(), traceManager.getSize());

        if(ContainsObservations.containsObservations(tracePostFix))
            return Collections.emptyIterator();

        List<Term> possibleTraces = new ArrayList<>();
        possibleTraces.add(tracePostFix);
        //If the rule is applied to
        // u1 : phi1 ~~ |- u1;u2 : phi1 ~~ ** phi2
        // there are 2 possible postfixes
        //      -> u2 : phi2
        //      -> u2 : ~~ ** phi2
        if(isPostfixExtandable)
            possibleTraces.add(TraceManager.unchop(lastPrefix.first,tracePostFix,services));
        return getListOfJudgments(updatePostfix, possibleTraces, services).iterator();
    }

    private List<Term> getListOfJudgments(Term update, List<Term> traces, Services services){
        return traces.stream().map(
                trace -> services.getTermFactory().createTerm(UpdateApplication.UPDATE_APPLICATION,
                        update,
                        services.getTermFactory().createTerm(Modality.DIA, trace))).toList();
    }



}
