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
import org.jspecify.annotations.NonNull;

import java.util.*;

public class PostFixGenerator implements TermGenerator {

    class PostFixGenIterator implements Iterator<Term> {
        private final Services services;
        private final Sequent sequent;
        private Iterator<Term> backingSetIt;
        final TacletApp tApp;
        final Term updateTerm;
        final Term traceTerm;
        final Term updatePrefixTerm;
        final Term tracePrefixTerm;

        public PostFixGenIterator(Sequent sequent, Services services, TacletApp tApp, Term updateTerm, Term traceTerm, Term updatePrefixTerm, Term tracePrefixTerm) {
            this.sequent = sequent;
            this.services = services;
            this.tApp = tApp;
            this.updateTerm = updateTerm;
            this.traceTerm = traceTerm;
            this.updatePrefixTerm = updatePrefixTerm;
            this.tracePrefixTerm = tracePrefixTerm;
        }

        @Override
        public boolean hasNext() {
            if (backingSetIt == null) {
                backingSetIt = lazySetGen(sequent, services, tracePrefixTerm, updatePrefixTerm, updateTerm, traceTerm);
            }
            return backingSetIt.hasNext();
        }

        @Override
        public Term next() {
            if (backingSetIt == null) {
                backingSetIt = lazySetGen(sequent, services, tracePrefixTerm, updatePrefixTerm, updateTerm, traceTerm);
            }
            return backingSetIt.next();
        }
    }

    @Override
    public Iterator<Term> generate(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        final TacletApp tApp = (TacletApp) app;
        final Term updateTerm = (Term) tApp.instantiations().lookupValue(new Name("update"));
        final Term traceTerm = (Term) tApp.instantiations().lookupValue(new Name("trace"));
        final Term updatePrefixTerm = (Term) tApp.instantiations().lookupValue(new Name("updatePrefix"));
        final Term tracePrefixTerm = (Term) tApp.instantiations().lookupValue(new Name("tracePrefix"));

        return new PostFixGenIterator(goal.sequent(), goal.proof().getServices(), tApp, updateTerm, traceTerm, updatePrefixTerm, tracePrefixTerm);
    }

    private @NonNull Iterator<Term> lazySetGen(Sequent seq,  Services services, Term tracePrefixTerm, Term updatePrefixTerm, Term updateTerm, Term traceTerm) {
        if(tracePrefixTerm == null || updatePrefixTerm == null)
            return Collections.emptyIterator();

        List<Pair<UpdateManager,TraceManager>> judgmentsAnte = getMatchingJudgmentsFromAnte(seq, updateTerm, traceTerm, services);

        Optional<Pair<UpdateManager,TraceManager>> optionalMax = judgmentsAnte.stream().max(
                Comparator.comparingInt(x -> ((Pair<UpdateManager,TraceManager>) x).first.getSize())
                        .thenComparingInt(x-> ((Pair<UpdateManager,TraceManager>) x).second.getSize())
        );

        if(optionalMax.isEmpty())
            return Collections.emptyIterator();
        UpdateManager updateManager =  optionalMax.get().first;
        TraceManager traceManager =  optionalMax.get().second;

        if(!updatePrefixTerm.equals(updateManager.getTermFromList()) ||
                !tracePrefixTerm.equals(traceManager.getTermFromList()))
            return Collections.emptyIterator();

        return List.of(createJudgment(
                new UpdateManager(updateTerm, services).getPostfixTerm(updateManager.getSize()),
                new TraceManager(traceTerm, services).getPostfixTerm(traceManager.getSize()),
                services)).iterator();

//
//        UpdateManager updateManager = new UpdateManager(updateTerm, services);
//        TraceManager traceManager = new TraceManager(traceTerm, services);
//
//        Sequent seq = goal.sequent();
//        List<Pair<UpdateManager,TraceManager>> judgmentsAnte = getMatchingJudgmentsFromAnte(seq,updateManager,traceManager,services);
//        Optional<Pair<UpdateManager,TraceManager>> optionalJugdmentWithLongestUpdate =
//                judgmentsAnte.stream().max(Comparator.comparingInt(o -> o.first.getSize()));
//        if(optionalJugdmentWithLongestUpdate.isEmpty()) {
//            //there is no matching prefix in the antencedent
//            //we can check here if: {runEv(m,i);U} : startEv(m,_) ** phi
//            return postFixLocally(traceManager,updateManager,services);
//        }
//
//
//
//        return getIteratorFromStrictPrefixes(
//                traceManager,
//                updateManager,
//                optionalJugdmentWithLongestUpdate.get(),
//                services);
    }

    private static Term getUpdate(SequentFormula sequentFormula){
        return sequentFormula.formula().sub(0);
    }

    private static Term getTrace(SequentFormula sequentFormula){
        return sequentFormula.formula().sub(1).sub(0);
    }

    private List<Pair<UpdateManager,TraceManager>> getMatchingJudgmentsFromAnte(
            Sequent seq,
            Term update,
            Term trace,
            Services services
    ){
        UpdateManager updateManager = new UpdateManager(update,services);
        TraceManager traceManager = new TraceManager(trace,services);
        List<Pair<UpdateManager,TraceManager>> res = new LinkedList<>();
        for (SequentFormula sequentFormula : seq.antecedent()) {
            if (PostFixGenerator.isJudgment(sequentFormula)) {
                Pair<UpdateManager, TraceManager> judgment =
                        new Pair<>(new UpdateManager(sequentFormula.formula().sub(0), services),
                                new TraceManager(sequentFormula.formula().sub(1).sub(0), services));
                if (updateManager.hasStrictPrefix(judgment.first) && traceManager.hasStrictPrefix(judgment.second))
                    res.add(judgment);
            }
        }
        return res;
    }

    private static boolean isJudgment(SequentFormula sequentFormula){
        return sequentFormula.formula().op() instanceof UpdateApplication && sequentFormula.formula().sub(1).op() instanceof Modality;
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

    private Term createJudgment(Term update, Term trace, Services services){
        return  services.getTermFactory().createTerm(UpdateApplication.UPDATE_APPLICATION,
                update,
                services.getTermFactory().createTerm(Modality.DIA, trace));
    }

    private List<Term> getListOfJudgments(Term update, List<Term> traces, Services services){
        return traces.stream().map(
                trace -> createJudgment(update, trace, services)).toList();
    }



}
