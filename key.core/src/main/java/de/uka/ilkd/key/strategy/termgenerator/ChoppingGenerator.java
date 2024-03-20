package de.uka.ilkd.key.strategy.termgenerator;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.PosInOccurrence;
import de.uka.ilkd.key.logic.Sequent;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.rule.RuleApp;
import de.uka.ilkd.key.rule.Taclet;
import de.uka.ilkd.key.rule.TacletApp;
import de.uka.ilkd.key.rule.conditions.ChopForCall;
import de.uka.ilkd.key.strategy.feature.MutableState;
import de.uka.ilkd.key.util.Triple;

import java.util.Iterator;
import java.util.List;

public class ChoppingGenerator implements TermGenerator {
    @Override
    public Iterator<Term> generate(RuleApp app, PosInOccurrence pos, Goal goal, MutableState mState) {
        TacletApp tApp = (TacletApp) app;
        Term fullFormula = (Term) tApp.instantiations().lookupValue(new Name("fullFormula"));
        Services services = goal.proof().getServices();
        List<Triple<Term,Term,Term>> choppings = ChopForCall.getChoppings(fullFormula, services);

//        Sequent seq = goal.sequent();
//        for( var sf : seq.antecedent() ){
//            Term t = sf.formula();
//        }
        // get longer judgment in assumptions and match against preFormula

        return choppings.stream().map(triple -> services.getTermBuilder().ife(triple.first,triple.second,triple.third)).toList().iterator();
    }
}
