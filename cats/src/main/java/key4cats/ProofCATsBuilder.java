package key4cats;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import key4cats.parsers.CATs.*;
import org.antlr.v4.runtime.*;
import com.github.javaparser.JavaParser;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

import static key4cats.TraceOp.chop;


public class ProofCATsBuilder extends CATsBaseVisitor<KeYGen>{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProofCATsBuilder.class);

    private final String include = "traceRules.key";
    private final String className;
    private final String javaSource = ".";
    private final String javaFileActualSource;
    private final Set<String> javaVarNames = new LinkedHashSet<>();
    private final Set<String> javaMethodNames = new LinkedHashSet<>();
    private final Map<String, Contract> contractsMap;
    private final Set<String> contractToBeGenerated;
    private final KeY4CATs.ProofGenMode mode;
    private Identifier currentCAT_ID = null;
    public
    ProofCATsBuilder(File catsFile, String contract, String className, KeY4CATs.ProofGenMode mode) throws FileNotFoundException {
        Map<String, Contract> contractsMapTMP = new HashMap<>();
        Set<String> contractToBeGeneratedTMP = new HashSet<>();
        this.className = className;
        this.mode = mode;

        javaFileActualSource = catsFile.getParent() + "/" + className + ".java";
        File javaFile = new File(javaFileActualSource);
        if(!javaFile.exists()) {
            throw new RuntimeException("File " + javaFileActualSource + " does not exist.");
        }
        setJavaVarMethodNames(javaFile);

        try {
            final InputStream targetStream = new DataInputStream(new FileInputStream(catsFile));
            String catsFileContent = new String(targetStream.readAllBytes(), StandardCharsets.UTF_8);

            CATsLexer java8Lexer = new CATsLexer(CharStreams.fromString(catsFileContent));
            CATsParser parser = new CATsParser(new CommonTokenStream(java8Lexer));
            java8Lexer.removeErrorListeners();
            parser.removeErrorListeners();
            java8Lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
            parser.addErrorListener(ThrowingErrorListener.INSTANCE);

            CATsParser.ProblemContext problemContext = parser.problem();
            contractsMapTMP = createContractMap(problemContext.contractWithId());
            switch (this.mode) {
                case KeY4CATs.ProofGenMode.SINGLE:
                    contractToBeGeneratedTMP = Set.of(contract);
                    break;
                case KeY4CATs.ProofGenMode.FULL:
                    contractToBeGeneratedTMP = getFullDependency(contract);
                    break;
                case KeY4CATs.ProofGenMode.ALL:
                    contractToBeGeneratedTMP = contractsMapTMP.keySet();
                    break;
                default:
                    contractToBeGeneratedTMP = Set.of();
            }
        } catch (Exception e) {
            if(e instanceof ParseCancellationException) {
                LOGGER.error("Error parsing file " + Paths.get(catsFile.getAbsolutePath()).normalize());
                LOGGER.error("Syntax error: " + e.getMessage());
                System.exit(1);
            }
            else {
                throw new RuntimeException(String.format("Exception while building proof obligation: %s", e.getMessage()));
            }
        }
        this.contractsMap = contractsMapTMP;
        this.contractToBeGenerated = contractToBeGeneratedTMP;
    }

    public void generateProof(String directory, String contractName) throws IOException{
        File keyFile = new File(String.format("%s/%s.key", directory, contractName));
        LOGGER.info(String.format("Generating proof for %s in file %s", contractName, Paths.get(keyFile.getAbsolutePath()).normalize()));
        String proofObligation = this.assembleProof(contractName).toKeY();
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(keyFile, false));
        dataOutputStream.writeBytes(proofObligation);
        dataOutputStream.flush();
    }

    private void setJavaVarMethodNames(File file) throws FileNotFoundException {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(file);
        if (!parseResult.getProblems().isEmpty()) {
            StringBuilder message = new StringBuilder("Problem parsing " + javaFileActualSource + ":");
            for (com.github.javaparser.Problem p : parseResult.getProblems()){
                message.append(String.format("\n - %s", p.getMessage()));
            }
            throw new RuntimeException(message.toString());
        }
        Optional<CompilationUnit> cu = parseResult.getResult();
        if (cu.isEmpty()) {
            throw new RuntimeException("Problem parsing " + javaFileActualSource + ".");
        }
        if (cu.get().getClassByName(className).isEmpty()) {
            throw new RuntimeException("Class  " + className + " not defined in " + javaFileActualSource + ".");
        }

        ClassOrInterfaceDeclaration classDecl = cu.get().getClassByName(className).get();
        javaVarNames.addAll(classDecl.getFields().stream().flatMap(it -> it.getVariables().stream().map(v -> v.getName().toString())).toList());
        javaMethodNames.addAll(classDecl.getMethods().stream().map(v -> v.getName().toString()).toList());

    }

    public Set<String> getFullDependency(String id){
        return getFullDependency(id, new LinkedHashSet<>());
    }


    public Set<String> getFullDependency(String id, Set<String> knownIds){
        Set<String> newDependencies = new LinkedHashSet<>(knownIds);
        newDependencies.add(id);
        if(!contractsMap.containsKey(id))
            throw new RuntimeException("Contract not defined: " + id);
        contractsMap.get(id).contractIds().forEach(
                contractId -> {
                    String contractIdStr = contractId.toKeY();
                    if(!knownIds.contains(contractIdStr))
                       newDependencies.addAll(getFullDependency(contractIdStr,newDependencies));
                }
        );
        return newDependencies;
    }

    private Map<String,Contract> createContractMap(List<CATsParser.ContractWithIdContext> contractWithIdContext){
        Map<String,Contract> map = new HashMap<>();
        contractWithIdContext.forEach(
                ctx->{
                    String id = ctx.id().getText();
                    if(map.containsKey(id))
                        throw new RuntimeException(String.format("Multiple declarations for contract %s", id));
                    map.put(id, this.getContractFromCtx(ctx));
                }
        );
        return map;
    }

    public Proof assembleProof(String contractID){
        Contract toProve =this.contractsMap.get(contractID);
        if(toProve == null)
            throw new RuntimeException(String.format("Cannot prove undefined contract \"%s\"",
                    contractID));
        CATof target = toProve.target();
        List<AssumeCAT> assumeCATs = toProve.contractIds().stream().map(
                x-> {
                    if (this.contractsMap.get(x.toKeY()) == null)
                        throw new RuntimeException(String.format("Contract \"%s\" is assumed but not declared",
                                x.toKeY()));
                    return    new AssumeCAT(this.contractsMap.get(x.toKeY()).target());
                }
        ).toList();
        return new Proof(javaSource, new Problem(assumeCATs,target));
    }

    private Contract getContractFromCtx(CATsParser.ContractWithIdContext ctx){
        return new Contract(
                (Identifier) ctx.id().accept(this),
                ctx.contract().id().stream().map(x -> (Identifier) x.accept(this)).toList(),
                (CATof) ctx.contract().target.accept(this));

    }

    public Set<String> getContractIds(){
        return contractToBeGenerated;
    }

    @Override
    public KeYGen visitCatOf(CATsParser.CatOfContext ctx) {
        if(!javaMethodNames.contains(ctx.id().getText()))
            throw new RuntimeException(String.format("Method \"%s\" not defined in class \"%s\"",ctx.id().getText(),className));
        Identifier currentCAT_ID = new Identifier(className +"::"+ ctx.id().getText());
        this.currentCAT_ID = currentCAT_ID;
        return new CATof(
                currentCAT_ID,
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
    public KeYGen visitInnerTrace(CATsParser.InnerTraceContext ctx) {
        Event startEv = new Event("start", currentCAT_ID, new CallId());
        Event popEv = new Event("pop", currentCAT_ID, new CallId());
        if(ctx.fullTrace != null) {
            TraceOp fullTrace = (TraceOp) ctx.fullTrace.accept(this);
            Set<Trace> traceElems = fullTrace.getElems();
            Trace newTrace = fullTrace;
            if(!traceElems.contains(startEv))
                newTrace = chop(startEv, newTrace);
            if(!traceElems.contains(popEv))
                newTrace = chop(newTrace,popEv);
            return newTrace;
        }
        if(ctx.shortTrace != null && currentCAT_ID != null) {
            return chop(
                    startEv,
                    (Trace) ctx.inner.accept(this),
                    popEv,
                    (Trace) ctx.postCond.accept(this)
                    );
        }
        return null;
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
        if(javaMethodNames.contains(ctx.observing.getText()))
            throw new RuntimeException("Name \"" + ctx.observing.getText() + "\" for observing variable not permitted: a method exists with the same name");
        if(javaVarNames.contains(ctx.observing.getText()))
            throw new RuntimeException("Name \"" + ctx.observing.getText() + "\" for observing variable not permitted: a program variable exists with the same name");
        if(!javaVarNames.contains(ctx.observed.getText()))
            throw new RuntimeException(String.format("Variable \"%s\" not defined in class \"%s\"",ctx.observed.getText(),className));

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

class ThrowingErrorListener extends BaseErrorListener {

    public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
            throws ParseCancellationException {
        throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
    }
}