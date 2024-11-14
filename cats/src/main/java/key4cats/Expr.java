package key4cats;

import java.util.List;
import java.util.Objects;


class Utils{

    public static String listToKeY(List<? extends KeYGen> elems){
        return listToKeY(elems, ", ");
    }

    public static String listToKeY(List<? extends KeYGen> elems, String delim){
        return String.join(delim, elems.stream().map(KeYGen::toKeY).toList());
    }

}


class Operator implements KeYGen {
    KeYGen elem1;
    KeYGen elem2;
    String op;
    public Operator(KeYGen elem1, KeYGen elem2, String op){
        this.elem1 = elem1;
        this.elem2 = elem2;
        this.op = op;
    }

    @Override
    public String toKeY() {return String.format("(%s %s %s)", elem1.toKeY(), op, elem2.toKeY());}
}

interface Expr extends KeYGen {}

interface ExprElem extends Expr{}

record NumExprElem(int val) implements ExprElem{
    @Override
    public String toKeY() {return Integer.toString(this.val);}
}

record Identifier(String id) implements ExprElem{

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Identifier) && Objects.equals(((Identifier) obj).id, this.id);
    }

    @Override
    public String toKeY() {return this.id;}
}

class ExprOp extends Operator implements Expr{
    public ExprOp(Expr elem1, Expr elem2, String op) {
        super(elem1, elem2, op);
    }
}