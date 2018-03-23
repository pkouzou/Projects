
typedef struct miniCalc1{
int result;
int id;
} miniCalc1;

typedef struct miniCalc2{
int result;
} miniCalc2;

typedef struct fullCalc{
struct miniCalc1 *extClass;
int result;
} fullCalc;

void miniCalc1_addOne(struct miniCalc1 *self, int input);

void miniCalc2_findMax(struct miniCalc2 *self, int input[5]);

void miniCalc2_testFunction(struct miniCalc2 *self, float v1, float v2);

void fullCalc_add(struct fullCalc *self, int input);

void fullCalc_constructor(struct fullCalc *self, int temp);
