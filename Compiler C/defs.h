//Panagiotis Kouzouglidis
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct var_list{
	struct var_node *firstVar;
	struct var_node *lastVar;
} var_list;

typedef struct var_node{
	char name[10];
	char type[20];
	struct var_node *nextVar;
} var_node;

typedef struct arg_list{
	struct arg_node *firstArg;
	struct arg_node *lastArg;
} arg_list;

typedef struct arg_node{
	char name[10];
	char type[20];
	struct arg_node *nextArg;
} arg_node;

typedef struct function_list{
	struct function_node *firstFunction;
	struct function_node *lastFunction;
} function_list;

typedef struct function_node{
	char name[15];
	struct function_node *nextFunction;
	struct arg_list *argList;
	struct var_list *varList;
} function_node;

typedef struct class_node{
	char name[15];
	struct class_node *nextClass;
	struct function_list *functionList;
	struct var_list *varList;
	struct class_node *extendClass;
	int numberOfConstructors;
	int numberOfDeconstructors;
} class_node;

class_node *firstClass = NULL;
class_node *lastClass = NULL;

class_node *tempClass = NULL;
function_node *tempFunction = NULL;
arg_node *tempArg = NULL;
var_node *tempVar = NULL;

int checkSubClass = 1;
int isFunctionSearch = 0;
int functionVarCheckCase = 0;
int isClass = 1;
int isMain = 0;
int isDuplicateSearch = 0;
char lastType[20];
char tempType[20];

FILE *output_file;
FILE *header_file;

void wt(char *s){
	
	if (isClass == 1 && isMain == 0){
		fprintf(header_file, (void *)s);
	}
	else{
		fprintf(output_file, (void *)s);
	}
	
}

void writeConstructorInitializations(){//inits lasts' class struct variables using malloc

	char *token;
	tempClass = lastClass;
	
	if (tempClass->varList == NULL){
		return;
	}
	
	tempVar = tempClass->varList->firstVar;
	
	while (tempVar != NULL){
		if (strcmp(tempVar->type, "integer") == 0 || strcmp(tempVar->type, "real") == 0){
			tempVar = tempVar->nextVar;
			continue;
		}
		
		strcpy(tempType, tempVar->type);
		token = strtok(tempType, ":");

		if (strcmp(token, "class") == 0){
			token = strtok(NULL, ":");
			wt("self->");
			wt(tempVar->name);
			wt(" = malloc(sizeof(");
			wt(token);
			wt("));\n");
		}
		
		tempVar = tempVar->nextVar;
	}
}

void writeDeconstructor(){
	
	if (lastClass->extendClass != NULL){
		wt("free(self->extClass);\n");
		wt("free(self);\n");
	}
}

void checkCD(){//checks the existence of lasts' class constructor and deconstructor
	
	if (lastClass->numberOfConstructors == 0){
		printf("Class '%s' must have one constructor\n", lastClass->name);
		exit(0);
	}
		
	if (lastClass->numberOfDeconstructors == 0){
		printf("Class '%s' must have one deconstructor\n", lastClass->name);
		exit(0);
	}
}

void createClass(char *name){
	
	if (firstClass == NULL){
		firstClass = (class_node *) malloc(sizeof(class_node));
		lastClass = (class_node *) malloc(sizeof(class_node));
		strcpy(firstClass->name, name);
		firstClass->nextClass = NULL;
		firstClass->functionList = NULL;
		firstClass->varList = NULL;
		firstClass->extendClass = NULL;
		firstClass->numberOfConstructors = 0;
		firstClass->numberOfDeconstructors = 0;
		lastClass = firstClass;
	}
	else{
		tempClass = (class_node *) malloc(sizeof(class_node));
		tempClass = (class_node *) malloc(sizeof(class_node));
		tempClass->nextClass = NULL;
		tempClass->functionList = NULL;
		tempClass->varList = NULL;
		tempClass->extendClass = NULL;
		tempClass->numberOfConstructors = 0;
		tempClass->numberOfDeconstructors = 0;
		strcpy(tempClass->name, name);
		lastClass->nextClass = (void *) tempClass;
		lastClass = tempClass;	
	}
}

