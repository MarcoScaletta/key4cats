grammar CATs;
import CATSLexer;


problem: contractWithId+;

contractWithId : BRACKL (TEST OBS_AS)? id BRACKR contract;
contract : (LBRACE (id SEMI)+ RBRACE)?   target=catOf;
mod: (SINGLE COL contractId=id) | ALL | TEST ;
id: (ALPHA+ DIGIT*)+;
javaFileName: (ALPHA+ DIGIT*)+ JAVA_EXT;
natural: DIGIT+;
exprElem : var=id | val=natural;
exprOp : op=(PLUS | MIN | TIMES | DIV) ;
expr: term=exprElem | expr1=expr op=exprOp expr2=expr | LPAREN expr1=expr op=exprOp expr2=expr RPAREN;

predOp : AND | OR | IMPL;

predicate :
        TRUE
    |   FALSE
    |   NOT LPAREN  negpred=predicate RPAREN
    |   pred1=predicate op=predOp pred2=predicate
    |   LPAREN  pred1=predicate op=predOp pred2=predicate RPAREN
    |   boolExpr=booleanExpr;

boolExprOp : EQ | NEQ | GT | GE | LT | LE;
booleanExpr : expr1=expr op=boolExprOp expr2=expr | LPAREN expr1=expr op=boolExprOp expr2=expr RPAREN ;

catOf : method=id COL cat;
oldSyntaxCat : CAT_START preTr=trace OR innerTr=innerTrace OR postTr=trace CAT_END;

cat :
    (REQUIRES COL preTr=trace SEMI
    ENSURES COL innerTr=innerTrace SEMI
    EXPECTS COL postTr=trace SEMI)
    |
    (
    CAT_START preTr=trace OR innerTr=innerTrace OR postTr=trace CAT_END
    )

    ;

innerTrace:
    fullTrace=trace |
    shortTrace=BRACKL inner=trace BRACKR CHOP postCond=trace
    ;

trace :
        stateFml
    |   absTr
    |   obs DOT tr=trace
    |   tr1=trace op=traceOp tr2=trace
    |   LPAREN tr1=trace op=traceOp tr2=trace RPAREN
    |   event;
event :
        (STARTEV | POPEV) LPAREN mId=id COMMA ctxId=contextId RPAREN |
        RETEV LPAREN ctxId=contextId RPAREN ;
absTr : ABSTR(LBRACE id (COMMA id)* RBRACE)?ABSTR;
contextId : natural | WILDCARD | ID;
traceOp : CHOP | SEMI | AND | OR;
obs: observed=id OBS_AS observing=id;
stateFml : STATEFML pred=predicate STATEFML;






