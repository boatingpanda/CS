typedef unsigned short color_t;

void clear_screen();
void exit_graphics();
void init_graphics();
char getkey();
void sleep_ms(long ms);

void draw_pixel(int x, int y, color_t color);
void draw_rect(int x1, int y1, int width, int height, color_t c);

int main(int argc, char** argv)
{
	int i = 65535;

	init_graphics();

	char key;
	int x = 0;
	int y = 0;
	
	int ux = 50;
	int uy = 20;
	int temp = 0;
	int dir = 0; // 0 is horizontal, 1 is vertical
	
	// initialize the board by randomly placing white dots of 20x20
	int count_x, count_y;
	int r = 31;
	
	for(count_y = 0; count_y < 480; count_y = count_y+40){
		for(count_x = 31; count_x < 640; count_x = count_x+r){
			
			r = (r*11+43)%100;
			if(r%2 == 1)
				draw_rect(count_x, count_y, 10, 10, 20);
			
		}
	}

	do
	{
		// first draw a black rectangle to cover up
		draw_rect(x, y, ux, uy, 0);
		key = getkey();
		if(key == 'w'){
			y-=10;
			if(dir != 1){
				dir = 1;
				temp = ux;
				ux = uy;
				uy = temp;
			}
		}
		else if(key == 's'){
			y+=10;
			if(dir != 1){
				dir = 1;
				temp = ux;
				ux = uy;
				uy = temp;
			}
		}
		else if(key == 'a'){
			x-=10;
			if(dir != 0){
				dir = 0;
				temp = ux;
				ux = uy;
				uy = temp;
			}
		}
		else if(key == 'd'){
			x+=10;
			if(dir != 0){
				dir = 0;
				temp = ux;
				ux = uy;
				uy = temp;
			}
		}
		//draw a rectangle based on user's directional input
		draw_rect(x, y, ux, uy, i);
		sleep_ms(20);
	} while(key != 'q');

	exit_graphics();

	return 0;

}
