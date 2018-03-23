%{
	//Panagiotis Kouzouglidis
	#include<stdio.h>
	#include <stdlib.h>
	#include "defs.h"
	#include <string.h>
	
	extern FILE *yyin;
	extern int yyparse (void);
	
	void yyerror(char *s);

	int classCounter = 0;
	int isVar = 0;
	int isClassVar = 0; //1 for class 0 for function
	int isArray = 0;
	char intToStr[20];
%}

%union {
	int ival;
	float rval;
	char *sval;
}

%token PROGRAM CLASS VAR PROCEDURE INTEGER REAL ARRAY OF BEGIN_ END_ ASSIGN IF
%token THEN ELSE WHILE DO RETURN NOT DIV MOD OR AND EQUAL NOT_EQUAL GREATER_EQUAL
%token LESS_EQUAL <ival> INTEGER_CONST <rval> REAL_CONST <sval> ID EXTENDS PRINT END_CLASS

%right IF THEN ELSE
%left OR AND NOT
%left LESS_EQUAL '<' GREATER_EQUAL '>' NOT_EQUAL EQUAL
%left '+' '-' '*' '/' DIV MOD

%start program

%% 
program: 
		PROGRAM {isMain = 1; wt("#include <stdio.h>\n"); wt("#include <stdlib.h>\n"); wt("#include \"output_file.h\"\n\n"); isMain = 0;} ID ';' multiple_classes {isMain = 1; createClass($3); wt("\nvoid main(){\n");} body '.' {wt("\n}");}
			{
				writeFunctionPrototypes();
				printf("PROGRAM ID %s\n", $3);
			}
		;
	
class: 
		CLASS ID {createClass($2);} extend multiple_local {checkCD(); wt("\ntypedef struct "); wt($2); wt("{\n"); writeVariables(lastClass->varList);} END_CLASS{wt("} "); wt($2); wt(";\n"); classCounter += 1;}
		; 

extend: 
		/*empty*/ 
		| EXTENDS ID {extends(lastClass->name, $2);}
		;

multiple_classes:
		| class
		| multiple_classes class 
		;

body: 
		multiple_local {writeVariables(lastClass->varList);} block
		;
		
multiple_local:
		local
		| multiple_local local

local: 
		VAR {isClassVar = 1;} multiple_type
		| header ';' procedure_body ';' {isClass = 1;}
		;
		
procedure_body: {isClassVar = 0;} multiple_vars {writeVariables(lastClass->functionList->lastFunction->varList);} block {wt("\n}\n");}

multiple_vars:
		one_var
		| multiple_vars one_var
		;
		
one_var:
		VAR multiple_type
		;

multiple_type: 
		one_type
		| multiple_type one_type 
		;

one_type: 
		{isVar = 1;} multiple_id ':' type ';' 
			{
				if (isClassVar){
					setVarType(lastType, "class");
				}
				else{
					setVarType(lastType, "function");
				}
			}	
		;

header: 
		PROCEDURE ID  {isClass = 0; addFunction($2);} '(' 
			{	
				wt("\nvoid "); 
				wt(lastClass->name); 
				wt("_"); 
				wt($2); 
				wt("(");
				addArgument("self");
				strcpy(lastType, "class:");
				strcat(lastType, lastClass->name);
				writeArguments(lastType);
			} 
			
			multiple_formal ')' 
			{
				wt("){\n");
				if (strcmp($2, "constructor") == 0){
					writeConstructorInitializations();
				}
				else if (strcmp($2, "deconstructor") == 0){
					writeDeconstructor();
				}
			}
		;

multiple_formal: 
		formal
		| multiple_formal ',' formal 
		;

formal: 
		{isVar = 0;} VAR multiple_id ':' type {writeArguments(lastType);}
		;

multiple_id: 
		ID
		{
			if (isVar == 1){
				if (isClassVar){
					addVariable($1, "class");
				}
				else{
					addVariable($1, "function");
				}
			}
			else {//its argument
			
				addArgument($1);
			}
		}
		| multiple_id ',' ID 
			{
				if (isVar == 1){
					if (isClassVar){
						addVariable($3, "class");
					}
					else{
						addVariable($3, "function");
					}
				}
				else {//its argument
					addArgument($3);	
				}	
			}
		;

type: 
		INTEGER 
			{
				if (isArray == 0){
					strcpy(lastType, "integer");
				}
				else{
					strcpy(lastType, "int");
					strcat(lastType, ":");
					strcat(lastType, intToStr);
					
				}
			}
		| REAL
			{
				if (isArray == 0){
					strcpy(lastType, "real");
				}
				else{
					strcat(lastType, "float");
				}
			}
		| ARRAY {isArray = 1;} '[' INTEGER_CONST {sprintf(intToStr, "%d", $4);} ']' OF type {isArray = 0;}
		
		| ID
			{
				findClass($1);
				if (isArray == 1){
					strcat(lastType, $1);
				}
				else{
					strcpy(lastType, "class:");
					strcat(lastType, $1);
				}
			}
		;

block: 
		BEGIN_ multiple_stmt END_
		;

multiple_stmt: 
		stmt {wt(";\n");}
		| multiple_stmt ';' stmt {wt(";\n");}
		;

