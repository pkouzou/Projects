#include <stdio.h>
#include <stdlib.h>
#include "output_file.h"


void miniCalc1_addOne(struct miniCalc1 *self, int input){
int temp;
 self->result = input + 1;
printf("%d\n", self->result);

}

void miniCalc2_findMax(struct miniCalc2 *self, int input[5]){
int tempMax;
int index;
 tempMax = input [ 0];
 index = 1;
while( index  < 5){
if( input [ index ]> tempMax ){
 tempMax = input [ index ];
}
else {
 tempMax = tempMax ;
}
;
 index = index + 1;
;
}
;
printf("%d\n", tempMax);

}

void miniCalc2_testFunction(struct miniCalc2 *self, float v1, float v2){
int v3;
int v4;
 v3 = 5* v1 ;
 v4 = 2.500000* v2 + 5% 2;
if( v2 > v1  && v1  == 5){
printf("%f\n", v1);
}
else {
printf("%f\n", v2);
}
;
printf("%d\n", v3);
printf("%d\n", v4);
if( 5+ 5> 1 ||! 5 < 0){
 v4 = v4 / v3 ;
printf("%d\n", v4);
;
}
else {
 return;
}
;

}

void fullCalc_add(struct fullCalc *self, int input){
int temp;
miniCalc1_addOne(self->extClass, input );

}

void fullCalc_constructor(struct fullCalc *self, int temp){
self->extClass = malloc(sizeof(miniCalc1));
float temp2;
;

}

void main(){
struct fullCalc *calc;
struct miniCalc2 *calc2;
int arr[5];
float k;
 arr [ 0]= 5;
 arr [ 1]= 6;
 arr [ 2]= 8;
 arr [ 3]= 5;
 arr [ 4]=- 2;
fullCalc_constructor(calc, 5);
fullCalc_add(calc, 5);
miniCalc2_findMax(calc2, arr );
miniCalc2_testFunction(calc2, 1,  4);

}