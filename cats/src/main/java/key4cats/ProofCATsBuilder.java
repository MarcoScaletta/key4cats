package key4cats;

import key4cats.parsers.CATs.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProofCATsBuilder extends CATsBaseVisitor<KeYGen>{


    private final String include = "traceRules.key";
    private final String className = "Traces";
    private final String javaSource = ".";
    private final Proof proof;
    private final String pathProblem;

    public ProofCATsBuilder(String catsProblem) {
        CATsLexer java8Lexer = new CATsLexer(CharStreams.fromString(catsProblem));
        CATsParser parser = new CATsParser(new CommonTokenStream(java8Lexer));
        CATsParser.ProblemIdContext problemIdCtx =  parser.problemId();
        String problemName =  problemIdCtx.id().getText();
        pathProblem = problemName;
        CATsParser.ProblemContext probCtx = problemIdCtx.problem();
        Problem problem = (Problem) probCtx.accept(this);
        this.proof = new Proof(String.format("\"%s\"",include), javaSource,problem);
    }

    public String getKeYProof() {
        return proof.toKeY();
    }

    public String getPathProblem(){
        return pathProblem;
    }
    public String getKeYProblemFile() {
        return proof.toKeY();
    }

    @Override
    public KeYGen visitProblem(CATsParser.ProblemContext ctx) {
        List<CATof> assumeCats = ctx.assumeCats().catOf().stream().map(
                x -> (CATof) x.accept(this)).toList();
        CATof target = (CATof) ctx.target.accept(this);
        return new Problem(assumeCats.stream().map(AssumeCAT::new).toList(), target);

    }


    @Override
    public KeYGen visitCatOf(CATsParser.CatOfContext ctx) {
        return new CATof(
                new Identifier(className +"::"+ ctx.id().getText()),
                (CAT) ctx.cat().accept(this));
    }

    @Override
    public KeYGen visitCat(CATsParser.CatContext ctx) {

        Trace preTr = (Trace)ctx.preTr.accept(this);
        Trace inTr = (Trace)ctx.innerTr.accept(this);
        Trace postTr = (Trace)ctx.postTr.accept(this);

        return new CAT(preTr, inTr, postTr);
    }

    @Override
    public KeYGen visitTrace(CATsParser.TraceContext ctx) {

        if(ctx.obs() != null)
            return new ObsTr(
                    (Obs) ctx.obs().accept(this),
                    (Trace) ctx.tr.accept(this));
        if(ctx.absTr() != null)
            return ctx.absTr().accept(this);
        if(ctx.traceOp() != null)
                return new TraceOp(
                        (Trace) ctx.tr1.accept(this),
                        (Trace) ctx.tr2.accept(this),
                        ctx.op.getText());

        if(ctx.stateFml() != null)
            return (Trace) ctx.stateFml().accept(this);

        System.err.print("returning null trace " + ctx.getText());
        return null;
    }

    @Override
    public KeYGen visitAbsTr(CATsParser.AbsTrContext ctx) {

        return new AbsTr(ctx.id()!=null?
                ctx.id().stream().map(x -> new Identifier(className + "::" + x.getText())).toList()
    : List.of());
    }

    @Override
    public KeYGen visitObs(CATsParser.ObsContext ctx) {
        Identifier observing = (Identifier) ctx.observing.accept(this);
        return new Obs(new Identifier (className + "." + ctx.observed.getText()), observing);
    }

    @Override
    public KeYGen visitStateFml(CATsParser.StateFmlContext ctx) {
        return new StateFml((Predicate) ctx.pred.accept(this));
    }

    @Override
    public KeYGen visitPredicate(CATsParser.PredicateContext ctx) {
        if(ctx.boolExpr != null)
            return ctx.boolExpr.accept(this);
        if(ctx.negpred != null)
            return new Not((Predicate) ctx.negpred.accept(this));
        if(ctx.op != null)
            return new PredOp(
                (PredOp) ctx.pred1.accept(this),
                (PredOp) ctx.pred2.accept(this),
                ctx.op.getText()
        );
        if(ctx.TRUE() != null) return new TruePred();
        if(ctx.FALSE() != null) return new FalsePred();

        return null;
    }

    @Override
    public KeYGen visitBooleanExpr(CATsParser.BooleanExprContext ctx) {
        return new BoolOpExpr(
                (Expr) ctx.expr1.accept(this),
                (Expr) ctx.expr2.accept(this),
                ctx.op.getText()
        );
    }

    @Override
    public KeYGen visitExpr(CATsParser.ExprContext ctx) {
        if(ctx.term != null)
            return ctx.term.accept(this);
        if(ctx.op != null)
            return new ExprOp(
                    (Expr) ctx.expr1.accept(this),
                    (Expr) ctx.expr2.accept(this),
                    ctx.op.getText()
                    );
        return null;
    }

    @Override
    public KeYGen visitExprElem(CATsParser.ExprElemContext ctx) {

        if(ctx.val != null) {
            return new NumExprElem(Integer.parseInt(ctx.getText()));
        }
        if(ctx.var != null)
            return ctx.var.accept(this);
        return null;
    }

    @Override
    public KeYGen visitId(CATsParser.IdContext ctx) {
        return new Identifier (ctx.getText());
    }
}
