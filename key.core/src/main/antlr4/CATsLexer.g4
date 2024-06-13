lexer grammar CATsLexer;

tokens {CAT_START}
WS:  [ \t\n\r\u00a0]+ -> channel(HIDDEN); //U+00A0 = non breakable whitespace
DIGIT: [0-9];
ALPHA: [a-zA-Z_];
TRUE: 'true';
FALSE: 'false';
AND: '&&';
OR: '||';
EQ: '==';
NEQ: '!=';
GT: '>';
LT: '<';
GE: '>=';
LE: '<=';
NOT: '!';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
COMMA: ',';
SEMI: ';';

CAT_START: '<<';
CAT_END: '>>';
CAT_SEP: '|';
OBS_AS: '::';
CHOP: '**';
CONCAT: '.';
STATEFML: '`';
ABSTR: '~';
COL : ':';

