package de.uka.ilkd.key.logic.op;

import de.uka.ilkd.key.logic.Name;
import de.uka.ilkd.key.logic.sort.Sort;

public class SpecialCallIds {

    public final static WildCard wildcard = new WildCard();
    public final static LogicVariable callId = new LogicVariable(new Name("\\callId"), Sort.ANY);

}

class WildCard extends AbstractSortedOperator {
    WildCard() {
        super(new Name("$?"), Sort.ANY, true);
    }
}