void addFunction(char *name){//function is added to the last created class

	tempFunction = (function_node *) malloc(sizeof(function_node));
	strcpy(tempFunction->name, name);
	tempFunction->nextFunction = NULL;
	tempFunction->argList = NULL;
	tempFunction->varList = NULL;
	
	if (strcmp(tempFunction->name, "constructor") == 0){
		lastClass->numberOfConstructors += 1;
		if (lastClass->numberOfConstructors > 1){
			printf("Error: Only one constructor is allowed per class\n");
			exit(0);
		}
	}
	else if (strcmp(tempFunction->name, "deconstructor") == 0){
		lastClass->numberOfDeconstructors += 1;
		if (lastClass->numberOfDeconstructors > 1){
			printf("Error: Only one deconstructor is allowed per class\n");
			exit(0);
		}
	}
	
	if (lastClass->functionList == NULL){
		lastClass->functionList = (function_list *) malloc(sizeof(function_list));
		lastClass->functionList->firstFunction = tempFunction;
		lastClass->functionList->lastFunction = tempFunction;
	}
	else{
		lastClass->functionList->lastFunction->nextFunction = tempFunction;
		lastClass->functionList->lastFunction = tempFunction;
	}	
}

void addArgument(char *name){//argument is added to the last createdFunction
	
	tempArg = (arg_node *) malloc(sizeof(arg_node));
	strcpy(tempArg->name, name);
	tempArg->nextArg = NULL;
	strcpy(tempArg->type, "none");
	
	if (lastClass->functionList->lastFunction->argList == NULL){
		lastClass->functionList->lastFunction->argList = (arg_list *) malloc(sizeof(arg_list));
		lastClass->functionList->lastFunction->argList->firstArg = tempArg;
		lastClass->functionList->lastFunction->argList->lastArg = tempArg;
	}
	else{
		lastClass->functionList->lastFunction->argList->lastArg->nextArg = tempArg;
		lastClass->functionList->lastFunction->argList->lastArg = tempArg;
	}
	
}

void addVariableFunction(var_node *tempVar){
	
	if (lastClass->functionList->lastFunction->varList == NULL){
		lastClass->functionList->lastFunction->varList = (var_list *) malloc(sizeof(var_list));
		lastClass->functionList->lastFunction->varList->firstVar = tempVar;
		lastClass->functionList->lastFunction->varList->lastVar = tempVar;
	}
	else{
		lastClass->functionList->lastFunction->varList->lastVar->nextVar = tempVar;
		lastClass->functionList->lastFunction->varList->lastVar = tempVar;
	}
}

void addVariableClass(var_node *tempVar){
	
	if (lastClass->varList == NULL){
		lastClass->varList = (var_list *) malloc(sizeof(var_list));
		lastClass->varList->firstVar = tempVar;
		lastClass->varList->lastVar = tempVar;
	}
	else{
		lastClass->varList->lastVar->nextVar = tempVar;
		lastClass->varList->lastVar = tempVar;
	}
}

void addVariable(char *name, char *dest){//dest is function or class

	tempVar = (var_node *) malloc(sizeof(var_node));
	strcpy(tempVar->name, name);
	tempVar->nextVar = NULL;
	strcpy(tempVar->type, "none");

	if (strcmp(dest, "function") == 0){
		addVariableFunction(tempVar);
	}
	else{
		addVariableClass(tempVar);
	}
}

class_node *findClass(char *className){
	
	tempClass = firstClass;
	
	while (tempClass != NULL){
		
		if (strcmp(tempClass->name, className) == 0){
			return tempClass;
		}
		
		tempClass = tempClass->nextClass;
	}
	
	printf("Error: class '%s' not found\n", className);
	exit(0);
	
	return NULL;
}

function_node *findFunction(char *className, char *searchName){
	
	tempClass = findClass(className);
	
	if (tempClass->functionList != NULL){
		
		tempFunction = tempClass->functionList->firstFunction;
		
		while (tempFunction != NULL){
			
			if (strcmp(tempFunction->name, searchName) == 0){
				checkSubClass = 1;
				return tempFunction;
			}
			
			tempFunction = tempFunction->nextFunction;
		}
	}
	
	if (checkSubClass == 1){
		
		checkSubClass = 0;
		
		if (tempClass->extendClass != NULL){
			
			tempFunction = findFunction(tempClass->extendClass->name, searchName);
			if (tempFunction != NULL){
				return tempFunction;
			}
		}
		
		printf("Error: class '%s' has not a function named '%s'\n", className, searchName);
		exit(0);
	}
	
	checkSubClass = 1;
	return NULL;
}

