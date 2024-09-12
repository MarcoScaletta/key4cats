package key4cats;

import de.uka.ilkd.key.util.Pair;
import key4cats.parsers.CATs.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.*;


public class ProofCATsBuilder extends CATsBaseVisitor<KeYGen>{

    public enum Mode {ALL, SINGLE};

    private final String include = "traceRules.key";
    private final String className;
    private final String javaSource = ".";
    private final Map<Identifier, Contract> contractsMap;
    private final Set<Identifier> contractToBeGenerated;
    private final Mode mode;
    public ProofCATsBuilder(String catsFileName) {
        CATsLexer java8Lexer = new CATsLexer(CharStreams.fromString(catsFileName));
        CATsParser parser = new CATsParser(new CommonTokenStream(java8Lexer));
        CATsParser.ProblemContext problemContext = parser.problem();

        try{
            CATsParser.ModContext modeCtx = problemContext.mod();
            className = problemContext.className.getText();

            this.contractsMap = createContractMap(problemContext.contractWithId());
            if(modeCtx.SINGLE() != null) {
                mode = Mode.SINGLE;
                contractToBeGenerated = Set.of((Identifier) problemContext.mod().id().accept(this));
            }
            else {
                mode = Mode.ALL;
                contractToBeGenerated = this.contractsMap.keySet();
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(String.format("Exception while parsing: %s", e.getMessage()) );
        }
    }

    public Mode getMode(){
        return mode;
    }


    private Map<Identifier,Contract> createContractMap(List<CATsParser.ContractWithIdContext> contractWithIdContext){
        Map<Identifier,Contract> map = new HashMap<>();
        contractWithIdContext.forEach(
                ctx->{
                    Identifier id = (Identifier) ctx.id().accept(this);
                    if(map.containsKey(id))
                        throw new RuntimeException(String.format("Multiple declarations for contract %s", id.toKeY()));
                    map.put(id, this.getContractFromCtx(ctx));
                }
        );
        return map;
    }

    public Proof assembleProof(Identifier contractID){
        Contract toProve =this.contractsMap.get(contractID);
        if(toProve == null)
            throw new RuntimeException(String.format("Cannot prove undefined contract \"%s\"",
                    contractID.toKeY()));
        CATof target = toProve.target();
        List<AssumeCAT> assumeCATs = toProve.contractIds().stream().map(

                x-> {
                    if (this.contractsMap.get(x) == null)
                        throw new RuntimeException(String.format("Contract \"%s\" is assumed but not declared",
                                x.toKeY()));
                    return    new AssumeCAT(this.contractsMap.get(x).target());
                }
        ).toList();
        return new Proof(String.format("\"%s\"", include), javaSource, new Problem( assumeCATs,target));
    }

    private Contract getContractFromCtx(CATsParser.ContractWithIdContext ctx){
        return new Contract(
                (Identifier) ctx.id().accept(this),
                ctx.contract().id().stream().map(x -> (Identifier) x.accept(this)).toList(),
                (CATof) ctx.contract().target.accept(this));

    }

    public Set<Identifier> getContractIds(){
        return contractToBeGenerated;
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
            return ctx.stateFml().accept(this);
        if(ctx.event() != null)
            return ctx.event().accept(this);
        System.err.print("returning null trace " + ctx.getText());
        return null;
    }

    @Override
    public KeYGen visitAbsTr(CATsParser.AbsTrContext ctx) {

        return new AbsTr(ctx.id()!=null?
                ctx.id().stream().map(this::getProcName).toList()
    : List.of());
    }

    @Override
    public KeYGen visitObs(CATsParser.ObsContext ctx) {
        if(ctx.observing.getText().equals("thisCallId"))
            throw new RuntimeException("Observing variables cannot be called \"callId\".");
        Identifier observing = (Identifier) ctx.observing.accept(this);
        return new Obs(getProgVarName(ctx.observed), observing);
    }

    @Override
    public KeYGen visitStateFml(CATsParser.StateFmlContext ctx) {
        return new StateFml((Predicate) ctx.pred.accept(this));
    }

    @Override
    public KeYGen visitEvent(CATsParser.EventContext ctx) {
        KeYGen k = ctx.ctxId.accept(this);
        if(ctx.STARTEV() != null)
            return new Event("start", getProcName(ctx.mId), ctx.ctxId.accept(this));
        if(ctx.POPEV() != null)
            return new Event("pop", getProcName(ctx.mId), ctx.ctxId.accept(this));
        if(ctx.RETEV() != null)
            return new Event("ret", ctx.ctxId.accept(this));
        System.err.println("returning null event " + ctx.getText());
        return null;
    }

    @Override
    public KeYGen visitPredicate(CATsParser.PredicateContext ctx) {
        if(ctx.boolExpr != null)
            return ctx.boolExpr.accept(this);
        if(ctx.negpred != null)
            return new Not((Predicate) ctx.negpred.accept(this));
        if(ctx.op != null)
            return new PredOp(
                (Predicate) ctx.pred1.accept(this),
                (Predicate) ctx.pred2.accept(this),
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
            return ctx.val.accept(this);
        }
        if(ctx.var != null)
            return ctx.var.accept(this);
        return null;
    }


    @Override
    public KeYGen visitNatural(CATsParser.NaturalContext ctx) {
        return new NumExprElem(Integer.parseInt(ctx.getText()));
    }

    @Override
    public KeYGen visitId(CATsParser.IdContext ctx) {
        return new Identifier (ctx.getText());
    }

    private Identifier getProcName(CATsParser.IdContext ctx){
        return new Identifier(className + "::" + ctx.getText());
    }
    private Identifier getProgVarName(CATsParser.IdContext ctx){
        return new Identifier(className + "." + ctx.getText());
    }

    @Override
    public KeYGen visitContextId(CATsParser.ContextIdContext ctx) {
        if(ctx.natural() != null)
            return ctx.natural().accept(this);
        if(ctx.WILDCARD() != null)
            return new Wildcard();
        if(ctx.ID() != null)
            return new CallId();
        System.err.printf("Context id dmust be either natural or wildcard, but it is:%s%n", ctx.getText());
        return null;
    }
}
