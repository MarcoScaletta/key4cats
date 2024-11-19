lexer grammar CATsLexer;

WS:  [ \t\n\r\u00a0]+ -> channel(HIDDEN); //U+00A0 = non breakable whitespace
DIGIT: [0-9];
ALPHA: [a-zA-Z];

TRUE: 'true';
FALSE: 'false';
STARTEV: 'start';
POPEV: 'pop';
RETEV:'ret';
AND: '&';
OR: '|';
IMPL: '->';
EQ: '=';
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
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
COMMA: ',';
SEMI: ';';
WILDCARD: '_';

CAT_START: '<<';
CAT_END: '>>';
OBS_AS: '::';
CHOP: '**';
STATEFML: '`';
ABSTR: '~';
COL : ':';
DOT : '.';
BRACKL: '[';
BRACKR: ']';
ID : '\\id';
JAVA_EXT : '.java';

TEST: '\\test';
SINGLE: '\\single';
ALL: '\\all';
ASSUME: '\\assume';

SL_COMMENT
:
	'//'
	(~('\n' | '\uFFFF'))* ('\n' | '\uFFFF' | EOF) -> channel(HIDDEN)
;

