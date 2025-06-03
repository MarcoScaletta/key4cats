lexer grammar AsyncLexer;

WS:  [ \t\n\r\u00a0]+ -> channel(HIDDEN); //U+00A0 = non breakable whitespace
DIGIT: [0-9];
ALPHA: [a-zA-Z];

// STATEMENTS
SKIP_STMT: 'skip';
IF: 'if';
RETURN: 'return';
AWAIT: 'await';

// VAR TYPE
FUT : 'fut';
INT : 'int';

TRUE: 'true';
FALSE: 'false';
STARTEV: 'start';
POPEV: 'pop';
RETEV:'ret';
AND: '&';
OR: '|';
IMPL: '->';
EQ: '=';
EQQ: '==';
NEQ: '!=';
GT: '>';
LT: '<';
GE: '>=';
LE: '<=';
NOT: '!';
PLUS: '+';
MIN: '-';
TIMES: '*';
DIV: '/';
PERCENT : '%';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
COMMA: ',';
SEMI: ';';
WILDCARD: '_';

SL_COMMENT
:
	'//'
	(~('\n' | '\uFFFF'))* ('\n' | '\uFFFF' | EOF) -> channel(HIDDEN)
;

