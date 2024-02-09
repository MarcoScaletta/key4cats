package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

import java.util.HashSet;
import java.util.Set;

public class PostStateCondition implements VariableCondition {

    private final SchemaVariable trace;
    private final SchemaVariable stateFml;

    public PostStateCondition(SchemaVariable trace, SchemaVariable stateFml) {
        this.trace = trace;
        this.stateFml = stateFml;
    }

    public Set<Name> getObservingVariable(Term trace){
        Set<Name> observingVariables = new HashSet<>();
        if(trace.op() == Junctor.CONC || trace.op() == Junctor.CHOP) {
            observingVariables.addAll(getObservingVariable(trace.sub(0)));
            observingVariables.addAll(getObservingVariable(trace.sub(1)));
        }else if(trace.op() instanceof Observation) {
            observingVariables.add(trace.sub(0).op().name());
        }
        return observingVariables;
    }

    public Set<Name> getStateFmlVariable(Term stateFml){
        return  new HashSet<>(getAllSubs(stateFml).stream().map( x-> x.op().name()).toList());
    }

    public Set<Term> getAllSubs(Term term){
        Set<Term> subTerms = new HashSet<>();
        if(term.subs().isEmpty()){
            subTerms.add(term);
        }
        else {
            for (Term t : term.subs()) {
                subTerms.addAll(getAllSubs(t));
            }
        }

        return subTerms;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        Term traceTerm = (Term) svInst.getInstantiation(trace);
        Term stateFmlTerm = (Term) svInst.getInstantiation(stateFml);
        MatchConditions result = null;
        if(stateFmlTerm.op() == Junctor.STATEFML) {
            Set<Name> observingVariables = getObservingVariable(traceTerm);
            if(observingVariables.isEmpty())
                result = matchCond;
            else {
                Set<Name> stateFmlVariables = getStateFmlVariable(stateFmlTerm);
                stateFmlVariables.retainAll(observingVariables);
                if(stateFmlVariables.isEmpty())
                    result = matchCond;

            }
        }
        return result;
    }

}
