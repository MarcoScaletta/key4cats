package key4cats;

import key4cats.parsers.CATs.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.*;
import java.util.stream.Collectors;

public class ProofCATsBuilder extends CATsBaseVisitor<KeYGen>{


    private final String include = "traceRules.key";
    private final String className = "Traces";
    private final String javaSource = ".";
    private final Proof proof;
    private final String pathProblem;
    private final Identifier contractToProve;
    private final Map<Identifier, Contract> contractsMap;

    public ProofCATsBuilder(String catsFileName) {
        CATsLexer java8Lexer = new CATsLexer(CharStreams.fromString(catsFileName));
        CATsParser parser = new CATsParser(new CommonTokenStream(java8Lexer));
        CATsParser.ProblemContext problemContext = parser.problem();
        this.contractToProve = (Identifier) problemContext.id().accept(this);

        this.contractsMap = createContractMap(problemContext);
        this.pathProblem = contractToProve.id();
        this.proof = new Proof(String.format("\"%s\"", include), javaSource, assembleProblem());
    }

    private Map<Identifier,Contract> createContractMap(CATsParser.ProblemContext problemContext){
        Map<Identifier,Contract> map = new HashMap<>();
        problemContext.contractWithId().forEach(
                ctx->{
                    Identifier id = (Identifier) ctx.id().accept(this);
                    if(map.containsKey(id))
                        throw new RuntimeException(String.format("Multiple declarations for contract %s", id.toKeY()));
                    map.put(id, this.getContractFromCtx(ctx));
                }
        );
        return map;
    }

    private Problem assembleProblem(){
        Contract toProve =this.contractsMap.get(contractToProve);
        if(toProve == null)
            throw new RuntimeException(String.format("Cannot prove undefined contract \"%s\"",
                    contractToProve.toKeY()));
        CATof target = toProve.target();
        List<AssumeCAT> assumeCATs = toProve.contractIds().stream().map(

                x-> {
                    if (this.contractsMap.get(x) == null)
                        throw new RuntimeException(String.format("Contract \"%s\" is assumed but not declared",
                                x.toKeY()));
                    return    new AssumeCAT(this.contractsMap.get(x).target());
                }
        ).toList();
        return new Problem( assumeCATs,target);
    }

    private Contract getContractFromCtx(CATsParser.ContractWithIdContext ctx){
        return new Contract(
                (Identifier) ctx.id().accept(this),
                ctx.contract().id().stream().map(x -> (Identifier) x.accept(this)).toList(),
                (CATof) ctx.contract().target.accept(this));

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
