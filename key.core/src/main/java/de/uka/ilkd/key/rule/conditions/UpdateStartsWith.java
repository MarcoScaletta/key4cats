package de.uka.ilkd.key.rule.conditions;

import de.uka.ilkd.key.java.Services;
import de.uka.ilkd.key.logic.Term;
import de.uka.ilkd.key.logic.TermFactory;
import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.logic.op.SVSubstitute;
import de.uka.ilkd.key.logic.op.SchemaVariable;
import de.uka.ilkd.key.logic.op.UpdateJunctor;
import de.uka.ilkd.key.rule.MatchConditions;
import de.uka.ilkd.key.rule.VariableCondition;
import de.uka.ilkd.key.rule.inst.SVInstantiations;
import org.key_project.util.Streams;
import org.key_project.util.collection.ImmutableArray;

import java.util.*;
import java.util.stream.IntStream;

public class UpdateStartsWith implements VariableCondition {


    private final SchemaVariable updateSV;
    private final SchemaVariable prefixSV;
    private final SchemaVariable postfixSV;

    public UpdateStartsWith(SchemaVariable update, SchemaVariable prefix,SchemaVariable postfix) {
        this.updateSV = update;
        this.prefixSV = prefix;
        this.postfixSV = postfix;
    }

    private List<Term> updateToList(Term update){
        List<Term> updateList = new ArrayList<>();
        if(update.op() instanceof UpdateJunctor op){
            if(op != UpdateJunctor.SEQUENTIAL_UPDATE)
                return null;
            updateList.addAll(updateToList(update.sub(0)));
            updateList.add(update.sub(1));
        }else {
            updateList.add(update);
        }
        return updateList;
    }


    private Term updateStartsWith(Term update, Term prefix, Services services){
        List<Term> updateList = updateToList(update);
        List<Term> prefixList =  updateToList(prefix);
        if(updateList.size() < prefixList.size())
            return null;
        int index;
        for(index=0;index<prefixList.size();index++){
            if(prefixList.get(index) != updateList.get(index))
                return null;
        }

        Term updateJunctorTerm = updateList.get(index++);
        for (int i = index; i < updateList.size(); i++) {
            updateJunctorTerm = services.getTermFactory().createTerm(UpdateJunctor.SEQUENTIAL_UPDATE, updateJunctorTerm, updateList.get(i));
        }
        return updateJunctorTerm;
    }

    @Override
    public MatchConditions check(SchemaVariable var, SVSubstitute instCandidate, MatchConditions matchCond, Services services) {
        SVInstantiations svInst = matchCond.getInstantiations();
        Term updateTerm = (Term) svInst.getInstantiation(updateSV);
        Term prefixTerm = (Term) svInst.getInstantiation(prefixSV);
        if(updateTerm == null || prefixTerm == null)
            return matchCond;
        Term postfixTerm = updateStartsWith(updateTerm,prefixTerm, services);
        if(postfixTerm!=null)
            return matchCond.setInstantiations(svInst.add(postfixSV, postfixTerm, services));
        return null;
    }
}
