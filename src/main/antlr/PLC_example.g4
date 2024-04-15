grammar PLC_example;

@parser::header {
    import waysideController.*;
}
@parser::members {
   private WaysideExecutor executor;
}

program[WaysideExecutor executor]
    : (statement | statement NEWLINE | NEWLINE)+ EOF
    {
        this.executor = executor;
    }
    ;

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

for_statement returns [int start, int end]
locals [String name]
    : FOR VARIABLE '='       { name = $VARIABLE.text; executor.addVariable(name); }
        ( int_val            { $start = $int_val.val; }
        | arith_expression   { $start = $arith_expression.val; }
        )
      TO  (int_val           { $end = $int_val.val; }
          |arith_expression  { $end = $arith_expression.val; }
          )
      DO
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

list_value : list_name '[' int_term | arith_expression ']' ;

arith_expression returns [int val]
: left=int_term OP='+' right=int_term { $val = $left.val + $right.val; }
| left=int_term OP='-' right=int_term { $val = $left.val - $right.val; }
;


int_term  returns [int val]
: int_val  {      $val = int_val.int; }
| int_variable  { $val = executor.getVariable(int_variable.text); }
;

int_val returns [int val]
: INT_VAL  { $val = Integer.parseInt($INT_VAL.text); }
;
int_variable returns [int val]
: VARIABLE  { $val = executor.getVariable($VARIABLE.text);}
;

list_name returns [String name]
        : OCCUPANCY     { $name = "occupancy"; }
        | SWITCH        { $name = "switch"; }
        | LIGHT         { $name = "light"; }
        | CROSSING      { $name = "crossing"; }
        | AUTHORITY     { $name = "authority"; }
        | DIRECTION     { $name = "direction"; }
        | DIR_ASSIGNED  { $name = "dir_assigned"; }
        ;

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

NEWLINE : ('\r'? '\n' | '\r')+ ;
WS : (' ' | '\t') -> skip ;

INT_VAL : [0-9]+ ;
VARIABLE : [a-zA-Z]+ | [a-zA-Z0-9]+ ;
