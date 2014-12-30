#include "mymalloc.h"
#include <unistd.h>
#include <stdio.h>
// 0 is free, 1 is not free

struct Node *head = NULL;
struct Node *tail = NULL;
struct Node *curr;
struct Node *start;

int freeCall = 0;

void *my_nextfit_malloc(int x){
	
	struct Node *newNode;
	// If this is the first time calling malloc, simply create an empty array of size x plus a node in front
	if(head == NULL){
		
		newNode = sbrk(sizeof(struct Node));
		newNode->ptr = sbrk(x);
		newNode -> isFree = 1;
		newNode -> prev = NULL;
		newNode -> next = NULL;
		newNode -> size = x;
		head = newNode;
		tail = head;
		start = head;
		
		return newNode->ptr;
		
	}
	
	else if(head->next == NULL){
		
		newNode = sbrk(sizeof(struct Node));
		newNode->ptr = sbrk(x);
		newNode -> prev = head;
		newNode -> next = NULL;
		newNode -> isFree = 1;
		newNode -> size = x;
		head -> next = newNode;
		tail = newNode;
		
		return newNode->ptr ;
		
	}
	
	else{
		
		if(freeCall == 0){

			newNode = sbrk(sizeof(struct Node));
			newNode -> ptr = sbrk(x);
			newNode -> prev = tail;
			newNode -> next = NULL;
			newNode -> isFree = 1;
			newNode -> size = x;
			tail -> next = newNode;
			tail = newNode;
			
			return newNode->ptr;

		}

		else{

			curr = start;
			while(curr != NULL){
				
				// if a free node of fit size is found
				if(curr->isFree == 0 && curr->size >= x){

					curr -> isFree = 1;
					start = curr;
					return 1;

				}

				// if no free node of fit size is found
				else if(curr->isFree == 1 && curr->next == NULL){
					
					newNode = sbrk(sizeof(struct Node));
					newNode -> ptr = sbrk(x);
					newNode -> prev = tail;
					newNode -> next = NULL;
					newNode -> isFree = 1;
					newNode -> size = x;
					tail -> next = newNode;
					tail = newNode;
					start = curr;

					return newNode->ptr;
					
				}

				else{
					curr = curr->next;
				}

			}

		}

	}
	
}

void my_free(void *ptr){
	
	
	struct Node *toRelease = (struct Node *)(ptr)-1;
	struct Node *tempNode = head;
	
	// if ptr is at the last allocated brk, erase the node
	if(toRelease == tail){
		
		if(tail->prev->isFree == 0 && tail->prev->prev != NULL){
			tail = tail->prev->prev;
			tail->next = NULL;
			tail = sbrk(-(sizeof(struct Node) + tail->size));
		}
		
		else{
			tail = toRelease->prev;
			tail->next = NULL;
			tail = sbrk(-(sizeof(struct Node) + tail->prev->size));
		}
		
	}
	
	if(toRelease == head){
		
		if(head->next == NULL){
			head = sbrk(-(sizeof(struct Node) + head->size));
			head = NULL;
		}
		
		if(head->next != NULL){
			head->isFree = 0;
			if(head->next->isFree == 0){
				
				head->size = (head->size) + (head->next->size);
				head->next = head->next->next;
				head->next->prev = head;
				
			}
		}
		
	}
	
	else{
		while(tempNode != NULL && tempNode->next != NULL){
			
			if(toRelease == tempNode && tempNode->next != NULL){
				// if ptr is in the middle, change the status of isFree of curr
				if(tempNode->prev->isFree != 0 && tempNode->next->isFree != 0 || tempNode->prev == NULL){
					tempNode->isFree = 0;
				}
				
				// if prt is right next to a free segment, merge the two together
				if(tempNode->next->isFree == 0){
					tempNode->isFree = 0;
					tempNode->size = (tempNode->size) + (tempNode->next->size);
					tempNode->next = tempNode->next->next;
					tempNode->next->prev = tempNode;
				}
				if(tempNode->prev->isFree == 0){
					tempNode->isFree = 0;
					tempNode->prev->size = (tempNode->size) + (tempNode->prev->size);
					tempNode = tempNode->prev;
					tempNode->next = tempNode->next->next;
				}
				
				if(tempNode->prev == head && head->isFree == 0){
					head->size = (head->size) + (head->next->size);
					head->next = head->next->next;
					head->next->prev = head;
				}
				
			}
			
			tempNode = tempNode->next;
		}
	}

	freeCall = 1;
	
}

void printlist(){
	
	struct Node *temp = head;
	
	while(temp != NULL){
		printf("|%d|%d| -> ", temp->size, temp->isFree);
		temp = temp->next;
	}

	if(temp == head && head == NULL){

		printf("Memory is free.");

	}

	printf("\n");
	
}