arg_node *findArgument(char *className, char *functionName, char *searchName){
	
	tempFunction = findFunction(className, functionName);
	
	if (tempFunction->argList != NULL){
		
		tempArg = tempFunction->argList->firstArg;
		
		while(tempArg != NULL){
			
			if (strcmp(tempArg->name, searchName) == 0){
				return tempArg;
			}
			
			tempArg = tempArg->nextArg;
		}
	}
	
	return NULL;

}

var_node *findClassVariable(char *className, char *searchName){
	
	tempClass = findClass(className);
	
	if (tempClass->varList != NULL){
		
		tempVar = tempClass->varList->firstVar;
		while(tempVar != NULL){
			
			if (strcmp(tempVar->name, searchName) == 0){
				functionVarCheckCase = -1;
				return tempVar;
			}
			
			tempVar = tempVar->nextVar;
		}
	}
	
	if (checkSubClass == 1){
		
		checkSubClass = 0;
		if (tempClass->extendClass != NULL){
			tempVar = findClassVariable(tempClass->extendClass->name, searchName);
			
			if (tempVar != NULL){
				functionVarCheckCase = 1;
				return tempVar;
			}
		}
		
		if (isFunctionSearch == 0){
			printf("Error: class '%s' has not a variable named '%s'\n", className, searchName);
			exit(0);
		}
		
	}
	
	checkSubClass = 1;
	return NULL;
}

var_node *findFunctionVariable(char *className, char *functionName, char *searchName){
	
	tempFunction = findFunction(className, functionName);
	isFunctionSearch = 1;
	
	//search function's variables first
	if (tempFunction->varList != NULL){
		
		tempVar = tempFunction->varList->firstVar;
		
		while (tempVar != NULL){
			
			if (strcmp(tempVar->name, searchName) == 0){
				isFunctionSearch = 0;
				return tempVar;
			}
			
			tempVar = tempVar->nextVar;
		}
	}
	//search function's arguments
	tempArg = findArgument(className, functionName, searchName);
	if (tempArg != NULL){
		isFunctionSearch = 0;
		return (void *)tempArg;
	}
	//search subclass and superclass variables
	tempVar = findClassVariable(className, searchName);
	if (tempVar != NULL){
		isFunctionSearch = 0;
		return tempVar;
	}
	
	printf("Error: Variable '%s' can not be resolved for the function '%s'\n", searchName, functionName);
	exit(0);
	
	
}

void extends(char *subClass, char *superClass){
	 
	findClass(subClass)->extendClass = findClass(superClass);
	
	addVariable("extClass", "class");
	strcpy(lastClass->varList->lastVar->type, "class:");
	strcat(lastClass->varList->lastVar->type, superClass);
}

void setVarType(char *type, char *source){ //source = class or function
	
	if  (strcmp(source, "class") == 0){
		tempVar = lastClass->varList->firstVar;
	}
	else{
		tempVar = lastClass->functionList->lastFunction->varList->firstVar;
	}
	
	while (tempVar != NULL){
		
		if (strcmp(tempVar->type, "none") == 0){
			strcpy(tempVar->type, type);
		}
		
		tempVar = tempVar->nextVar;
	}
}


char *getClassType(char *fullType){
	
	char *token;
	strcpy(tempType, fullType);
	token = strtok(tempType, ":");
	token = strtok(NULL, ":");
	
	return token;
}

void writeBasedOnType(char *name, char *type){
	
	if (strcmp(type, "integer") == 0){
		wt("int ");
		wt(name);
	}
	else if (strcmp(type, "real") == 0){
		wt("float ");	
		wt(name);				
	}
	else{
		char *token;
		strcpy(tempType, type);
				
		token = strtok(tempType, ":");
				
		if (strcmp(token, "class") == 0){
			token = strtok(NULL, ":");
			wt("struct ");
			wt(token);
			wt(" *");
			wt(name);
		}
		else{
			wt(token);
			wt(" ");
			wt(name);
			wt("[");
			token = strtok(NULL, ":");
			wt(token);
			wt("]");
		}
	}	
}

