grammar AsyncParser;
import AsyncLexer;


program: procedure* main ;

main :  LBRACE
            var_decls
            (stmt)+
        RBRACE;
procedure : proc_name=id
            LPAREN parameters? RPAREN
            LBRACE
                var_decls?
                (stmt)+
                RETURN SEMI
            RBRACE
            ;

var_decl : (FUT | INT ) id ( COMMA id )*;
parameters : FUT id ( COMMA FUT b=id )* ;
var_decls : (var_decl SEMI )+;

stmt :
    SKIP_STMT
    | assign_stmt SEMI
    | await_stmt SEMI
    | assign_stmt SEMI
    | if_stmt
;
assign_stmt : id EQ (expr | call) ;
call : NOT id LPAREN params=id* RPAREN ;
await_stmt : AWAIT LPAREN id RPAREN ;
if_stmt : IF LPAREN booleanExpr RPAREN LBRACE stmt RBRACE ;


// EXPRESSIONS
boolExprOp : EQQ  | NEQ | GT | GE | LT | LE;
booleanExpr : expr1=expr op=boolExprOp expr2=expr | LPAREN expr1=expr op=boolExprOp expr2=expr RPAREN | TRUE | FALSE ;
exprElem : var=id | val=natural;
exprOp : op=(PLUS | MIN | TIMES | DIV | PERCENT)  ;
expr:   term=exprElem
        | expr1=expr op=exprOp expr2=expr
        | LPAREN expr1=expr op=exprOp expr2=expr RPAREN;

// ID AND NUM
natural: DIGIT+;
id: (ALPHA+ DIGIT*)+;