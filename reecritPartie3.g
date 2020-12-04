

tokens{

        SEQEXP='seqExp';
        NEGATION='negation';
        CALLEXP='callExp';
        INFIXEXP='infixExp';
        ARRCREATE='arrCreate';
        RECCREATE='recCreate';
        IDENTIFICATION;
}

seqExp
    :'(' (exp (';' exp)*)? ')' ->^(SEQEXP exp*)
    ;

negation
    : '-' exp -> ^(NEGATION exp)
    ;

/*callExp
    :id '(' (exp (',' exp)*)? ')' ->^(CALLEXP id exp*)
    ;*/

infixExp
    :exp infixOp exp -> ^(INFIXEXP exp exp)
    ;

/*arrCreate
    :tyId '[' i1=exp ']' of i2=exp ->^(ARRCREATE tyId $i1 $i2)
    ;*/

/*recCreate
    :tyId '{' fieldCreate ∗, '}' ->^(RECCREATE tyId fieldCreate*)
    ;*/
    
//Rendre LL(1) cette partie de la grammaire

identification:
    :tyId createOrCall ->^(IDENTIFICATION tyId createOrCall)
    ;

createOrCall:
    :'(' (exp (',' exp)*)? ')' ->^(CALLEXP exp*)
    |'[' i1=exp ']' of i2=exp ^(ARRCREATE $i1 $i2)
    |'{' fieldCreate ∗, '}' ->^(RECCREATE fieldCreate*)
    ;


//les expressions régulières reconnaissant les tokens

IDENTIFIER : ('a'..'z' | 'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*
;
INT: ('0'..'9')+
;
STRING: '"'.*'"'
;
WS: ('\r' | '\t' | '\n')+
;
