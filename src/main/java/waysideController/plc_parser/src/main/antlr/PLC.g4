grammar PLC;

program : (statement | statement NEWLINE)+;
statement : set_list_value | if_statement | if_else_statement ;

set_list_value : list_value '=' (list_value | value_false | value_true) ;
if_statement : 'if' compound_value NEWLINE statement NEWLINE 'endif' ;
if_else_statement : 'if' compound_value NEWLINE statement NEWLINE 'else' NEWLINE statement NEWLINE 'endif' ;

compound_value
    : (single_val | or_operator | and_operator)
    | '(' compound_value ') and (' compound_value ')'
    | '(' compound_value ') and' single_val
    | single_val 'and (' compound_value ')'
    | '(' compound_value ') or (' compound_value ')'
    | '(' compound_value ') or' single_val
    | single_val 'or (' compound_value ')' ;

or_operator: single_val ('or' single_val)+ ;
and_operator: single_val ('and' single_val)+ ;
single_val : not_operator | list_value ;
not_operator : 'not' list_value;
list_value : (OCCUPANCY | SWITCH | SWITCH_REQUEST | LIGHT | CROSSING | AUTHORITY) '[' INDEX ']' ;
value_false : FALSE | RED | MAIN | CLOSED ;
value_true : TRUE | GREEN | ALT | OPEN ;

OCCUPANCY : 'occupancy' ;
SWITCH : 'switch' ;
SWITCH_REQUEST : 'switch_request' ;
LIGHT : 'light' ;
CROSSING : 'crossing' ;
AUTHORITY : 'authority' ;

TRUE : 'TRUE' ;
FALSE : 'FALSE' ;
RED : 'RED' ;
GREEN : 'GREEN' ;
MAIN : 'MAIN' ;
ALT : 'ALT' ;
OPEN : 'OPEN' ;
CLOSED : 'CLOSED' ;

INDEX : [0-9]+ ;

NEWLINE : ('\r'? '\n' | '\r')+ ;
WS : (' ' | '\t') -> skip;