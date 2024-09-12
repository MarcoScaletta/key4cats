package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TraceManager;
import de.uka.ilkd.key.logic.op.*;
import de.uka.ilkd.key.rule.VariableConditionAdapter;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

import java.util.Set;

/**
 * @author Marco Scaletta
 */
public class SchematicTraceInclusion extends VariableConditionAdapter {

    enum Bool {TRUE, FALSE, UNKNOWN};

    private final SchemaVariable anteTrSV;
    private final SchemaVariable succTr2SV;

    private final boolean negated;


    public SchematicTraceInclusion(SchemaVariable succTr, SchemaVariable anteTr, boolean negated) {
        this.succTr2SV = succTr;
        this.anteTrSV = anteTr;
        this.negated = negated;
    }


    @Override
    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations svInst, Services services) {
        Term anteTerm = (Term) svInst.getInstantiation(anteTrSV);
        Term succTerm = (Term) svInst.getInstantiation(succTr2SV);

        if(anteTerm == null || succTerm == null)
            return true;
        if(succTerm.op() == SchematicTrace.SCHEM_TRACE) {
            Set<Term> forbProcsSucc = IsSchematicTraceOverM.getForbiddenProcsRec(succTerm.sub(0));
            if (anteTerm.op() == SchematicTrace.SCHEM_TRACE) {
                Set<Term> forbProcsAnte = IsSchematicTraceOverM.getForbiddenProcsRec(anteTerm.sub(0));
                return negated != forbProcsAnte.containsAll(forbProcsSucc);
            }else{
                TraceManager traceManagerAnte = new TraceManager(anteTerm, services);
                return negated != (containEventsOnProcs(traceManagerAnte,forbProcsSucc)== Bool.FALSE);
            }
        } else
            return negated;

    }

    private boolean containsOnlyChopConcatJunctors(TraceManager tm){
        return tm.getTracePairs().stream().noneMatch(pair -> pair.first.op() == Junctor.AND || pair.first.op() == Junctor.OR);
    }

    private Bool containEventsOnProcs(TraceManager traceManager, Set<Term> forbiddenProc){
        if(!containsOnlyChopConcatJunctors(traceManager))
            return Bool.UNKNOWN;
        if(traceManager.getTracePairs().stream().anyMatch(pair -> termAllowsForbiddenProcs(pair.first,forbiddenProc)))
            return Bool.TRUE;
        return Bool.FALSE;
    }

    private boolean termAllowsForbiddenProcs(Term term, Set<Term> forbiddenProc){
        if(term.op() == SchematicTrace.SCHEM_TRACE_TRIV)
            return true;
        if(term.op() == SchematicTrace.SCHEM_TRACE) {
            return !IsSchematicTraceOverM.getForbiddenProcsRec(term.sub(0)).containsAll(forbiddenProc);
        }
        if(term instanceof TraceEvent){
            if(term.op() == TraceEvent.START_TR_EV || term.op() == TraceEvent.START_TR_EV){
                return forbiddenProc.contains(term.sub(0));
            }
        }
        return false;
    }


}
