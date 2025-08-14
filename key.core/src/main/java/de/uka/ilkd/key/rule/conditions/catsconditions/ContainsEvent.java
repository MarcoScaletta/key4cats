package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.SchematicTrace;
import de.uka.ilkd.key.logic.op.TraceEvent;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import org.key_project.util.collection.ImmutableArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Marco Scaletta
 */
public class ContainsEvent extends VariableConditionAdapter {


    private final TraceResolver traceResolver;
    private final Term event;
    private final boolean negated;

    public ContainsEvent(TraceResolver traceResolver, Term event, boolean negated) {
        this.traceResolver = traceResolver;
        this.event = event;
        this.negated = negated;
    }

    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations instMap, Services services) {

        if(event == null ||!traceResolver.isVarInstantiated(instMap))
            return true;
        List<Term> args = event.subs().stream().map(arg ->
                ((Term) instMap.lookupValue(arg.op().name())) ).toList();
        if(args.stream().anyMatch(Objects::isNull))
            return true;
        Term formulaTerm = traceResolver.resolve(instMap,services);
        TraceManager tm = new TraceManager(formulaTerm, services);
        List<Term> terms = tm.getListOfConcatenatedAndChoppedTerms();
        Term instantiatedEvent = services.getTermFactory().createTerm(event.op(), args);
        return negated != terms.contains(instantiatedEvent);
    }

}
