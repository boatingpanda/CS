void *my_nextfit_malloc(int x);
void my_free(void *ptr);
void printlist();

struct Node{
	
	struct Node *prev;
	struct Node *next;
	int isFree;
	int size;
	void *ptr;
	
};
