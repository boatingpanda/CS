#include "mymalloc.h"
#include <stdio.h>

#define MALLOC my_nextfit_malloc
#define FREE my_free
extern struct Node *head;

int main(){

	printf("%d\n", sbrk(0));
	int * i = MALLOC(sizeof(int));
	
	char * c = MALLOC(sizeof(char));
	
	int * i2 = MALLOC(sizeof(int));
	
	char * a = MALLOC(sizeof(char));
	
	char * b = MALLOC(sizeof(char));
	
	
	printlist();
	
	FREE(c);
	
	printlist();
	
	FREE(a);
	
	printlist();
	
	FREE(i2);
	
	printlist();

	int * i3 = MALLOC(sizeof(int));

	printlist();

	int * i4 = MALLOC(sizeof(int));

	printlist();

	FREE(b);

	printlist();

	char * d = MALLOC(sizeof(char));

	printlist();
	
	return 0;
	
}