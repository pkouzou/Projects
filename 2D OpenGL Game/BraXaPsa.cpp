#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <GL/gl.h>     // openGL header
#include <GL/glu.h>   // glut header
#include <GL/glut.h>   // glut header
#pragma warning (disable : 4996)
#define ROWS 12
#define COLUMNS 15
#define disx 2.0/COLUMNS //block width
#define disy 1.8/ROWS  //block height
#define dx -1.0 + j*2.0 / COLUMNS
#define dy 1.0 - i*1.8 / ROWS
#define ROCK_ID 0
#define PAPER_ID 1
#define SCISSORS_ID 2
#define BOMB_ID 3
#define BLOCKS 6
#define MAX_BOMBS 10

//functions' prototypes
void init(); //inits values and board
void readPGM(char *name, int id, float *arr); //reads PGM and inits textures
void reshape(GLint width, GLint height); //when window is resized object dont lose their properties
void normal_keys(unsigned char key, int x, int y);
void drawText(const char *text, int length, float x, float y); //draws given text
int toDestroy(char *type1, char *type2, int distance); //is used to find blocks that gonna be destroyed based on distance
int destroys(char *block1, char *block2); //checks if blocks1 destroys block2
int checkVertically(int x, int y); //checks Vertically if a triad exists
int checkHorizontal(int x, int y); //checks Horizontal if a triad exists
void find_distanceV(int top, int y, int distance); //finds distance for a vertical triad
void find_distanceH(int left, int x, int distance); //finds distance for a horizontal triad
void findSame(); //finds all the triads on the board
void refreshBoard(); //blocks fall and board is filled with new blocks
int findNeighbour(int i, int j); //find which neighbours given block can destroy
void mouse(int button, int state, int x, int y); //mouse control
void display();


// Global variables
char title[] = "BraXaPsa II";  // Windowed mode's title
int windowWidth = 15 * 40;     // Windowed mode's width
int windowHeight = 12 * 40 + 40;     // Windowed mode's height
int windowPosX = 100;      // Windowed mode's top-left corner x
int windowPosY = 100;      // Windowed mode's top-left corner y
int game_started = 0, wait_to_destroy = 0, player_move = 0; //player_move = 1 when he swaps 2 blocks
int Score = 0, moves = 0, game_end = 0, gives_input = 0, num_of_bombs = 0, clicks = 0; //when give_input = 1 means user gives_inputint 
int tx0, ty0, tx1, ty1; //coordinates of selected cells
char *block_types[] = { "paper", "rock", "scissors", "red", "blue", "bomb" };
char *game_over = "Game Over", *restart = "press b to restart or esc to exit", *score = "Score: ", *give_input = "Give number of moves (max 9 digits): ", *moves_left = "Moves Left: "; //messages
char *confirm_delete = "Press z to confirm and x to clear current input", *final_score = "Final Score: ";
float *rock, *paper, *scissors, *bomb; //textures


typedef struct cell
{
	GLfloat x, y;
	char *type;
	int isSelected;
	int isEmpty;
}cellt;

cellt board[ROWS][COLUMNS];

void init()
{
	game_started = 1;
	gives_input = 1;
	game_end = 0;
	Score = 0;
	num_of_bombs = 0;
	wait_to_destroy = 0;
	player_move = 0;
	//initialize quads' coordinates
	for (int i = 0; i < ROWS; i++){
		for (int j = 0; j < COLUMNS; j++){
			board[i][j].x = dx;
			board[i][j].y = dy;
			if (num_of_bombs < MAX_BOMBS){
				board[i][j].type = block_types[rand() % BLOCKS];
				if (strcmp(board[i][j].type, "bomb") == 0) num_of_bombs += 1;
			}
			else board[i][j].type = block_types[rand() % (BLOCKS - 1)];

			board[i][j].isSelected = 0;
			board[i][j].isEmpty = 0;
		}
	}
	readPGM("rock.pgm", ROCK_ID, rock);
	readPGM("paper.pgm", PAPER_ID, paper);
	readPGM("scissors.pgm", SCISSORS_ID, scissors);
	readPGM("bomb.pgm", BOMB_ID, bomb);
}

