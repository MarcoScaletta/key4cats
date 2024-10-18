package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.logic.op.Junctor;
import de.uka.ilkd.key.util.Pair;

import java.util.List;

public abstract class SeqManager<T> {


    public abstract T getFirst();
    public abstract T getLast();
    public abstract T get(int i);
    public abstract int getSize();
    public abstract List<T> getList();
    public abstract Term getTermFromList();
    // begin: inclusive, end: exclusive
    public abstract Term getTermFromSubList(int begin, int end);

    public Term getPostfixTerm(int startIndex){
        return getTermFromSubList(startIndex, this.getSize());
    }

    public Term getPrefixTerm(int lastIndex){
        return getTermFromSubList(0, lastIndex);
    }
    public Term getPrefixButLast(){
        return getPrefixTerm(getSize()-1);
    }

    public boolean hasStrictPrefix(SeqManager<T> possiblePrefix){
        int prefixSize = possiblePrefix.getSize();
        int traceSize  = this.getSize();

        if(prefixSize >= traceSize)
            return false;
        return this.getList().subList(0,prefixSize).equals(possiblePrefix.getList());
    }

    public boolean hasStrictPostfix(SeqManager<T> possiblePrefix){
        int prefixSize = possiblePrefix.getSize();
        int traceSize  = this.getSize();

        if(prefixSize >= traceSize)
            return false;
        return this.getList().subList(traceSize-prefixSize,traceSize).equals(possiblePrefix.getList());
    }


    public String toString(){
        return this.getList().toString();
    }

//
//    void addLast(T elem);

}
