/* To compile:
 * gcc -o square library.c square.c
 * ./square.c
 */

#include <sys/select.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <termios.h>
#include <linux/fb.h>

#define filedir "/dev/fb0"
typedef unsigned short color_t;

int fd;
color_t *map;
struct fb_var_screeninfo vRes;
struct fb_fix_screeninfo bDepth;
int mapSize;
struct termios currTerm, savedTerm;

void clear_screen(){
	// Since we cannot use C library, no printf?
	write(1, "\033[2J", 7);
	
}

void init_graphics(){
	
	// Open the framebuffer for writting
	fd = open(filedir, O_RDWR);
	
	// Grab the necessary information about the screen: resolution and bit-depth
	ioctl(fd, FBIOGET_VSCREENINFO, &vRes);
	ioctl(fd, FBIOGET_FSCREENINFO, &bDepth);
	mapSize = vRes.yres_virtual * bDepth.line_length;
	
	// Use mmap() to get an address that represent the content of the file
	map = mmap(NULL, mapSize, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
	
	// Use IOCTL to disable keypress/echo
	ioctl(1, TCGETS, &currTerm);
	savedTerm = currTerm;
	currTerm.c_lflag &= ~ECHO;
	currTerm.c_lflag &= ~ICANON;
	ioctl(1, TCSETS, &currTerm);

	clear_screen();
}

void exit_graphics(){
	
	clear_screen();
	// Use ioctl() to reenable key press echo and buffering
	ioctl(1, TCSETS, &savedTerm);
	
	// Use munmap() to clean up
	munmap(map, mapSize);
	
	// Closing the file descriptor
	close(fd);
	
}

char getkey(){

	char keyPressed;
	fd_set rfds;
	struct timeval tv;
	int retval;

	FD_ZERO(&rfds);
	FD_SET(0, &rfds);

	tv.tv_sec = 0;
	tv.tv_usec = 0;

	retval = select(1, &rfds, NULL, NULL, &tv);

	if(retval){
		ssize_t keyRead = read(0, &keyPressed, 1);
	}

	return keyPressed;
	
}

void sleep_ms(long ms){
	if(ms > 1000){
		ms = 20;
	}
	ms = ms*1000000;
	
	struct timespec ts;
	ts.tv_sec = 0;
	ts.tv_nsec = ms;
	
	// nanosleep((struct timespec[]){{0,ms}}, NULL);
	nanosleep(&ts, NULL);
	
}

void draw_pixel(int x, int y, color_t color){
	
	if(x < 0)
		x = 640+x;
	if(y < 0)
		y = y+480;
	
	if(x<640 && y<480){
		map[y*640 + x] = color;
	}
	
}

void draw_rect(int x1, int y1, int width, int height, color_t c){
	
	int i, k;

	for(i = x1; i < x1+width; i++){

		for(k = y1; k < y1+height; k++){

			draw_pixel(i, k, c);

		}

	}
	
}