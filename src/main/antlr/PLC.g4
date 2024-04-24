grammar PLC;

program : (statement | statement NEWLINE | NEWLINE)+;
statement : set_list_value | if_statement | if_else_statement | for_statement ;

set_list_value : list_name '[' (int_val | int_variable | arith_expression) ']' '=' (equality_check | compound_value | value_false | value_true) ;

if_statement
    : IF (compound_value | equality_check)
    NEWLINE (statement | statement NEWLINE | NEWLINE)+
    NEWLINE ENDIF ;

if_else_statement
    : IF (compound_value | equality_check)
    NEWLINE (statement | statement NEWLINE | NEWLINE)+ NEWLINE ELSE
    NEWLINE (statement | statement NEWLINE | NEWLINE)+
    NEWLINE ENDIF ;

for_statement
    : FOR VARIABLE '=' (int_val | int_variable | arith_expression) TO (int_val | int_variable | arith_expression) DO
    NEWLINE (statement | statement NEWLINE | NEWLINE)+
    NEWLINE ENDFOR ;


equality_check : equals_statement | not_equals_statement ;
equals_statement : compound_value '==' (compound_value | value_false | value_true) ;
not_equals_statement : compound_value '!=' (compound_value | value_false | value_true) ;

compound_value : (and_operator | or_operator | single_val) ;

or_operator: (and_operator | single_val | ('(' compound_value ')')) (OR (and_operator | single_val | ('(' compound_value ')')))+ ;
and_operator: (single_val | ('(' compound_value ')')) (AND (single_val | ('(' compound_value ')')))+ ;
single_val : not_operator | list_value ;
not_operator : NOT list_value ;
list_value : list_name '[' (int_val | int_variable | arith_expression) ']' ;
arith_expression : (int_val | int_variable) ARITH_OP (int_val | int_variable) ;
int_val : INT_VAL ;
int_variable : VARIABLE  ;
list_name : (OCCUPANCY | SWITCH | LIGHT | CROSSING | AUTHORITY | DIRECTION | DIR_ASSIGNED) ;
value_false : FALSE | RED | MAIN | CLOSED | SOUTHBOUND ;
value_true : TRUE | GREEN | ALT | OPEN | NORTHBOUND ;


END : 'end' | 'END' ;
AND : 'and' | 'AND' ;
OR : 'or' | 'OR' ;
NOT : 'not' | 'NOT' ;
IF : 'if' | 'IF' ;
ELSE : 'else' | 'ELSE' ;
ENDIF : 'endif' | 'ENDIF' ;
FOR : 'for' | 'FOR' ;
ENDFOR : 'endfor' | 'ENDFOR' ;
COMMENT : '//' ~( '\r' | '\n' )* NEWLINE -> skip ;
TO : 'to' | 'TO' ;
DO : 'do' | 'DO' ;


OCCUPANCY : 'occupied' ;
SWITCH : 'switch' ;
LIGHT : 'light' ;
CROSSING : 'crossing' ;
AUTHORITY : 'authority' ;
DIRECTION : 'direction' ;
DIR_ASSIGNED : 'dir_assigned' ;

TRUE : 'TRUE' ;
FALSE : 'FALSE' ;
RED : 'RED' ;
GREEN : 'GREEN' ;
MAIN : 'MAIN' ;
ALT : 'ALT' ;
OPEN : 'OPEN' ;
CLOSED : 'CLOSED' ;
NORTHBOUND : 'NORTHBOUND' ;
SOUTHBOUND : 'SOUTHBOUND' ;

ARITH_OP : ('+' | '-') ;
NEWLINE : ('\r'? '\n' | '\r')+ ;
WS : (' ' | '\t') -> skip ;

INT_VAL : [0-9]+ ;
VARIABLE : [a-zA-Z]+ | [a-zA-Z0-9]+ ;
