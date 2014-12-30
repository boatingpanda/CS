#include <stdio.h>	// printf, scanf, etc
#include <sys/types.h>	// open()
#include <sys/stat.h>	// open()
#include <fcntl.h>	// open()
#include <unistd.h>	// read(), write(), and close()
#include <errno.h>	// EACCES

void sort(int * arr1, char * arr2){
	
	int i, k;
	
	for(i = 0; i < 5; i++){
		
		for(k = 0; k < 5; k++){
			
			if(arr1[k] > arr1[k + 1]){
				
				int temp1 = arr1[k];
				char temp2 = arr2[k];
				
				arr1[k] = arr1[k+1];
				arr1[k+1] = temp1;
				
				arr2[k] = arr2[k+1];
				arr2[k+1] = temp2;
				
			}
			if(arr1[k] == arr1[k + 1]){
				if(arr2[k] > arr2[k + 1]){
					char temp = arr2[k];
					arr2[k] = arr2[k+1];
					arr2[k+1] = temp;
				}
			}
			
		}
		
	}
		
}

// Check for the hand by checking a combination of these functions and whether their return value is 1 or 0
void compareHands(int * arr1, char * arr2){
	int onePair = isOnePair(arr1);
	int twoPair = isTwoPair(arr1);
	int threeKind = isThreeKind(arr1);
	int fourKind = isFourKind(arr1);
	int straight = isStraight(arr1);
	int flush = isFlush(arr2);
	int fullHouse = isFullHouse(arr1);
	int royalFlush = isRoyalFlush(arr1, arr2);
	int straightFlush = isStraightFlush(arr1, arr2);
	
	if(straightFlush == 1){
		printf("You have a straight flush\n");
	}
	else if(royalFlush == 1){
		printf("You have a royal flush\n");
	}
	else if(fullHouse == 1){
		printf("You have a full house\n");
	}
	else if(flush == 1){
		printf("You have a flush\n");
	}
	else if(straight == 1){
		printf("You have a straight\n");
	}
	else if(fourKind == 1){
		printf("You have four of a kind\n");
	}
	else if(threeKind == 1){
		printf("You have three of a kind\n");
	}
	else if(twoPair == 1){
		printf("You have two pair\n");
	}
	else if(onePair == 1){
		printf("You have one pair\n");
	}
	else{
		printf("You have high card\n");
	}
	
}

// if the hand is a one pair, return 1, else return 0
int isOnePair(int * arr1){
	int i;
	int pairs = 0;
	int count = 0;
	for(i = 0; i < 5; i++){
		if(arr1[i] == arr1[i+1]){
			count++;
		}
		if(arr1[i] != arr1[i+1]){
			if(count == 1){
				pairs++;
			}
			
			count = 0;
		}
	}
	
	if(pairs == 1){
		return 1;
	}
	else{
		return 0;
	}
}

// if the hand is two pair, return 1, else return 0
int isTwoPair(int * arr1){
	int i;
	int pairs = 0;
	int count = 0;
	for(i = 0; i < 5; i++){
		if(arr1[i] == arr1[i+1]){
			count++;
		}
		if(arr1[i] != arr1[i+1]){
			if(count == 1){
				pairs++;
			}
			count = 0;
		}
	}
	
	if(pairs == 2){
		return 1;
	}
	else{
		return 0;
	}
}

// if hand is a three of a kind, return 1, else 0
int isThreeKind(int * arr1){
	int i;
	int TK = 0;
	int count = 0;
	for(i = 0; i < 5; i++){
		if(arr1[i] == arr1[i+1]){
			count++;
		}
		if(arr1[i] != arr1[i+1]){
			if(count == 2){
				TK++;
			}
			
			count = 0;
			
		}
	}
	
	if(TK == 1){
		return 1;
	}
	else{
		return 0;
	}
}

// if hand is a four of a kind, return 1, else 0
int isFourKind(int * arr1){
	int i;
	int FK = 0;
	int count = 0;
	for(i = 0; i < 5; i++){
		if(arr1[i] == arr1[i+1]){
			count++;
		}
		if(arr1[i] != arr1[i+1]){
			if(count == 3){
				FK++;
			}
			count = 0;
		}
	}
	
	if(FK == 1){
		return 1;
	}
	else{
		return 0;
	}
}

// if hand is a straight, return 1, else 0
int isStraight(int * arr1){
	int i;
	int count = 0;
	for(i = 0; i < 5; i++){
		if(arr1[i]+1 == arr1[i+1]){
			count++;
		}
	}
	
	if(count == 4){
		return 1;
	}
	else{
		return 0;
	}
}

// if hand is a flush, return 1, else 0
int isFlush(char * arr2){
	int i;
	int count = 0;
	for(i = 0; i < 5; i++){
		char a = arr2[i];
		char b = arr2[i+1];
		if(a == b){
			count++;
		}
	}
	
	if(count == 4){
		return 1;
	}
	else{
		return 0;
	}
}

