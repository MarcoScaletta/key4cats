package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.inst.SVInstantiations;


public abstract class MethodNameResolver {

    TraceResolver traceResolver;

    private MethodNameResolver(TraceResolver traceSV){
        this.traceResolver = traceSV;
    }

    public static MethodNameResolver getMethodNameOf(TraceResolver eventSV){
        return new MethodNameFromEventResolver(eventSV);
    }


    public boolean isVarInstantiated(SVInstantiations instMap, Services services){
        return this.traceResolver.resolve(instMap,services) != null;
    }

    public static Object getIdentity(TraceResolver op) {
        return new MethodNameResolver.IdentityMethodNameResolver(op);
    }

    public Term resolve(SVInstantiations instMap, Services services) {
        final Term event = traceResolver.resolve(instMap,services);
        return event != null ? resolve(event, services) : null;
    }

    public abstract Term resolve(Term term, Services services);

    public static class MethodNameFromEventResolver extends MethodNameResolver {

        private MethodNameFromEventResolver(TraceResolver eventSV) {
            super(eventSV);
        }

        @Override
        public Term resolve(Term term, Services services) {
            if(term!=null&& (term.sort() == Sort.FORMULA || term.sort() == Sort.UPDATE))
                return term.sub(0).sort().name().equals(new Name("MethodName")) ? term.sub(0) : null;
            return null;
        }
    }


    public static class IdentityMethodNameResolver extends MethodNameResolver {


        private IdentityMethodNameResolver(TraceResolver traceSV) {
            super(traceSV);
        }

        @Override
        public Term resolve(Term term, Services services) {
            return term;
        }
    }
}