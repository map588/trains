grammar PLC;

program : (statement | statement NEWLINE | NEWLINE)+;
statement : set_list_value | request_direction | if_statement | if_else_statement | COMMENT ;

set_list_value : list_name '[' index ']' '=' (equality_check | compound_value | value_false | value_true) ;
request_direction : REQUEST_DIRECTION '(' index ',' (INBOUND | OUTBOUND) ')' ;

if_statement
    : IF (compound_value | equality_check)
    NEWLINE (statement | statement NEWLINE | NEWLINE)+
    NEWLINE ENDIF ;

if_else_statement
    : IF (compound_value | equality_check)
    NEWLINE (statement | statement NEWLINE | NEWLINE)+ NEWLINE ELSE
    NEWLINE (statement | statement NEWLINE | NEWLINE)+
    NEWLINE ENDIF ;

equality_check : equals_statement | not_equals_statement ;
equals_statement : compound_value '==' (compound_value | value_false | value_true) ;
not_equals_statement : compound_value '!=' (compound_value | value_false | value_true) ;

compound_value : (and_operator | or_operator | single_val) ;

or_operator: (and_operator | single_val | ('(' compound_value ')')) (OR (and_operator | single_val | ('(' compound_value ')')))+ ;
and_operator: (single_val | ('(' compound_value ')')) (AND (single_val | ('(' compound_value ')')))+ ;
single_val : not_operator | list_value ;
not_operator : NOT list_value ;
list_value : list_name '[' index ']' ;
index : INDEX ;
list_name : (OCCUPANCY | SWITCH | LIGHT | CROSSING | AUTHORITY | DIRECTION | DIR_ASSIGNED) ;
value_false : FALSE | RED | MAIN | CLOSED | OUTBOUND ;
value_true : TRUE | GREEN | ALT | OPEN | INBOUND ;

AND : 'and' | 'AND' ;
OR : 'or' | 'OR' ;
NOT : 'not' | 'NOT' ;
IF : 'if' | 'IF' ;
ELSE : 'else' | 'ELSE' ;
ENDIF : 'endif' | 'ENDIF' ;
COMMENT : '//' ~( '\r' | '\n' )* NEWLINE ;
REQUEST_DIRECTION : 'request_direction' ;

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
INBOUND : 'INBOUND' ;
OUTBOUND : 'OUTBOUND' ;

INDEX : [0-9]+ ;
NEWLINE : ('\r'? '\n' | '\r')+ ;
WS : (' ' | '\t') -> skip ;