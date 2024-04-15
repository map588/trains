grammar PLC_example;

@parser::header {
    import waysideController.*;
}
@parser::members {
   private WaysideExecutor executor;
}

program[WaysideExecutor executor]
    : START statement_list END EOF
    {
        this.executor = executor;
    }
    ;

statement_list: (statement | NEWLINE)+ ;

statement : set_list_value | if_statement | if_else_statement | for_statement ;

set_list_value : list_name '['  arith_expression ']' '=' (equality_check | compound_value | bool_literal) ;

if_statement
    : IF (compound_value | equality_check)
    NEWLINE statement_list
    NEWLINE ENDIF ;

if_else_statement
    : IF (compound_value | equality_check)
    NEWLINE statement_list NEWLINE ELSE
    NEWLINE statement_list
    NEWLINE ENDIF ;

for_statement returns [int start, int end]
locals [String name]
    : FOR VARIABLE '=' s=arith_expression TO  e=arith_expression DO
      NEWLINE statement_list
      NEWLINE ENDFOR { $name = $VARIABLE.text; $start = $s.val; $end = $e.val; executor.addVariable($name, $start); }
      ;


equality_check : equals_statement | not_equals_statement ;
equals_statement : compound_value '==' (compound_value | bool_literal ) ;
not_equals_statement : compound_value '!=' (compound_value | bool_literal) ;

compound_value : and_operator  #compound_and
               | or_operator   #compound_or
               | single_val    #compound_single
               ;

or_operator: (and_operator | single_val | ('(' compound_value ')')) (OR (and_operator | single_val | ('(' compound_value ')')))+ ;
and_operator: (single_val | ('(' compound_value ')')) (AND (single_val | ('(' compound_value ')')))+ ;

single_val : not_operator | list_value ;

not_operator : NOT list_value ;

list_value : list_name '[' arith_expression ']' ;

arith_expression returns [int val]
: left=int_term OP=PLUS right=int_term { $val = $left.val + $right.val; }  #addition
| left=int_term OP=MINUS right=int_term { $val = $left.val - $right.val; } #subtraction
| int_term { $val = int_term().val; }  #one_term
;


int_term  returns [int val]
: int_val       { $val = int_val().val; }          #int_literal
| int_variable  { $val = int_variable().val; }     #int_var
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


bool_literal returns [boolean val]
            : value_true   { $val = true; }
            | value_false  { $val = false; }
            ;

value_false: FALSE | RED | MAIN | CLOSED | SOUTHBOUND ;
value_true : TRUE | GREEN | ALT | OPEN | NORTHBOUND ;



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

START : 'start plc' | 'START PLC' ;
END : 'end plc' | 'END PLC' ;
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
PLUS : '+' ;
MINUS : '-' ;

NEWLINE : ('\r'? '\n' | '\r')+ ;
WS : (' ' | '\t') -> skip ;

INT_VAL : [0-9]+ ;
VARIABLE : [a-zA-Z]+ | [a-zA-Z0-9]+ ;
