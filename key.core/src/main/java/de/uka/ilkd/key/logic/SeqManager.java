package de.uka.ilkd.key.logic;

import de.uka.ilkd.key.logic.op.SchemaVariable;

import java.util.List;

public abstract class SeqManager<T> {


    public abstract Term toTerm(T el);
    public abstract T getFirst();
    public abstract T getLast();
    public Term getFirstTerm(){
        return toTerm(getFirst());
    }
    public Term getLastTerm(){
        return toTerm(getLast());
    }
    public T get(int i){
        return getList().get(i);
    }

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
        return possiblePrefix.getSize() < this.getSize() && this.hasPrefix(possiblePrefix);
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

    public boolean hasPrefix(SeqManager<T> possiblePrefix){
        if(possiblePrefix.getSize() > this.getSize())
            return false;
        return this.getList().subList(0,possiblePrefix.getSize()).equals(possiblePrefix.getList());
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof SeqManager<?> && ((SeqManager<?>) obj).getList().equals(this.getList());
    }

    public boolean isFullyInstantiated(){
        return this.getList().stream().noneMatch(x -> toTerm(x).op() instanceof SchemaVariable);
    }
    //
//    void addLast(T elem);

}
