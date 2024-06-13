parser grammar CATsParser;
options { tokenVocab=CATsLexer; }


id: ALPHA+ DIGIT*;
exprElem : id | DIGIT+;

predOp : AND | OR ;

predicate :
        TRUE
    |   FALSE
    |   NOT LPAREN predicate RPAREN
    |   predicate predOp predicate
    |   booleanExpr;

boolExprOp : EQ | NEQ | GT | GE | LT | LE;
booleanExpr : exprElem EQ exprElem;

proof : assumeCats SEMI  catOf;
assumeCats : LBRACE (a=catOf (SEMI b=catOf)*)? RBRACE;
catOf : method=id COL cat;
cat : CAT_START preTr=trace CAT_SEP innerTr=trace CAT_SEP postTr=trace CAT_END;

trace :
        stateFml
    |   ABSTR(LBRACE id (COMMA id)* RBRACE)?ABSTR
    |   obs CONCAT trace
    |   trace traceOp trace;

traceOp : CHOP | CONCAT;
obs: id OBS_AS id;
stateFml : STATEFML predicate STATEFML;





