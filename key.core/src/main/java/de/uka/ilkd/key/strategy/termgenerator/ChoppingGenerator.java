package de.uka.ilkd.key.strategy.termgenerator;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.Modality;
import de.uka.ilkd.key.logic.op.UpdateApplication;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.rule.conditions.catsconditions.ChopForCall;
import de.uka.ilkd.key.strategy.feature.MutableState;
import de.uka.ilkd.key.util.Triple;

import java.util.*;

public class ChoppingGenerator implements TermGenerator {




    @Override
    public Iterator<Term> generate(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        TacletApp tApp = (TacletApp) app;
        Term fullFormula = (Term) tApp.instantiations().lookupValue(new Name("fullFormula"));
        Services services = goal.proof().getServices();
        List<Triple<Term,Term,Term>> choppings = ChopForCall.getChoppings(fullFormula, services);

        if(choppings == null)
            return Collections.emptyIterator();
        Sequent seq = goal.sequent();
        List<TraceManager> traces = seq.antecedent().asList().stream()
                .filter(x -> x.formula().op() instanceof UpdateApplication && x.formula().sub(1).op() instanceof Modality)
                .map(x -> new TraceManager(x.formula().sub(1).sub(0), services)).toList();
        Optional<TraceManager> optionalMax = traces.stream().max(Comparator.comparingInt(TraceManager::getSize));
        if(optionalMax.isEmpty())
            return Collections.emptyIterator();
        List<Triple<Term,Term,Term>> filteredChoppings = choppings.stream().filter(
                chopping -> (new TraceManager(chopping.first, services).hasPrefixOrIsEquals(optionalMax.get()) > -1)).toList();

        return filteredChoppings.stream().map(triple ->
            services.getTermBuilder().ife(triple.first, triple.second, triple.third)).toList().iterator();

    }



}