void readPGM(char *name, int id, float *arr){
	FILE *file = fopen(name, "r");
	if (file == NULL) exit(0);
	char *text;
	int number_of_line = 1, c = 0, width = 0, height = 0, counter = 0;
	float max_value = 0.0;
	size_t pos = 0;
	text = (char *)malloc(9 * sizeof(char));

	while (number_of_line <= 4){
		if (number_of_line == 1 || number_of_line == 2){
			c = fgetc(file);
			if ((char)c == '\n') number_of_line += 1;
		}
		else if (number_of_line == 3){ //reads width and height
			c = fgetc(file);
			if ((char)c == ' '){
				width = atoi(text);
				pos = 0;
			}
			else if ((char)c == '\n'){
				height = atoi(text);
				pos = 0;
				number_of_line += 1;
			}
			else text[pos++] = (char)c;
		}
		else{
			c = fgetc(file);
			if ((char)c == '\n'){
				max_value = float(atoi(text));
				pos = 0;
				number_of_line += 1;
			}
			else text[pos++] = (char)c;
		}
	}

	arr = (float *)malloc(width*height * 3 * sizeof(float));
	char buf[256];

	while (fscanf(file, "%s", buf) != EOF){
		arr[counter] = atoi(buf) / max_value;
		arr[counter + 1] = atoi(buf) / max_value;
		arr[counter + 2] = atoi(buf) / max_value;
		counter += 3;
	}

	glBindTexture(GL_TEXTURE_2D, id);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_FLOAT, arr);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

	fclose(file);
}

void reshape(GLint width, GLint height)
{
	windowWidth = width;
	windowHeight = height;
	glViewport(0, 0, windowWidth, windowHeight);

}

/* Callback handler for normal-key event */
void normal_keys(unsigned char key, int x, int y) {

	if (key == 27){ //ESC
		exit(0);
	}
	else if (key == 'b' && !game_started) { // If they ‘b’ key was pressed  
		init();							//initialize our variables
	}
	else if (key == 'z' && gives_input && moves > 0){
		gives_input = 0;
	}
	else if (key == 'x' && gives_input){
		moves = 0;
	}
	//this is for moves' input
	else if (key == '1' && gives_input){
		moves = moves * 10 + 1;
	}
	else if (key == '2' && gives_input){
		moves = moves * 10 + 2;
	}
	else if (key == '3' && gives_input){
		moves = moves * 10 + 3;
	}
	else if (key == '4' && gives_input){
		moves = moves * 10 + 4;
	}
	else if (key == '5' && gives_input){
		moves = moves * 10 + 5;
	}
	else if (key == '6' && gives_input){
		moves = moves * 10 + 6;
	}
	else if (key == '7' && gives_input){
		moves = moves * 10 + 7;
	}
	else if (key == '8' && gives_input){
		moves = moves * 10 + 8;
	}
	else if (key == '9' && gives_input){
		moves = moves * 10 + 9;
	}
	else if (key == '0' && gives_input){
		moves = moves * 10;
	}
}

void drawText(const char *text, int length, float x, float y){
	glMatrixMode(GL_PROJECTION);
	double *matrix = new double[16];
	glGetDoublev(GL_PROJECTION_MATRIX, matrix);
	glLoadIdentity();
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	glPushMatrix();
	glLoadIdentity();
	glRasterPos2f(x, y);
	
	for (int i = 0; i < length; i++){
		glutBitmapCharacter(GLUT_BITMAP_9_BY_15, (int)text[i]);
	}
	glPopMatrix();
	glMatrixMode(GL_PROJECTION);
	glLoadMatrixd(matrix);
	glMatrixMode(GL_MODELVIEW);
}