stmt: 
		';'
		| l_value ASSIGN {wt("=");} expr
		| block 
		| call
		| IF {wt("if(");} expr  {wt("){\n");} THEN stmt {wt(";\n}\n");} ELSE {wt("else {\n");} stmt {wt(";\n}\n");}
		| WHILE {wt("while(");} expr {wt("){\n");} DO  stmt {wt(";\n}\n");}
		| ID ':' stmt 
		| RETURN {wt(" return");}
		| PRINT '(' {wt("printf(");} print_type ':' 
			ID 
			{
				if (isClass == 1 && isMain == 1){
					
				}
				else{
					functionVarCheckCase = 0;
					findFunctionVariable(lastClass->name, lastClass->functionList->lastFunction->name, $6);
					
					if (functionVarCheckCase == -1){//subclass
						wt("self->");
					}
					else if (functionVarCheckCase == 1){//superclass
						wt("self->extClass");
					}		
				}
				
				wt($6);
			}
			')' {wt(")");}
		;
		
print_type: 
		INTEGER {wt("\"%%d\\n\", ");}
		| REAL {wt("\"%%f\\n\", ");}
		;
	
expr: 
		l_value 
		| r_value
		;

l_value: 
		ID 
		{	
			wt(" "); 
			functionVarCheckCase = 0; //if == -1 its from subclass else if == 1 its from superclass
			if (isClass == 0){//its function variable
			
				findFunctionVariable(lastClass->name, lastClass->functionList->lastFunction->name, $1);
				if (functionVarCheckCase == -1){//its from subclass
					wt("self->");
				}
				else if (functionVarCheckCase == 1){//its from superclass
					wt("self->extClass->");
				}
			}
			
			wt($1); 
			wt(" ");
		}
		| l_value '[' {wt("[");} expr ']' {wt("]");}
		| '(' l_value ')'
		;

r_value: 
		INTEGER_CONST {fprintf(output_file, " %d", $1);}
		| REAL_CONST {fprintf(output_file, " %f", $1);}
		| '(' r_value ')'
		| call 
		| unop_expr 
		| binop_expr 
		;
		
call: 
		ID '(' 
			{ 
				if (isMain == 1){
					printf("Error: main can not call function '%s'\n", $1);
					exit(0);
				}
				
				findFunction(lastClass->name, $1);
				if(strcmp(tempClass->name, lastClass->name) == 0){
					wt(lastClass->name);
				}
				else{
					wt(tempClass->name); //extend class
				}
				wt("_");
				wt($1);
				{wt("(");}
				
				if(strcmp(tempClass->name, lastClass->name) == 0){
					wt("self");
				}
				else{
					wt("self->extClass"); //extend class
				}
				
				wt(",");
				
			} 
			multiple_expressions ')' {wt(")");}
		| ID '.' ID 
			{
				if (isClass == 1){
					strcpy(lastType, getClassType(findClassVariable(lastClass->name, $1)->type)); //lastType == lasts' var class name
					
					findFunction(lastType, $3);
					if (strcmp(tempClass->name, lastType) == 0 || isMain == 1){
						wt(lastType);
					}
					else{
						wt(tempClass->name);
					}
					
				}
				else{
					strcpy(lastType, getClassType(findFunctionVariable(lastClass->name, lastClass->functionList->lastFunction->name, $1)->type));

					findFunction(lastType, $3);
					if (strcmp(tempClass->name, lastType) == 0 || isMain == 1){
						wt(lastType);
					}
					else{
						wt(tempClass->name);
					}
				}
				
				wt("_");
				wt($3);
				wt("(");
				
				if (strcmp(tempClass->name, lastType) == 0 || isMain == 1){
					if (isMain == 0){
						wt("self->");
					}
					wt($1);
				}
				else{
					wt($1);
					wt("->extClass");
				}
				
				wt(",");
				
			}
		'(' multiple_expressions ')' {wt(")");}
		;

multiple_expressions: 
		expr 
		| multiple_expressions ',' {wt(", ");} expr 
		;
		
unop_expr: 
		NOT {wt("!");} expr
		| '+' {wt("+");} expr
		| '-' {wt("-");} expr
		;

binop_expr: 
		expr '+' {wt("+");} expr 
		| expr '-' {wt("-");} expr 
		| expr '*' {wt("*");} expr 
		| expr '/' {wt("/");} expr
		| expr DIV expr
		| expr MOD {wt("%%");} expr
		| expr OR {wt(" ||");} expr 
		| expr AND {wt(" &&");} expr 
		| expr EQUAL {wt(" ==");} expr 
		| expr NOT_EQUAL {wt(" !=");} 
		| expr '<' {wt(" <");} expr 
		| expr LESS_EQUAL {wt(" <=");} expr 
		| expr '>' {wt(">");} expr 
		| expr GREATER_EQUAL {wt(" >=");} expr 
		;

%%

extern int line;
extern int column;
extern char expectedToken[100];
extern char oldToken[100];


void yyerror(char *s)
{
	printf("syntax error at %d:%d, expected: %s\n", line, column, oldToken);
	
	exit(0);
}

int main(int argc, char **argv){

	++argv; --argc;

	if(argc > 0){
		yyin = fopen(argv[0], "r+");
		output_file = fopen("output_file.c", "w");
		header_file = fopen("output_file.h", "w");
		
		if(yyin == NULL){
			yyin = stdin;
		}
		else{
			yyparse();
		}
	}
	else{
		yyin = stdin;
	}
	
	fclose(output_file);
	fclose(header_file);
	printf("Total classes: %d\n", classCounter);
	printf("Number of lines %d\n", line);
	
	printClasses();
	
	return 0;
}