void writeFunctionPrototypes(){
	
	isClass = 1;//enables writing at header file
	isMain = 0;
	tempClass = firstClass;
	
	while (tempClass->nextClass != NULL){
		
		if (tempClass-> functionList == NULL) continue;
		
		tempFunction = tempClass->functionList->firstFunction;
		
		while (tempFunction != NULL){
	
			wt("\nvoid ");
			wt(tempClass->name);
			wt("_");
			wt(tempFunction->name);
			wt("(");
			
			if (tempFunction->argList == NULL){
				return;
			}
			
			tempArg = tempFunction->argList->firstArg;
			
			if (tempArg != NULL){
				writeBasedOnType(tempArg->name, tempArg->type);
				tempArg = tempArg->nextArg;
			}
			
			while(tempArg != NULL){
				
				wt(", ");
				writeBasedOnType(tempArg->name, tempArg->type);
				tempArg = tempArg->nextArg;
			}
			
			wt(");\n");
			
			tempFunction = tempFunction->nextFunction;
		}
		
		tempClass = tempClass->nextClass;
	}
}

void writeVariables(var_list *tempVarList){
	
	if (tempVarList == NULL){
		return;
	}
	
	tempVar = tempVarList->firstVar;
	
	while(tempVar != NULL){
		
		writeBasedOnType(tempVar->name, tempVar->type);
		wt(";\n");
		
		tempVar = tempVar->nextVar;
	}
}

void writeArguments(char *type){
	tempArg = lastClass->functionList->lastFunction->argList->firstArg;
	int isFirst = 1;
	while (tempArg != NULL){
		
		if (strcmp(tempArg->type, "none") == 0){
			strcpy(tempArg->type, type);
			if (isFirst == 1){
				isFirst = 0;
			}
			else{
				wt(", ");
			}
			
			writeBasedOnType(tempArg->name, type);
			
		}
		else{
			isFirst = 0;
		}
			
		tempArg = tempArg->nextArg;
	}
}


void printClasses(){
	
	class_node *currentClass;
	function_node *currentFunction;
	arg_node *currentArg;
	var_node *currentVar;
	
	currentClass = (void *) firstClass;
	
	while (currentClass != NULL){
		printf("%s\n", currentClass->name);
		printf("=========================================\n");
		
		printf("extends: ");
		
		if (currentClass->extendClass == NULL){
			printf("NULL\n");
		}
		else{
			printf("%s\n", currentClass->extendClass->name);
		}
		
		
		if (currentClass->varList == NULL){
				printf("Class Vars()\n\n");
		}
		else{
				
			printf("Class Vars(");
				
			currentVar = currentClass->varList->firstVar;
				
			while (currentVar != NULL){
					
				printf("%s:%s ", currentVar->name, currentVar->type);
				currentVar = currentVar->nextVar;
			}
				
			printf(") \n");
			printf("---------------------------------------------\n");
				
				
		}
		
		printf("Functions:\n");
		
		if (currentClass->functionList != NULL){
			currentFunction = currentClass->functionList->firstFunction;
		}
		else {
			currentClass = currentClass->nextClass;
			printf("\n");
			continue;
		}
		
		while (currentFunction != NULL){

		
			printf("%s", currentFunction->name);
			
			if (currentFunction->argList == NULL){
				printf("() \n");
			}
			else{
				
				printf("(");
				
				currentArg = currentFunction->argList->firstArg;
				
				while (currentArg != NULL){
					
					printf("%s:%s ", currentArg->name, currentArg->type);
					currentArg = currentArg->nextArg;
				}
				
				printf(") \n\n");

			}
			
			if (currentFunction->varList == NULL){
				printf("Vars()\n\n");
			}
			else{
				
				printf("Vars(");
				
				currentVar = currentFunction->varList->firstVar;
				
				while (currentVar != NULL){
					
					printf("%s:%s ", currentVar->name, currentVar->type);
					currentVar = currentVar->nextVar;
				}
				
				printf(") \n");
				printf("---------------------------------------------\n");
			}		
			currentFunction = currentFunction->nextFunction;
		}
		
		printf("\n");
		currentClass = currentClass->nextClass;
	}
}