int toDestroy(char *type1, char *type2, int distance){ //this function used for distances

	if (distance == 1){
		if ((strcmp(type1, "red") == 0 || strcmp(type1, "blue") == 0) && strcmp(type2, "bomb") == 0){
			Score -= 30;
			num_of_bombs -= 1;
			return 1;
		}
		if ((strcmp(type1, "red") == 0 || strcmp(type1, "blue") == 0)) return 0;

		if (strcmp(type2, "red") == 0 || strcmp(type2, "blue") == 0) {
			Score += 2;
			return 1;
		}
		else if (strcmp(type2, "bomb") == 0){
			Score -= 30;
			num_of_bombs -= 1; //one bomb destroyed
			return 1;
		}
	}

	if (strcmp(type1, "red") == 0 || strcmp(type1, "blue") == 0) return 0;

	int destroyed = 0;

	if (strcmp(type1, "rock") == 0){
		if (strcmp(type2, "scissors") == 0) destroyed = 1;
	}
	else if (strcmp(type1, "paper") == 0){
		if (strcmp(type2, "rock") == 0) destroyed = 1;
	}
	else{
		if (strcmp(type2, "paper") == 0) destroyed = 1;
	}

	if (destroyed){
		if (distance == 1) Score += 2;
		else Score += 3;
	}
	else if (distance == 1 && player_move) Score -= 1; //block didnt destroyed

	return destroyed;
}

int destroys(char *block1, char *block2){//this fucntion is used when blocks fall

	int destroys = 0;
	if (strcmp(block1, "rock") == 0){
		if (strcmp(block2, "scissors") == 0) destroys = 1;
	}
	else if (strcmp(block1, "paper") == 0){
		if (strcmp(block2, "rock") == 0) destroys = 1;
	}
	else if (strcmp(block1, "scissors") == 0){
		if (strcmp(block2, "paper") == 0) destroys = 1;
	}

	if (destroys) Score += 2;

	return destroys;
}

int checkVertically(int x, int y){

	if (board[x][y].isEmpty) return 0;
	if (strcmp(board[x][y].type, "bomb") == 0) return 0;
	if (x - 1 >= 0 && x + 1 <= ROWS - 1){
		if (board[x - 1][y].isEmpty || board[x + 1][y].isEmpty) return 0;
		if (strcmp(board[x][y].type, board[x - 1][y].type) == 0 && strcmp(board[x][y].type, board[x + 1][y].type) == 0){
			if (player_move){
				find_distanceV(x - 1, y, 1);
				find_distanceV(x - 1, y, 2);
				find_distanceV(x - 1, y, 3);
			}
			board[x][y].isEmpty = 1;
			board[x - 1][y].isEmpty = 1;
			board[x + 1][y].isEmpty = 1;
			return 1;
		}
	}
	if (x + 1 <= 11 && x + 2 <= ROWS - 1){
		if (board[x + 1][y].isEmpty || board[x + 2][y].isEmpty) return 0;
		if (strcmp(board[x][y].type, board[x + 1][y].type) == 0 && strcmp(board[x][y].type, board[x + 2][y].type) == 0){
			if (player_move){
				find_distanceV(x, y, 1);
				find_distanceV(x, y, 2);
				find_distanceV(x, y, 3);
			}
			board[x][y].isEmpty = 1;
			board[x + 1][y].isEmpty = 1;
			board[x + 2][y].isEmpty = 1;
			return 1;
		}
	}
	if (x - 1 >= 0 && x - 2 >= 0){
		if (board[x - 1][y].isEmpty || board[x - 2][y].isEmpty) return 0;
		if (strcmp(board[x][y].type, board[x - 1][y].type) == 0 && strcmp(board[x][y].type, board[x - 2][y].type) == 0){
			if (player_move){
				find_distanceV(x - 2, y, 1);
				find_distanceV(x - 2, y, 2);
				find_distanceV(x - 2, y, 3);
			}
			board[x][y].isEmpty = 1;
			board[x - 1][y].isEmpty = 1;
			board[x - 2][y].isEmpty = 1;
			return 1;
		}
	}

	return 0;
}

