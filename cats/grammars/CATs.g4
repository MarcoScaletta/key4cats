grammar CATs;
import CATSLexer;


problem: (COL TEST | id) (contractWithId)+;

contractWithId : BRACKL id BRACKR contract;
contract : (LBRACE (id SEMI)+ RBRACE)?   target=catOf;

id: (ALPHA+ DIGIT*)+;
natural: DIGIT+;

exprElem : var=id | val=natural;
exprOp : op=(PLUS | MIN | TIMES | DIV) ;
expr: term=exprElem | expr1=expr op=exprOp expr2=expr;

predOp : AND | OR ;

predicate :
        TRUE
    |   FALSE
    |   NOT LPAREN  negpred=predicate RPAREN
    |   pred1=predicate op=predOp pred2=predicate
    |   boolExpr=booleanExpr;

boolExprOp : EQ | NEQ | GT | GE | LT | LE;
booleanExpr : expr1=expr op=boolExprOp expr2=expr;

catOf : method=id COL cat;
cat : CAT_START preTr=trace OR innerTr=trace OR postTr=trace CAT_END;

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
contextId : natural | WILDCARD;
traceOp : CHOP | SEMI | AND | OR;
obs: observed=id OBS_AS observing=id;
stateFml : STATEFML pred=predicate STATEFML;






