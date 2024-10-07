package de.uka.ilkd.key.logic;

import java.util.List;

public interface SeqManager<T> {

    T getFirst();
    T getLast();
    T get(int i);
    int getSize();
    List<T> getList();
    Term getTermFromSubList(List<T> list);
    Term getTermFromSubList();
    // begin: inclusive, end: exclusive
    Term getTermFromSubList(int begin, int end);


}
