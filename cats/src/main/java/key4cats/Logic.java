package key4cats;

import java.util.*;
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

record Obs(Identifier observed, Identifier observing) implements KeYGen{
    @Override
    public String toKeY() {
        return String.format("\\obs(%s,%s)",observed.toKeY(),observing.toKeY());
    }
}

class ObsTr extends Operator implements Trace {
    public ObsTr(Obs elem1, Trace elem2) {
        super(elem1, elem2, "$.");
    }
}

class Event implements Trace{
    String eventName;
    List<KeYGen> subs = new ArrayList<>();

    public Event(String eventName, KeYGen... subs){
        this.eventName = eventName;
        if(subs != null && subs.length > 0)
            this.subs = List.of(subs);
    }

    @Override
    public String toKeY() {
        String subsString = (!subs.isEmpty() ? String.format("(%s)", String.join(", ",
                subs.stream().map(KeYGen::toKeY).toList())) : "");
        return String.format("\\%sTrEv%s", eventName, subsString);
    }
}

class TraceOp extends Operator implements Trace{
    static final Map<String, String> m = new HashMap<>() {{
        put(";", "$.");
        put(".", "$.");
        put("&", "&");
        put("|", "|");
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

record Wildcard() implements KeYGen{
    @Override
    public String toKeY() {
        return "$?";
    }
}

record CallId() implements KeYGen{
    @Override
    public String toKeY() {
        return "thisCallId";
    }
}

class CAT implements KeYGen{

    Trace preTr;Trace inTr;Trace postTr;
    final List<Identifier> observingVars;

    public CAT(Trace preTr, Trace inTr, Trace postTr){

        this.observingVars = new ArrayList<>();
        this.preTr = preTr;
        this.inTr = inTr;
        this.postTr = postTr;
        getObsVars(preTr);
        getObsVars(inTr);
        getObsVars(postTr);
    }

    private void getObsVars(Trace trace){
        switch (trace) {
            case TraceOp op:
                getObsVars((Trace) op.elem1);
                getObsVars((Trace) op.elem2);
                break;
            case ObsTr obsTr:
                Identifier observingVar = ((Obs) obsTr.elem1).observing();
                if(observingVars.contains(observingVar))
                    throw new RuntimeException(String.format("Multiple declaration for observing variable %s in %s  ", observingVar.toKeY(), this.toKeY()));
                observingVars.addLast(observingVar);
                getObsVars((Trace) obsTr.elem2);
                break;
            default: break;
        }
    }

    @Override
    public String toKeY() {
        String CATtoKeY = String.format("CAT(%s)", Utils.listToKeY(List.of(preTr,inTr,postTr)));
        for(Identifier var : observingVars.reversed()){
            CATtoKeY = String.format("bind{ int %s;}(%s)", var.toKeY(),CATtoKeY);
        }
        return String.format("callId{int thisCallId;}(%s)", CATtoKeY);
    }
}