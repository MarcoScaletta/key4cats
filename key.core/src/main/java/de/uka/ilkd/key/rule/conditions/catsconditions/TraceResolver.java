package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.*;
import de.uka.ilkd.key.logic.op.AbstractTermTransformer;
import de.uka.ilkd.key.logic.op.Observation;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.sort.Sort;
import de.uka.ilkd.key.rule.inst.SVInstantiations;


public abstract class TraceResolver {

    SchemaVariable traceSV;

    private TraceResolver(){
        traceSV = null;
    }

    private TraceResolver(SchemaVariable traceSV){
        this.traceSV = traceSV;
    }

    public Term resolve(SVInstantiations instMap, Services services) {
        final Term seq = (Term) instMap.getInstantiation(traceSV);
        return seq != null ? resolve(seq, services) : null;
    }
    public abstract Term resolve(Term term, Services services);

    public boolean isVarInstantiated(SVInstantiations instMap){
        return instMap.getInstantiation(this.traceSV) != null;
    }

    public static TraceResolver getFirstSeqExtractor(SchemaVariable sv) {
        return new FirstElemResolver(sv);
    }
    public static TraceResolver getLastSeqExtractor(SchemaVariable sv) {
        return new LastElemResolver(sv);
    }

    public static TraceResolver getObservedVarResolver(SchemaVariable sv){
        return new ObservedVarResolver(sv);
    }

    public static TraceResolver getObservingVarResolver(SchemaVariable sv){
        return new ObservingVarResolver(sv);
    }

    public static TraceResolver getIdentity(SchemaVariable s) {
        return new TraceResolver.IdentityResolver(s);
    }

    public static TraceResolver getPostfixTrace(SchemaVariable s) {
        return new PostfixTraceResolver(s);
    }

    public static class ObservedVarResolver extends  TraceResolver{

        private ObservedVarResolver(SchemaVariable traceSV) {
            super(traceSV);
        }

        @Override
        public Term resolve(Term term, Services services) {
            if(term.op() instanceof Observation obs)
                return services.getTermFactory().createTerm(obs.observed());
            return null;
        }
    }

    public static class ObservingVarResolver extends  TraceResolver{

        private ObservingVarResolver(SchemaVariable traceSV) {
            super(traceSV);
        }
        @Override
        public Term resolve(Term term, Services services) {
            return term.op() instanceof Observation ? term.sub(0) : null;
        }
    }

    public static class IdentityResolver extends TraceResolver {


        private IdentityResolver() {
            super();
        }

        private IdentityResolver(SchemaVariable traceSV) {
            super(traceSV);
        }

        @Override
        public Term resolve(Term term, Services services) {
            return term;
        }
    }

    public static class PostfixTraceResolver extends TraceResolver{
        int index;

        private PostfixTraceResolver() {
            super();
        }

        private PostfixTraceResolver(SchemaVariable traceSV) {
            super(traceSV);
        }
        @Override
        public Term resolve(Term seq, Services services) {
            if(seq != null && ((seq.sort() == Sort.FORMULA && TraceManager.isTrace(seq)) || seq.sort() == Sort.UPDATE)){
                return (seq.sort() == Sort.FORMULA ? new TraceManager(seq,services) : new UpdateManager(seq,services)).getPostfixTerm(1);
            }
            return null;
        }

    }

    public static class FirstElemResolver extends TraceResolver {

        private FirstElemResolver(){
            super();
        }

        private FirstElemResolver(SchemaVariable traceSV) {
            super(traceSV);
        }

        @Override
        public Term resolve(Term seq, Services services) {
            if(seq != null && (seq.sort() == Sort.FORMULA || seq.sort() == Sort.UPDATE))
                return (seq.sort() == Sort.FORMULA ? new TraceManager(seq,services) : new UpdateManager(seq,services)).getFirstTerm();
            return null;
        }

    }

    public static class LastElemResolver extends TraceResolver {

        private LastElemResolver(){
            super();
        }

        private LastElemResolver(SchemaVariable traceSV) {
            super(traceSV);
        }
        @Override
        public Term resolve(Term term, Services services) {
            if(term != null && (term.sort() == Sort.FORMULA || term.sort() == Sort.UPDATE))
                return (term.sort() == Sort.FORMULA ? new TraceManager(term,services) : new UpdateManager(term,services)).getLastTerm();
            return null;
        }

    }

    public static final class FirstOfTrace extends AbstractTermTransformer {
        public FirstOfTrace(){
            super(new Name("#firstOf"), 1);
        }
        @Override
        public Term transform(Term term, SVInstantiations svInst, Services services) {
            return new FirstElemResolver().resolve(term.sub(0),services);
        }
    }

    public static final class FirstOfUpdate extends AbstractTermTransformer {
        public FirstOfUpdate(){
            super(new Name("#firstOfUpdate"), 1, Sort.UPDATE);
        }
        @Override
        public Term transform(Term term, SVInstantiations svInst, Services services) {
            return new FirstElemResolver().resolve(term.sub(0),services);
        }
    }

    public static final class LastOfTrace extends AbstractTermTransformer {
        public LastOfTrace(){
            super(new Name("#lastOf"), 1);
        }
        @Override
        public Term transform(Term term, SVInstantiations svInst, Services services) {
            return new LastElemResolver().resolve(term.sub(0),services);
        }
    }

    public static final class TraceIdentity extends AbstractTermTransformer {
        public TraceIdentity(){
            super(new Name("#id"), 1);
        }
        @Override
        public Term transform(Term term, SVInstantiations svInst, Services services) {
            return new IdentityResolver().resolve(term.sub(0),services);
        }
    }

    public static final class PostfixTrace extends AbstractTermTransformer {
        public PostfixTrace(){
            super(new Name("#postfixTrace"), 1);
        }
        @Override
        public Term transform(Term term, SVInstantiations svInst, Services services) {
            return new PostfixTraceResolver().resolve(term.sub(0),services);
        }
    }


    public static final class PostfixUpdate extends AbstractTermTransformer {
        public PostfixUpdate(){
            super(new Name("#postfixUpdate"), 1, Sort.UPDATE);
        }
        @Override
        public Term transform(Term term, SVInstantiations svInst, Services services) {
            return new PostfixTraceResolver().resolve(term.sub(0),services);
        }
    }


}