// if hand has a two pair and three of a kind, return 1, else 0
int isFullHouse(int * arr1){
	int handOP = isOnePair(arr1);
	int handTOK = isThreeKind(arr1);
	
	if(handOP == 1 && handTOK == 1){
		return 1;
	}
	else{
		return 0;
	}
}

// if hand is royal flush, return 1, else 0
int isRoyalFlush(int * arr1, char * arr2){
	int i;
	int royal = 0;
	int first = 10;
	for(i = 0; i < 5; i++){
		if(arr1[i] == 10+i){
			royal++;
		}
	}
	int flush = isFlush(arr2);
	
	if(royal == 5 && flush == 1){
		return 1;
	}
	else{
		return 0;
	}
}

int isStraightFlush(int * arr1, char * arr2){
	int i;
	int flushHand = isFlush(arr2);
	int straightHand = isStraight(arr1);
	
	if(flushHand == 1 && straightHand == 1){
		return 1;
	}
	else{
		return 0;
	}
}

int main(){
	
	char shuffle;
	int returnVal; // return value from syscall
	int fp; // this is our file pointer
	char buffer[2];
	char action[10];
	
	fp = open("/dev/deck", O_RDWR);
	if(fp == EACCES){
		printf("Unable to open /dev/deck\n");
		return -1;
	}
	
	/*
	*	TEST SECTION
	*/
	
	/*
	*	set the deck to its original sorted order
	*/
	shuffle = '0';
	returnVal = write(fp, &shuffle, 1);
	if(returnVal != 1){
		printf("Cannot write to device file");
		return -1;
	}
	
	/*
	*	get cards in sorted worder
	*/
	printf("Cards in order:\n");
	int counter = 0;
	while(counter < 52){
		
		returnVal = read(fp, buffer, 2);
		if(returnVal != 2){
			printf("Cannot read from device file\n");
			return -1;
		}
		
		printf("%d%c  ", buffer[0], buffer[1]);
		counter++;
		
		if(counter % 13 == 0){
			printf("\n");
		}
		
	}
	printf("\n");
	
	/*
	*	shuffle the cards
	*/
	shuffle = '1';
	returnVal = write(fp, &shuffle, 1);
	if(returnVal != 1){
		printf("Cannot write to device file");
		return -1;
	}
	
	/*
	*	get the shuffled cards and output them
	*/
	printf("Cards after reset and shuffled:\n");
	counter = 0;
	while(counter < 52){
		
		returnVal = read(fp, buffer, 2);
		if(returnVal != 2){
			printf("Cannot read from device file\n");
			return -1;
		}
		
		printf("%d%c  ", buffer[0], buffer[1]);
		counter++;
		
		if(counter % 13 == 0){
			printf("\n");
		}
		
	}
	printf("\n\n");
	
	/*
	*	End of Testing Portion, begins playing portion
	*/
	
	printf("Start playing...\n\n");
	
	int handFace[5];
	char handSuit[5];
	char toChange[20];
	do{
		
		printf("Your hand:\n");
		
		counter = 0;
		while(counter < 5){
			returnVal = read(fp, buffer, 2);
			
			handFace[counter] = buffer[0];
			handSuit[counter] = buffer[1];
			counter++;
		}
		
		for(counter = 0; counter < 5; counter++){
			printf("%d%c  ", handFace[counter], handSuit[counter]);
		}
		
		printf("\nSelect cards to be changed(0 for no change or select 1-5 for card posiitons up to 5 cards): ");
		scanf(" %[^\n]s", toChange);
		int cnt = 0;
		while(toChange[cnt] != '\0'){
			
			if(toChange[cnt] == '1'){
				returnVal = read(fp, buffer, 2);
				handFace[0] = buffer[0];
				handSuit[0] = buffer[1];
			}
			
			if(toChange[cnt] == '2'){
				returnVal = read(fp, buffer, 2);
				handFace[1] = buffer[0];
				handSuit[1] = buffer[1];
			}
			
			if(toChange[cnt] == '3'){
				returnVal = read(fp, buffer, 2);
				handFace[2] = buffer[0];
				handSuit[2] = buffer[1];
			}
			
			if(toChange[cnt] == '4'){
				returnVal = read(fp, buffer, 2);
				handFace[3] = buffer[0];
				handSuit[3] = buffer[1];
			}
			
			if(toChange[cnt] == '5'){
				returnVal = read(fp, buffer, 2);
				handFace[4] = buffer[0];
				handSuit[4] = buffer[1];
			}
			
			cnt++;
			
		}
		
		printf("Your hand:\n");
		for(counter = 0; counter < 5; counter++){
			printf("%d%c  ", handFace[counter], handSuit[counter]);
		}
		
		printf("\nYour sorted hand:\n");
		sort(handFace, handSuit);
		for(counter = 0; counter < 5; counter++){
			printf("%d%c  ", handFace[counter], handSuit[counter]);
		}
		
		/*
		*	Figure out what hand does the player have
		*/
		
		compareHands(handFace, handSuit);
		
		printf("\nWould you like to play again (y/n): ");
		scanf(" %[^\n]s", action);
		
	}
	while(action[0] == 'y');
	
	close(fp);
	
	return 0;
	
}