int checkHorizontal(int x, int y){

	if (strcmp(board[x][y].type, "bomb") == 0) return 0;
	if (y - 1 >= 0 && y + 1 <= COLUMNS - 1){//OxO
		if (board[x][y - 1].isEmpty || board[x][y + 1].isEmpty) return 0;
		if (strcmp(board[x][y].type, board[x][y - 1].type) == 0 && strcmp(board[x][y].type, board[x][y + 1].type) == 0){
			if (player_move){
				find_distanceH(y - 1, x, 1);
				find_distanceH(y - 1, x, 2);
				find_distanceH(y - 1, x, 3);
			}
			board[x][y].isEmpty = 1;
			board[x][y - 1].isEmpty = 1;
			board[x][y + 1].isEmpty = 1;
			return 1;
		}
	}
	if (y + 1 <= 14 && y + 2 <= COLUMNS - 1){//xOO
		if (board[x][y + 1].isEmpty || board[x][y + 2].isEmpty) return 0;
		if (strcmp(board[x][y].type, board[x][y + 1].type) == 0 && strcmp(board[x][y].type, board[x][y + 2].type) == 0){
			if (player_move){
				find_distanceH(y, x, 1);
				find_distanceH(y, x, 2);
				find_distanceH(y, x, 3);
			}
			board[x][y].isEmpty = 1;
			board[x][y + 1].isEmpty = 1;
			board[x][y + 2].isEmpty = 1;
			return 1;
		}
	}
	if (y - 1 >= 0 && y - 2 >= 0){//00x
		if (board[x][y - 1].isEmpty || board[x][y - 2].isEmpty) return 0;
		if (strcmp(board[x][y].type, board[x][y - 1].type) == 0 && strcmp(board[x][y].type, board[x][y - 2].type) == 0){
			if (player_move){
				find_distanceH(y - 2, x, 1);
				find_distanceH(y - 2, x, 2);
				find_distanceH(y - 2, x, 3);
			}
			board[x][y].isEmpty = 1;
			board[x][y - 1].isEmpty = 1;
			board[x][y - 2].isEmpty = 1;
			return 1;
		}
	}

	return 0;
}

void find_distanceV(int top, int y, int distance){//vertically

	//top
	if (top - distance >= 0){
		for (int i = y - distance; i <= y + distance; i++){
			if (i >= 0 && i <= COLUMNS - 1){
				if (board[top - distance][i].isEmpty) continue;
				if (toDestroy(board[top][y].type, board[top - distance][i].type, distance)){
					board[top - distance][i].isEmpty = 1;
				}
			}
		}
	}
	//bottom
	if (top + distance + 2 <= ROWS - 1){
		for (int i = y - distance; i <= y + distance; i++){
			if (i >= 0 && i <= COLUMNS - 1){
				if (board[top + distance + 2][i].isEmpty) continue;
				if (toDestroy(board[top][y].type, board[top + distance + 2][i].type, distance)){
					board[top + distance + 2][i].isEmpty = 1;
				}
			}
		}
	}
	//left
	if (y - distance >= 0){
		for (int i = top - distance + 1; i <= top + 2 + distance; i++){
			if (i >= 0 && i <= ROWS - 1){
				if (board[i][y - distance].isEmpty) continue;
				if (toDestroy(board[top][y].type, board[i][y - distance].type, distance)){
					board[i][y - distance].isEmpty = 1;
				}
			}
		}
	}
	//right
	if (y + distance <= COLUMNS - 1){

		for (int i = top - distance + 1; i <= top + 2 + distance; i++){
			if (i >= 0 && i <= ROWS - 1){
				if (board[i][y + distance].isEmpty) continue;
				if (toDestroy(board[top][y].type, board[i][y + distance].type, distance)){
					board[i][y + distance].isEmpty = 1;
				}
			}
		}
	}
}

void find_distanceH(int left, int x, int distance){

	//left
	if (left - distance >= 0){
		for (int i = x - distance; i <= x + distance; i++){
			if (i >= 0 && i <= ROWS - 1){
				if (board[i][left - distance].isEmpty) continue;
				if (toDestroy(board[x][left].type, board[i][left - distance].type, distance)){
					board[i][left - distance].isEmpty = 1;
				}
			}
		}
	}
	//bottom
	if (left + 2 + distance <= COLUMNS - 1){
		for (int i = x - distance; i <= x + distance; i++){
			if (i >= 0 && i <= ROWS - 1){
				if (board[i][left + 2 + distance].isEmpty) continue;
				if (toDestroy(board[x][left].type, board[i][left + 2 + distance].type, distance)){
					board[i][left + 2 + distance].isEmpty = 1;
				}
			}
		}
	}
	//top
	if (x - distance >= 0){
		for (int i = left - distance + 1; i <= left + 1 + distance; i++){
			if (i >= 0 && i <= COLUMNS - 1){
				if (board[x - distance][i].isEmpty) continue;
				if (toDestroy(board[x][left].type, board[x - distance][i].type, distance)){
					board[x - distance][i].isEmpty = 1;
				}
			}
		}
	}
	//bottom
	if (x + distance <= ROWS - 1){
		for (int i = left - distance + 1; i <= left + 1 + distance; i++){
			if (i >= 0 && i <= COLUMNS - 1){
				if (board[x + distance][i].isEmpty) continue;
				if (toDestroy(board[x][left].type, board[x + distance][i].type, distance)){
					board[x + distance][i].isEmpty = 1;
				}
			}
		}
	}
}

