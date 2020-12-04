grammar TigerLanguage;

options { 
    k=1;
    output=AST;
    ASTLabelType=XTree;
}

tokens {
    PROG;
    TYPEDEC;
    ARRAYOF;
    FIELDDEC;
    RECDEC;
    FUNCTION;
    VARDEC='varDec';
    UNSPECIFIEDTYPE='UnType';
    TYPESTRING='string';
    TYPEINT='int';
    TABACCESS='tabAccess';
    FIELDACCESS='fieldAccess';
    NIL='nil';
    BREAK;
    FIELD;
	WHILE='while'; //ok
	DO='do'; //ok
	FOR='for'; //ok
	TO='to'; //ok
	LET='let'; //ok
	IN='in'; //ok
	END='end'; //ok
	CONDITION;
	SEQEXP;
    CALLEXP;
    ARRCREATE;
    RECCREATE;
    BREAK='break';
    NEGATION;
    AFFECT;
    IF;
    THEN;
    ELSE;
    PLUS='+';
    MOINS='-';
    MULT='*';
    DIV='/';
    ORToken='|';
    ANDToken='&';
    InfToken='<';
    SupToken='>';
    InfEqualToken='<=';
    SupEqualToken='>=';
    EqualToken='=';
    DiffToken='<>';
}

@parser::header {
  package AST;
  import AST.*;
}

@lexer::header {
  package AST;
  import AST.*;
  import java.lang.StringBuilder;

}

@members {
    XTree identifierMemoire;
}



prog:   exp -> ^(PROG exp)
    ;






dec:    
    tyDec -> tyDec
    |   varDec -> varDec
    |   funDec -> funDec
    ;
tyDec:  
    'type' tyId '=' ty -> ^(TYPEDEC tyId ty)
    ;

ty  :   
    tyId -> tyId
    |   arrTy -> arrTy
    |   recTy -> recTy
    ;

arrTy:  
    'array of' tyId -> ^(ARRAYOF tyId)
    ;

recTy:  
    '{' (fieldDec (',' fieldDec)*)? '}' ->^(RECDEC fieldDec*)
    ;

fieldDec:
    id ':' tyId -> ^(FIELDDEC id tyId)
    ;

funDec:                                                     
    'function' id '(' (fieldDec (',' fieldDec)*)? ')' funDecValue -> ^(FUNCTION id fieldDec* funDecValue)
    ;

funDecValue:                                                          
    '=' exp ->  exp
    | ':' tyId '=' exp -> tyId exp
    ;

varDec:
    'var' id ':' varDecValue -> ^(VARDEC id varDecValue)
    ;

varDecValue:
    '=' exp -> exp
    | tyId ':' '=' exp -> tyId exp
    ;   



exp:
    infixAssignment -> infixAssignment
    | conditionExp -> conditionExp
    | letExp -> letExp
    | whileExp -> whileExp
    | forExp -> forExp
    ;

letExp :
	LET dec+ IN (exp (';' exp)*)? END-> ^(LET dec+ exp*)
	;

conditionExp options{greedy=true;} :
    'if' i1=conditionIf 'then' i2=conditionThen (-> $i1 $i2) {identifierMemoire = (XTree) $conditionExp.tree;} i3=ifnot -> $i3
    ;
conditionIf:
    exp->^(IF exp)
    ;
conditionThen:
    exp->^(THEN exp)
    ;
ifnot options{greedy=true;}:
    (-> {identifierMemoire}) 'else' conditionElse -> ^(CONDITION $ifnot conditionElse)
    | (-> {identifierMemoire}) -> ^(CONDITION $ifnot)
    ;
conditionElse:
    exp->^(ELSE exp)
    ;

whileExp :
	WHILE whi1=exp DO whi2=exp -> ^(WHILE $whi1 $whi2)
	;

forExp :
	FOR id ':' '=' i1=exp TO i2=exp DO i3=exp ->^(FOR id $i1 $i2 $i3)
	;


infixAssignment
    : (infixOr->infixOr) ( (':' '=') e1=infixOr-> ^(AFFECT $infixAssignment $e1))*
    ;
infixOr
    : infixAnd ( ORToken ^ infixAnd)*
    ;
infixAnd
    : infixComp ( ANDToken ^ infixComp)*
    ;
infixComp
    : infixAddSub ((EqualToken ^ | InfToken ^ | SupToken ^ | InfEqualToken ^| SupEqualToken ^| DiffToken ^) infixAddSub)*
    ;
