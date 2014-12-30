#include <unistd.h>
#include <sys/syscall.h>
#include <sys/mman.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>

struct cs1550_sem{
	int value;
	struct Node *head, *tail;
};

// setup all the required semaphores
struct cs1550_sem *empty;
struct cs1550_sem *full;
struct cs1550_sem *mutex;

// the function down and up are used for the two newly added syscalls
void down(struct cs1550_sem *sem){
	syscall(__NR_cs1550_down, sem);
}

void up(struct cs1550_sem *sem){
	syscall(__NR_cs1550_up, sem);
}

// start of the main program which will contain the producers and consumers
int main(int argc, char *argv[]){
	
	// this is used to wait for child processes to finish
	int status;
	// fetch the required information, which are number of producers, number of consumers, and max buffer size
	if(argc != 4){
		printf("Invalid input, please enter the number of producers, number of consumers, and max buffer size respectively separated by space.\nSystem shutting down...\n");
		exit(1);
	}
	
	// get the number of producers, consumers, and max buffer size respectively from command line input
	int prod = atoi(argv[1]);
	int cons = atoi(argv[2]);
	int mBuf = atoi(argv[3]);
	
	// allocate memory for the buffer and semaphores using mmap and assign them, make sure we allocate enough memory for all of the data we want to store, so the buffer's size is (mBuf+3) multiplied by size of int and memory for semaphore is 3*size of the semaphore structs
	void *buf_ptr =  mmap(NULL, (mBuf+3)*sizeof(int), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
	void *sem_ptr =  mmap(NULL, 3*sizeof(struct cs1550_sem *), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
	
	// store the 3 semaphores in our memory allocated for them
	empty = (struct cs1550_sem *)sem_ptr;
	full = empty + 1;
	mutex = full + 1;
	
	// store the producer and consumer in the buffer
	int *in, *out;
	in = (int *)buf_ptr;
	out = in+1;
	*in = 0;
	*out = 0;
	
	// initialize the semaphores' initial values, head node, and tail node
	empty->value = mBuf;
	empty->head = NULL;
	empty->tail = NULL;
	
	full->value = 0;
	full->head = NULL;
	full->tail = NULL;
	
	mutex->value = 1;
	mutex->head = NULL;
	mutex->tail = NULL;
	
	// runs producer
	int i;
	for(i = 0; i < prod; i++){
		if(fork()==0){
			int pitem;
			while(1){
				// produce an item add to the buffer
				down(empty);
				down(mutex);
				
				pitem = *in;
				*in = (*in+1)%mBuf;
				printf("Producer %c produces %d\n", 65+i, pitem);
				
				up(mutex);
				up(full);
				
			}
		}
	}
	
	// run the consumer
	int k;
	for(k = 0; k < cons; k++){
		if(fork()==0){
			int citem;
			while(1){
				
				// consume an item
				down(full);
				down(mutex);
				
				citem = *out;
				*out = (*out+1)%mBuf;
				printf("Consumer %c consumes %d\n", 65+k, citem);
				
				up(mutex);
				up(empty);
				
			}
		}
	}
	
	// wait for child process to finish
	wait(&status);
	return 0;
	
}