void findSame(){

	for (int j = 0; j <= COLUMNS - 1; j++){
		for (int i = 0; i <= ROWS - 1; i++){
			if (board[i][j].isEmpty) continue;
			if (checkVertically(i, j)) {
				Score += 10;
				wait_to_destroy = 1;
			}

			else if (checkHorizontal(i, j)){
				Score += 10;
				wait_to_destroy = 1;
			}
		}
	}

}

void refreshBoard(){

	int box_to_refresh, no_empty;
	char *ttype;
	int moved[COLUMNS]; //to find in which colomns block are moved

	for (int i = 0; i < COLUMNS; i++) moved[i] = -1;
	for (int j = 0; j <= COLUMNS - 1; j++){

		box_to_refresh = 0;
		no_empty = 0;
		for (int i = ROWS - 1; i >= 0; i--){
			if (board[i][j].isEmpty){
				if (moved[i] == -1) moved[j] = i; //finds first empty
				box_to_refresh += 1;
			}
			else{
				board[ROWS - 1 - no_empty][j].type = board[i][j].type;
				board[ROWS - 1 - no_empty][j].isEmpty = 0;
				no_empty += 1;
			}
		}
		if (box_to_refresh == 0) continue;
		for (int i = 0; i < box_to_refresh; i++){
			if (num_of_bombs < MAX_BOMBS){
				board[i][j].type = block_types[rand() % BLOCKS];
				if (strcmp(board[i][j].type, "bomb") == 0) num_of_bombs += 1;

			}
			else board[i][j].type = block_types[rand() % (BLOCKS - 1)];

			board[i][j].isEmpty = 0;
		}
	}
	//find the neighbours of the blocks that moved
	for (int j = 0; j < COLUMNS; j++){
		if (moved[j] == -1) continue;

		for (int i = moved[j]; i >= 0; i--){
			if (checkVertically(i, j)) {
				Score += 10;
				wait_to_destroy = 1;
			}
			else if (checkHorizontal(i, j)) {
				Score += 10;
				wait_to_destroy = 1;
			}
			else if (findNeighbour(i, j)) wait_to_destroy = 1;
		}
	}
}

int findNeighbour(int i, int j){

	int found = 0;
	//left
	if (j - 1 >= 0){
		if (!board[i][j - 1].isEmpty && destroys(board[i][j].type, board[i][j - 1].type)) {
			board[i][j - 1].isEmpty = 1;
			found = 1;
			Score += 2;
		}
	}
	//right
	if (j + 1 <= COLUMNS - 1){
		if (!board[i][j + 1].isEmpty && destroys(board[i][j].type, board[i][j + 1].type)) {
			board[i][j + 1].isEmpty = 1;
			found = 1;
			Score += 2;
		}
	}
	//top
	if (i - 1 >= 0){
		if (!board[i - 1][j].isEmpty && destroys(board[i][j].type, board[i - 1][j].type)) {
			board[i - 1][j].isEmpty = 1;
			found = 1;
			Score += 2;
		}
	}
	//bottom
	if (i + 1 <= ROWS - 1){
		if (!board[i + 1][j].isEmpty && destroys(board[i][j].type, board[i + 1][j].type)) {
			board[i + 1][j].isEmpty = 1;
			found = 1;
			Score += 2;
		}
	}
	return found;
}


