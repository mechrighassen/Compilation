grammar compil;

options { 
	k=1;
	output=AST;
}

tokens{
    FIELD;
	WHILE='while';
	DO='do';
	FOR='for';
	TO='to';
	LET='let';
	IN='in';
	END='end';
	CONDITION;
}

fieldCreate :
	id '=' exp -> ^(FIELD id exp)
	;

assignment :
	lValue ':' '=' exp ->^(AFFECT lValue exp)
	;
else :
	'else' exp -> exp
    |
	;
ifThen :
	'if' i1=exp 'then' i2=exp else -> ^(CONDITION $i1 $i2 else)
	; 
whileExp :
	'while' i1=exp 'do' i2=exp -> ^(WHILE $i1 $i2)
	;

forExp :
	'for' id ':' '=' i1=exp 'to' i2exp 'do' i3=exp ->^(FOR id $i1 $i2 $i3)
	;

letExp :
	'let' dec+ 'in' (exp (';' exp)*)? 'end'-> ^(LET dec+ exp*)
	;
