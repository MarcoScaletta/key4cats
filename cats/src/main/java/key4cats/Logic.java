package key4cats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
;
interface Predicate extends KeYGen{}

record TruePred() implements Predicate{
    @Override
    public String toKeY() {
        return "true";
    }
}

record FalsePred() implements Predicate{
    @Override
    public String toKeY() {
        return "false";
    }
}

record Not(Predicate subPred) implements Predicate{
    @Override
    public String toKeY() {
        return String.format("not(%s)", this.subPred.toKeY());
    }
}

class PredOp extends Operator implements Predicate{
    public PredOp(Predicate elem1, Predicate elem2, String op) {
        super(elem1, elem2, op);
    }
}

class BoolOpExpr extends Operator implements Predicate{
    public BoolOpExpr(Expr elem1, Expr elem2, String op) {
        super(elem1, elem2, op);
    }
}

interface Trace extends KeYGen{}

record StateFml(Predicate pred) implements Trace{
    @Override
    public String toKeY() {
        return String.format("`%s`", pred.toKeY());
    }
}

record Obs(Identifier observing, Identifier observed) implements KeYGen{
    @Override
    public String toKeY() {
        return String.format("\\obs(Traces.%s:-:%s)",observing.toKeY(),observed.toKeY());
    }
}

class ObsTr extends Operator implements Trace {
    public ObsTr(Obs elem1, Trace elem2) {
        super(elem1, elem2, ".");
    }
}

class TraceOp extends Operator implements Trace{
    static final Map<String, String> m = new HashMap<>() {{
        put(";", "$.");
        put(".", "$.");
        put("**", "**");
    }};
    public TraceOp(Trace elem1, Trace elem2, String op) {
        super(elem1, elem2, m.get(op));

    }
}

record AbsTr(List<Identifier> methods) implements Trace{
    @Override
    public String toKeY() {
        if(methods.isEmpty())
            return "~~";
        else
            return String.format("~(%s)~", String.join(", ", Utils.listToKeY(methods)));
    }
}

record CAT(Trace preTr, Trace inTr, Trace postTr) implements KeYGen{

    @Override
    public String toKeY() {

        return String.format("CAT(%s)", Utils.listToKeY(List.of(preTr,inTr,postTr)));
    }
}