/* Callback handler for mouse event */
void mouse(int button, int state, int x, int y) {

	if (!game_started || gives_input || wait_to_destroy) return;

	if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {

		float normalizedX = -1.0 + 2.0 * (float)x / windowWidth;
		float normalizedY = 1.0 - 2.0 * (float)y / windowHeight;

		if (clicks == 0){ //first click is to change "state"

			for (int i = 0; i < ROWS; i++){
				for (int j = 0; j < COLUMNS; j++){
					if (board[i][j].x <= normalizedX && normalizedX <= board[i][j].x + disx  && board[i][j].y >= normalizedY && normalizedY >= board[i][j].y - disy){
						tx0 = i;
						ty0 = j;
						clicks += 1;
						board[i][j].isSelected = 1;
						return;
					}
				}
			}
		}
		else if (clicks == 1){

			for (int i = 0; i < ROWS; i++){
				for (int j = 0; j < COLUMNS; j++){
					if (board[i][j].x <= normalizedX && normalizedX <= board[i][j].x + disx  && board[i][j].y >= normalizedY && normalizedY >= board[i][j].y - disy){
						tx1 = i;
						ty1 = j;
						if (tx0 != tx1 || ty0 != ty1){
							if ((ty0 == ty1 && abs(tx0 - tx1) == 1) || tx0 == tx1 && abs(ty0 - ty1) == 1){

								board[tx0][ty0].isSelected = 0;
								char *ttype = board[tx0][ty0].type;
								board[tx0][ty0].type = board[tx1][ty1].type;
								board[tx1][ty1].type = ttype;
								clicks = 0;
								moves -= 1;
								player_move = 1;
								if (checkVertically(tx0, ty0)) Score += 10;
								else {
									if (checkHorizontal(tx0, ty0)) Score += 10;
								}
								if (checkVertically(tx1, ty1)) Score += 10;
								else {
									if (checkHorizontal(tx1, ty1)) Score += 10;
								}
								//check if game over
								if (moves == 0){
									game_end = 1;
									game_started = 0;
								}
								return;
							}
						}
					}
				}
			}
			clicks = 0;
			board[tx0][ty0].isSelected = 0;
		}
	}
}

