package de.uka.ilkd.key.logic;

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


}
