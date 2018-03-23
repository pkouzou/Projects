%option noyywrap


%{
	//Panagiotis Kouzouglidis
	#include<stdio.h>
	#include<stdlib.h>
	#include "y.tab.h"
	
	void lineIncrease();
	void count();
	void setError(char *t);
	
	extern YYSTYPE yylval;
	extern int lineno;
	extern void yyerror(char *msg);
%}

%%

"program" {setError("ID after 'program'"); count(); return PROGRAM;}
"class" {setError("ID after 'class'"); count(); return CLASS;}
"extends" {setError("multiple classes declaration after 'extends'"); count(); return EXTENDS;}
"var" {setError("multiple variable declaration after 'var'"); count(); return VAR;}
"procedure" {setError("ID after 'procedure'"); count(); return PROCEDURE;}
"integer" {setError("';' after 'integer'"); count(); return INTEGER;}
"real" {setError("';' after 'real'"); count(); return REAL;}
"array" {setError("[ after 'array'"); count(); return ARRAY;}
"of" {setError("variable type after 'of'"); count(); return OF;}
"begin" {setError("multiple statement after 'begin'"); count(); return BEGIN_;}
"end" {count(); return END_;}
":=" {setError("expression after ':='"); count(); return ASSIGN;}
"if" {setError("expression after 'if'"); count(); return IF;}
"then" {setError("statement after 'then'"); count(); return THEN;}
"else" {setError("statement after 'else'"); count(); return ELSE;}
"while" {setError("expression after 'while'"); count(); return WHILE;}
"do" {setError("expression after 'do'"); count(); return DO;}
"return" {setError("unknown after 'return'"); count(); return RETURN;}
"not" {setError("expression after 'not'"); count(); return NOT;}
"div" {setError("expression after 'div'"); count(); return DIV;}
"mod" {setError("expression after 'mod'"); count(); return MOD;}
"and" {setError("expression after 'and'"); count(); return AND;}
"print" {setError("( after 'print'"); count(); return PRINT;}
"end_class"	{setError("unknown"); count(); return END_CLASS;}
"or" {setError("expression after 'or'"); count(); return OR;}
"=" {setError("expression after '='"); count(); return EQUAL;}
"<>" {setError("expression after '<>'"); count(); return NOT_EQUAL;}
">=" {setError("expression after '>='"); count(); return GREATER_EQUAL;}
"<=" {setError("expression after '<='"); count(); return LESS_EQUAL;}
" " {count();}
"\t" {count();}
"\n" {count(); lineIncrease();}
"\r" {count();}


[0-9]+ {setError("unknown after INTEGER_CONST"); yylval.ival = atoi(yytext);  count(); return INTEGER_CONST;}
[0-9][0-9]*"."[0-9][0-9]* {setError("unknown after REAL_CONST"); yylval.rval = atof(yytext); count(); return REAL_CONST;}
[a-zA-Z][a-zA-Z0-9]* {setError("unknown after ID"); yylval.sval = strdup(yytext); count(); return ID;}

[\+\-\*\/\<\>] {setError("expression"); count(); return *yytext;}
":" {setError("variable type after ':'"); count(); return *yytext;}
"," {setError("ID after ','"); count(); return *yytext;}
"(" {setError("multiple_formal after '('"); count(); return *yytext;}
"[" {setError("expression after '['"); count(); return *yytext;}
"." {setError("'(' after '.'"); count(); return *yytext;}

[\;\)\]] {setError("unknown"); count(); return *yytext;}

. {printf("INVALID %d\n", *yytext); exit(0);}

%%
int line = 1;

void lineIncrease(){
	line += 1;
}

int column = 1;

void count()
{
	int i;

	for (i = 0; yytext[i] != '\0'; i++)
		if (yytext[i] == '\n')
			column = 1;
		else if (yytext[i] == '\t')
			column += 8 - (column % 8);
		else
			column++;
}

char expectedToken[100] = "program";
char oldToken[100];

void setError(char t[100]){
	//printf("line %d column %d\n", line, column);
	strcpy(oldToken, expectedToken);
	strcpy(expectedToken, t);
	
}


