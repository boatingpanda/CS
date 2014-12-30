#include <stdio.h>
#include <stdlib.h>

int main(){
	char name[50]; // stores player's name
	char action[50]; // stores player's action to either play or not play
	int diceOne = 0;
	int diceTwo = 0;
	int sum = 0;
	int point = 0;
	
	printf("Welcome to Jons Casino!\nPlease enter your name: ");
	scanf("%[^\n]s", name);
	
	printf("Hello %s, would you like to play or quit? ", name);
	scanf(" %[^\n]s", action);
	
	if(strcmp(action, "play") == 0){
	
		do{
			printf("\n");
			srand((unsigned int)time(NULL));
			diceOne = rand() % 6 + 1;
			diceTwo = rand() % 6 + 1;
			sum = diceOne + diceTwo;
	
			if(sum == 2 || sum == 3 || sum == 12){
				printf("You rolled %i + %i = %i\n", diceOne, diceTwo, sum);
				printf("You Lose!\n");
			}
	
			else if(sum == 7 || sum == 11){
				printf("You rolled %i + %i = %i\n", diceOne, diceTwo, sum);
				printf("You Win!\n");
			}
	
			else{
				point = sum;
				sum = 0;
				while(sum != 7 || sum != point){
					diceOne = rand() % 6 + 1;
					diceTwo = rand() % 6 + 1;
					sum = diceOne + diceTwo;
					
					if(sum == 7){
						printf("You rolled %i + %i = %i\n", diceOne, diceTwo, sum);
						printf("You Lose!\n");
						break;
					}
					if(sum == point){
						printf("You rolled %i + %i = %i\n", diceOne, diceTwo, sum);
						printf("You Win!\n");
						break;
					}
				}
			}
			printf("Would you like to play again? ");
			scanf(" %[^\n]s", action);
		}
		while(strcmp(action, "yes") == 0);
		
	}
	printf("Goodbye, %s!\n", name);
	
	return 0;
}
