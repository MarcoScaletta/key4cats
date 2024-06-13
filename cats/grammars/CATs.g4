grammar CATs;
import CATSLexer;


problem : assumeCats SEMI  target=catOf;

id: ALPHA+ DIGIT*;

exprElem : var=id | val=DIGIT+;
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
booleanExpr : expr1=exprElem op=boolExprOp expr2=exprElem;

assumeCats : LBRACE (a=catOf (SEMI b=catOf)*)? RBRACE;
catOf : method=id COL cat;
cat : CAT_START preTr=trace CAT_SEP innerTr=trace CAT_SEP postTr=trace CAT_END;

trace :
        stateFml
    |   absTr
    |   obs DOT tr=trace
    |   tr1=trace op=traceOp tr2=trace;
absTr : ABSTR(LBRACE id (COMMA id)* RBRACE)?ABSTR;
traceOp : CHOP | SEMI;
obs: observing=id OBS_AS observed=id;
stateFml : STATEFML pred=predicate STATEFML;






