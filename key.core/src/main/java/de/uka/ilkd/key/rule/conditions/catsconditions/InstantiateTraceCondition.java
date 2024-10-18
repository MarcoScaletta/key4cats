//package de.uka.ilkd.key.rule.conditions.catsconditions;
//
//import de.uka.ilkd.key.java.Services;
//import de.uka.ilkd.key.logic.Term;
//import de.uka.ilkd.key.logic.TraceManager;
//import de.uka.ilkd.key.logic.op.SVSubstitute;
//import de.uka.ilkd.key.logic.op.SchemaVariable;
//import de.uka.ilkd.key.rule.VariableConditionAdapter;
//import de.uka.ilkd.key.rule.inst.SVInstantiations;
//
///**
// * @author Marco Scaletta
// */
//public class InstantiateTraceCondition extends VariableConditionAdapter {
//
//
//    private final SchemaVariable trace1;
//    private final Term trace2;
//
//
//    public InstantiateTraceCondition(SchemaVariable trace1, Term trace2) {
//        this.trace1 = trace1;
//        this.trace2 = trace2;
//    }
//
//    @Override
//    public boolean check(SchemaVariable var, SVSubstitute instCandidate, SVInstantiations instMap, Services services) {
//        Term trace1Term = trace1.op() instanceof SchemaVariable ? (Term) instMap.getInstantiation((SchemaVariable) trace1.op()) : trace1;
//        Term trace2Term = trace2.op() instanceof SchemaVariable ? (Term) instMap.getInstantiation((SchemaVariable) trace2.op()) : trace2;
//
//        if(TraceManager.isTrace(trace1Term) && TraceManager.isTrace(trace2Term)){
//            TraceManager traceManager1 = new TraceManager(trace1Term,instMap, services);
//            TraceManager traceManager2 = new TraceManager(trace2Term,instMap, services);
//            if(!traceManager1.isFullyInstantiated() || !traceManager2.isFullyInstantiated())
//                return true;
//            return traceManager1.equals(traceManager2);
//        }
//        return false;
//
//    }
//}
