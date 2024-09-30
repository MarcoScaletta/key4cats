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
import de.uka.ilkd.key.strategy.feature.MutableState;
import de.uka.ilkd.key.util.Pair;
import de.uka.ilkd.key.util.Triple;

import java.util.*;

public class ChoppingGenerator implements TermGenerator {


    @Override
    public Iterator<Term> generate(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        TacletApp tApp = (TacletApp) app;
        Term fullFormula = (Term) tApp.instantiations().lookupValue(new Name("fullFormula"));
        Services services = goal.proof().getServices();


        Sequent seq = goal.sequent();
        List<TraceManager> traces = seq.antecedent().asList().stream()
                .filter(x -> x.formula().op() instanceof UpdateApplication && x.formula().sub(1).op() instanceof Modality)
                .map(x -> new TraceManager(x.formula().sub(1).sub(0), services)).toList();
        Optional<TraceManager> optionalLongestPreFmlTraceManager = traces.stream().max(Comparator.comparingInt(TraceManager::getSize));
        if(optionalLongestPreFmlTraceManager.isEmpty())
            return Collections.emptyIterator();
        TraceManager longestPreFmlTM = optionalLongestPreFmlTraceManager.get();
        TraceManager fullFormulaTM = new TraceManager(fullFormula,services);

        int lastIndexCommonPrefix = fullFormulaTM.hasCommonPrefixOrIsEquals(longestPreFmlTM);



        if(lastIndexCommonPrefix < (longestPreFmlTM.getSize()-1)) {
            // if true: the longest formula in the antecedent is not prefix of fullformula
            return Collections.emptyIterator();
        }

        if(fullFormulaTM.getTracePairs().get(lastIndexCommonPrefix).second != Junctor.CHOP){
            // if true: prefix in fullFormula is not connected with chop
            // cannot do guided chopping...
            return Collections.emptyIterator();
        }
        Term firstOfInnerTrace = fullFormulaTM.getTracePairs().getFirst().first;
        Term alternativePreFml = null;
        if(firstOfInnerTrace.op() instanceof SchematicTrace){
            alternativePreFml = TraceManager.getTraceFromList(TraceManager.addLast(longestPreFmlTM.getTracePairs(), firstOfInnerTrace, Junctor.CHOP),services);
        }


        // The longest formula in the antecedent is prefix of fullformula
        Term traceTrail =  TraceManager.getTraceFromList(fullFormulaTM.getTracePairs().subList(lastIndexCommonPrefix+1, fullFormulaTM.getSize()),services);

        List<Pair<Term,Term>> choppingTrail = ChopForCall.choppingTrail(traceTrail, fullFormulaTM.getTracePairs().get(lastIndexCommonPrefix).first, services);


        if(choppingTrail == null)
            return Collections.emptyIterator();
        else {
            LinkedHashSet<Triple<Term,Term,Term>> triples = new LinkedHashSet<>();
                for(Pair<Term,Term> choppedTrail : choppingTrail){
                    triples.add(new Triple<>(TraceManager.getTraceFromList(longestPreFmlTM.getTracePairs(), services), choppedTrail.first, choppedTrail.second));
                    if(alternativePreFml != null)
                        triples.add(new Triple<>(alternativePreFml, choppedTrail.first, choppedTrail.second));
                }
            return triples.stream().map(x -> services.getTermBuilder().ife(x.first,x.second,x.third)).toList().iterator();
        }
    }



}