infixAddSub
    : infixMulDiv ((PLUS ^| MOINS ^) infixMulDiv)*
    ;
infixMulDiv
    : negation ((MULT ^ | DIV ^) negation)*
    ;
negation: 
    '-' atomExp -> ^(NEGATION atomExp)
    | atomExp -> atomExp
    ;

atomExp:
    NIL -> NIL
    | intLit -> intLit
    | stringLit -> stringLit
    | seqExp -> seqExp
  //  | IDENTIFIER {identifierMemoire = new XTree(new CommonToken(IDENTIFIER, $IDENTIFIER.text));} beginningWithID -> beginningWithID
    | IDENTIFIER (-> IDENTIFIER) {identifierMemoire = $atomExp.tree;} beginningWithID -> beginningWithID
    | BREAK -> BREAK
    ;

seqExp
    :'(' (exp (';' exp)*)? ')' ->^(SEQEXP exp*)
    ;

/*
A partir d'un atomExp, on peut avoir 4 expressions commençant par un identifier :
- l'appel d'une fonction : id ( ... )
- l'instanciation d'un record : id { ... }
- l'accés à une variable : id ETC (où ETC peut valoir diverses choses, dont [exp], .id, .[id.id.id[5]], ...)
- l'instanciation d'un tableau : id[exp] of exp
- si on est dans aucun des cas ci-dessus, on a just un id seul.

Les deux premiers points sont faciles à traité car on peut faciliement différencier ( et {
Les deux derniers points sont plus compliqués : si on lit id[exp], on doit attendre l'unité lexicale suivante
pour savoir si on a un 'of' ou autre chose.

Ci-dessous, la règle 3 correspond au cas id.ETC. La règle 4 correspond à id[EXP] ETC ou id[EXP] of exp.
*/

beginningWithID:
    callExp -> callExp
    | recCreate -> recCreate
    | (->{identifierMemoire}) ('.' id -> ^(FIELDACCESS $beginningWithID id))  // id.
                (('[' i5=exp ']' -> ^(TABACCESS $beginningWithID $i5)) | ('.' i2=id -> ^(FIELDACCESS $beginningWithID $i2)))* //ETC
    | (->{identifierMemoire}) '[' e1=exp ']' (('of' e2=atomExp -> ^(ARRCREATE $beginningWithID $e1 $e2)) | (->^(TABACCESS $beginningWithID $e1)) //id[exp]
            (('[' i3=exp ']' -> ^(TABACCESS $beginningWithID $i3)) | ('.' i2=id -> ^(FIELDACCESS $beginningWithID $i2)))* ) //ETC
    | (-> {identifierMemoire})
    ;

callExp:
    (->{identifierMemoire}) '(' (exp (',' exp)* )? ')' -> ^(CALLEXP $callExp exp*)
    ;

recCreate: 
    (->{identifierMemoire})'{' (fieldCreate (',' fieldCreate)*)? '}' ->^(RECCREATE $recCreate fieldCreate*)
    ;

fieldCreate :
	id '=' exp -> ^(FIELD id exp)
	;



id: IDENTIFIER -> IDENTIFIER;
tyId : IDENTIFIER -> IDENTIFIER | TYPESTRING -> TYPESTRING | TYPEINT -> TYPEINT;
intLit: INTEGER -> INTEGER;
stringLit :
    STRING -> STRING
    ;


NIL : 'nil';    
IDENTIFIER: 
    ('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*
    ;
INTEGER: ('0'..'9')+;
//CHARACTER : ~('"' | '\\');
STRING @init{StringBuilder lBuf = new StringBuilder();}
:   '"'
    (
        chara=~('"' | '\\') {lBuf.append((char)$chara);}
    |
        '\\' (
                '"' {lBuf.append('"');} 
                | '\\' {lBuf.append('\\');} 
                | 'n' {lBuf.append('\n');}
                | 't' {lBuf.append('\t');}
                | INTEGER { int d = Integer.parseInt($INTEGER.text); if (d < 128) lBuf.append((char)d); else System.out.println("Warning l"+ getLine() + " : " + d + " is not a valid ASCII value.");}
                | WS+ '\\'
             )
    )+
    {setText(lBuf.toString());}
    '"'
    ;
  
WS: ('\r' | '\t' | '\n' | ' ')+ { $channel = HIDDEN; }; 
COMMENT: '//' ~('\n')* { $channel = HIDDEN; };