void display()
{
	glClear(GL_COLOR_BUFFER_BIT);	//Clear the screen
	glMatrixMode(GL_MODELVIEW);    // To operate on the model-view matrix
	glLoadIdentity();              // Reset model-view matrix


	if (game_started || game_end){
		glColor3f(0, 0, 0);
		if (gives_input){

			if (moves / 1000000000.0 > 1){
				moves = 0;
			}
			//draws input data
			char s[11], temp[40]; //converts moves_input to string
			itoa(moves, s, 10);
			strcpy(temp, give_input);
			strcat(temp, s);
			drawText(temp, strlen(temp), -0.99, -0.85);
			drawText(confirm_delete, strlen(confirm_delete), -0.99, -0.92);
			glEnd();
		}
		else{
			if (!game_end) {

				findSame();
				if (player_move) player_move = 0;

				if (Score / 1000000000.0 > 1){
					Score = 0;
				}
				//draws score
				char s[11], m[11]; //converts Score, moves to string
				itoa(Score, s, 10);
				itoa(moves, m, 10);
				char temp[40];
				strcpy(temp, score);
				strcat(temp, s);
				drawText(temp, strlen(temp), -0.99, -0.85);
				strcpy(temp, moves_left);
				strcat(temp, m);
				drawText(temp, strlen(temp), -0.99, -0.92);
				glEnd();
			}
			
		}

		//Start drawing quads
		for (int i = 0; i < ROWS; i++){
			for (int j = 0; j < COLUMNS; j++){

				if (strcmp(board[i][j].type, "red") == 0 || strcmp(board[i][j].type, "blue") == 0){


					float alpha = 1.0;
					if (board[i][j].isEmpty || board[i][j].isSelected) alpha = 0.5;
					if (strcmp(board[i][j].type, "red") == 0) glColor4f(1.0f, 0.0f, 0.0f, alpha);
					else glColor4f(0.0f, 0.0f, 1.0f, alpha);

					glEnable(GL_BLEND);
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

					glBegin(GL_QUADS);
					glVertex2f(board[i][j].x, board[i][j].y);
					glVertex2f(board[i][j].x, board[i][j].y - disy);
					glVertex2f(board[i][j].x + disx, board[i][j].y - disy);
					glVertex2f(board[i][j].x + disx, board[i][j].y);
					glEnd();

					glDisable(GL_BLEND);
				}
				else{

					if (strcmp(board[i][j].type, "rock") == 0 || strcmp(board[i][j].type, "paper") == 0 || strcmp(board[i][j].type, "scissors") == 0 || strcmp(board[i][j].type, "bomb") == 0){

						int id;
						if (strcmp(board[i][j].type, "rock") == 0) id = ROCK_ID;
						else if (strcmp(board[i][j].type, "paper") == 0) id = PAPER_ID;
						else if (strcmp(board[i][j].type, "scissors") == 0)  id = SCISSORS_ID;
						else id = BOMB_ID;

						glPushMatrix();
						glBindTexture(GL_TEXTURE_2D, id);
						glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
						glEnable(GL_TEXTURE_2D);

						glBegin(GL_QUADS);
						glTexCoord2f(0, 1);
						glVertex2f(board[i][j].x, board[i][j].y - disy);
						glTexCoord2f(1, 1);
						glVertex2f(board[i][j].x + disx, board[i][j].y - disy);
						glTexCoord2f(1, 0);
						glVertex2f(board[i][j].x + disx, board[i][j].y);
						glTexCoord2f(0, 0);
						glVertex2f(board[i][j].x, board[i][j].y);
						glEnd();
						glPopMatrix();
						glDisable(GL_TEXTURE_2D);
					}
				}

				if ((strcmp(board[i][j].type, "red") != 0 && strcmp(board[i][j].type, "blue") != 0) && (board[i][j].isSelected || board[i][j].isEmpty)){

					if (board[i][j].isSelected) glColor4f(0.0, 1.0, 1.0, 0.5);
					else if (board[i][j].isEmpty) glColor4f(1.0, 0.6, 0.0, 0.5);

					glEnable(GL_BLEND);
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

					glBegin(GL_QUADS);
					glVertex2f(board[i][j].x, board[i][j].y);
					glVertex2f(board[i][j].x, board[i][j].y - disy);
					glVertex2f(board[i][j].x + disx, board[i][j].y - disy);
					glVertex2f(board[i][j].x + disx, board[i][j].y);
					glEnd();

					glDisable(GL_BLEND);
				}
			}
		}
		glEnd();
	}

	glBegin(GL_LINES);
	glColor3f(0, 0, 0);				//Change the object colors to black
	for (int j = 1; j <= COLUMNS - 1; j++){
		glVertex2f(dx, -0.8f);
		glVertex2f(dx, 1.0f);
	}

	for (int i = 1; i <= ROWS; i++){
		glVertex2f(-1.0, dy);
		glVertex2f(1.0, dy);
	}

	glEnd();


	if (wait_to_destroy){
		Sleep(500);
		wait_to_destroy = 0;
	}

	refreshBoard();

	if (game_end && !wait_to_destroy){
		//draws score
		char s[5], temp[20]; //converts Score,to string
		itoa(Score, s, 10);
		strcpy(temp, final_score);
		strcat(temp, s);
		drawText(temp, strlen(temp), -0.99, -0.91);
		drawText(restart, strlen(restart), -0.99, -0.97);
		glColor3f(1, 0, 0);
		drawText(game_over, strlen(game_over), -0.99, -0.85);
		glEnd();
	}
	glutSwapBuffers();
	glutPostRedisplay();
}

int main(int argc, char ** argv)
{
	time_t t;
	srand((unsigned)time(&t));
	glutInit(&argc, argv);			//Initialize GLUT
	glutInitWindowSize(windowWidth, windowHeight);	//define the window size
	glutInitWindowPosition(windowPosX, windowPosX);	//Position the window
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);	//Define the drawing mode
	glutCreateWindow(title);	//Create our window
	glClearColor(1, 1, 1, 0);			//Define our background color
	glutDisplayFunc(display);		//tell Glut what our display function is
	glutReshapeFunc(reshape);
	glutKeyboardFunc(normal_keys);                  // Set the normal keyboard function
	glutMouseFunc(mouse);   // Register callback handler for mouse event
	glutMainLoop();					//Keep the program running
	return 0;
}