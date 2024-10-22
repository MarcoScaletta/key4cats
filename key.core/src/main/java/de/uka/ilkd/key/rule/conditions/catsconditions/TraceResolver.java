package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.inst.SVInstantiations;

import javax.xml.validation.Schema;

public abstract class TraceResolver {

    SchemaVariable traceSV;

    private TraceResolver(SchemaVariable traceSV){
        this.traceSV = traceSV;
    }

    public abstract Term resolve(SVInstantiations instMap, Services services);

    public boolean isVarInstantiated(SVInstantiations instMap, Services services){
        return instMap.getInstantiation(this.traceSV) != null;
    }

    public static TraceResolver getFirstSeqExtractor(SchemaVariable sv) {
        return new FirstElemResolver(sv);
    }
    public static TraceResolver getLastSeqExtractor(SchemaVariable sv) {
        return new LastElemResolver(sv);
    }


    public static TraceResolver getIdentity(SchemaVariable s) {
        return new TraceResolver.IdentityResolver(s);
    }

    public static class IdentityResolver extends TraceResolver {

        private IdentityResolver(SchemaVariable traceSV) {
            super(traceSV);
        }

        @Override
        public Term resolve(SVInstantiations instMap, Services services) {
            return (Term) instMap.getInstantiation(traceSV);
        }
    }

    public static class FirstElemResolver extends TraceResolver {

        private FirstElemResolver(SchemaVariable traceSV) {
            super(traceSV);
        }

        @Override
        public Term resolve(SVInstantiations instMap, Services services) {

            final Term seq = (Term) instMap.getInstantiation(traceSV);
            if(seq != null && (seq.sort() == Sort.FORMULA || seq.sort() == Sort.UPDATE))
                return (seq.sort() == Sort.FORMULA ? new TraceManager(seq,services) : new UpdateManager(seq,services)).getFirstTerm();
            return null;
        }

    }

    public static class LastElemResolver extends TraceResolver {


             private LastElemResolver(SchemaVariable traceSV) {
            super(traceSV);
        }

        @Override
        public Term resolve(SVInstantiations instMap, Services services) {
            final Object a = instMap.getInstantiation(traceSV);
            final Term seq = (Term) a;
            if(seq != null && (seq.sort() == Sort.FORMULA || seq.sort() == Sort.UPDATE))
                return (seq.sort() == Sort.FORMULA ? new TraceManager(seq,services) : new UpdateManager(seq,services)).getLastTerm();
            return null;
        }

    }
}