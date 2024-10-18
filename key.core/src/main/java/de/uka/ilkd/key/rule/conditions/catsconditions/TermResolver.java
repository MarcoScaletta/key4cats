package de.uka.ilkd.key.rule.conditions.catsconditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.rule.inst.SVInstantiations;


public abstract class TermResolver {

    Term term;

    public TermResolver(){
        term = null;
    }

    public TermResolver(Term traceSV){
        this.term = traceSV;
    }

    public Term resolve(SVInstantiations instMap, Services services) {
        final Term seq = term.op() instanceof SchemaVariable ? (Term) instMap.getInstantiation((SchemaVariable) term) : term;
        return seq != null ? resolve(seq, services) : null;
    }
    public abstract Term resolve(Term term, Services services);

    public boolean isVarInstantiated(SVInstantiations instMap){
        final Term seq = term.op() instanceof SchemaVariable ? (Term) instMap.getInstantiation((SchemaVariable) term) : term;
        return seq != null;
    }

    public static TermResolver getIdentity(Term s) {
        return new TermResolver.IdentityResolver(s);
    }

    public static class IdentityResolver extends TermResolver {

        private IdentityResolver(Term term) {
            super(term);
        }

        @Override
        public Term resolve(Term term, Services services) {
            return term;
        }
    }
}

