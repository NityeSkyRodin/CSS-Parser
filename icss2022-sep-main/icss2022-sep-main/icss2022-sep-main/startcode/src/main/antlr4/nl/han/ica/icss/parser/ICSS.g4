grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
//PA000

//PA001
stylesheet: astnode* ;

//PA001
astnode: stylerule | variableassigment;


rulebody: declaration | ifclause | variableassigment;
//PA001
stylerule: selector OPEN_BRACE rulebody* CLOSE_BRACE;

//PA001
selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;

//PA002
variableassigment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;

//PA004
ifclause: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE rulebody* CLOSE_BRACE (elseclause)?;
elseclause: ELSE OPEN_BRACE rulebody* CLOSE_BRACE;

//PA001
declaration: LOWER_IDENT COLON expression SEMICOLON;

//PA002/PA003
expression:  operation | literalExpression;

//PA003
operation: literalExpression (operatorLiteral) expression;

//PA003
operatorLiteral: MUL | PLUS | MIN;

//PA001/PA002
literalExpression: literal | variableReference;

//PA002
variableReference: CAPITAL_IDENT;

//PA001
literal : COLOR | PIXELSIZE | TRUE | FALSE | PERCENTAGE | SCALAR;

