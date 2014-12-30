#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>

int commands(char * args){
	
	int i = 0;
	int length = 0;
	while(args[i] != NULL){
		if(args[i] == ' '){
			length++;
		}
		i++;
	}
	
	char * cmd = strtok(args, " ()|&;\t\n");
	
	// exit command
	if(strcmp(cmd, "exit") == 0){
		exit(0);
	}
	
	// change directory
	else if(strcmp(cmd, "cd") == 0){
		
		cmd = strtok(NULL, " ");
		char * temp = malloc(sizeof(cmd));
		temp = strncpy(temp , cmd, strlen(cmd) -1);
		
		// changing directory to home
		if(strcmp(cmd, "--\n") == 0){
			
			// get home directory
			char * home = getenv("HOME");
			chdir(home);
			
		}
		// go previous directory
		else if(strcmp(cmd, "..") == 0){
			return 1;
		}
		
		// go to a specific directory
		else if(chdir(temp) == 0){
			return 1;
		}
		
		// otherwise print perror
		else{
			perror("\nError in changing directory.");
			return 1;
		}
		
	}
	
	else{
		
		int counter = 0;
		if(length == 0){
			length = 1;
		}
		char *token[length+1];
		
		//fill the array with the strings.
		while(cmd != NULL){
			token[counter] = cmd;
			cmd = strtok(NULL, " ()|&;\t\n");
			counter++;
		}
		
		if(fork() == 0){
			
			// redirect output to file
			if(length + 1 > 2 && strcmp(token[length-1], ">") == 0){
				char *input[length+1];
				int cnt = 0;
				while(cnt < length+1){
					input[cnt] = token[cnt];
					cnt++;
				}
				input[length] = NULL;
				input[length-1] = NULL;
				
				freopen(token[length], "w", stdout);
				if(execvp(input[0], input) == -1){
					perror("execvp");
					exit(-1);
				}
			}
			
			// redirect output by appending to existing file
			else if(length + 1 > 2 && strcmp(token[length-1], ">>") == 0){
				char *input[length+1];
				int cnt = 0;
				while(cnt < length+1){
					input[cnt] = token[cnt];
					cnt++;
				}
				input[length] = NULL;
				input[length-1] = NULL;
				
				freopen(token[length], "a", stdout);
				if(execvp(input[0], input) == -1){
					perror("execvp");
					exit(-1);
				}
			}
			
			// redirect input from file
			else if(length + 1 > 2 && strcmp(token[length-1], "<") == 0){
				char *input[length+1];
				int cnt = 0;
				while(cnt < length+1){
					input[cnt] = token[cnt];
					cnt++;
				}
				input[length] = NULL;
				input[length-1] = NULL;
				
				freopen(token[length], "r", stdin);
				if(execvp(input[0], input) == -1){
					perror("execvp");
					exit(-1);
				}
			}
			
			// all other instructions not related to input/output redirection, exit, or change directory
			else{
				if(execvp(token[0], token) == -1){
					perror("execvp");
					exit(-1);
				}
				printf("\n");
			}
			
		}
		
		// wait for the child process to finish
		else{
			int status;
			wait(&status);
		}
		
	}
	
}

int main(){
	
	char args[512];
	
	// keeps looping unless the command "exit" is entered by user
	while(1){
		
		// prints out the current directory for ease of use
		char * dir = (char *) get_current_dir_name();
		printf("~myshell:%s ", dir);
		
		fgets(args, sizeof(args), stdin);
		
		// if there's no command input, go back
		if(args == NULL || strcmp(args, "\n") == 0){
			continue;
		}
		
		if(commands(args) == 1){
			continue;
		}
		
	}
	
	return 0;
	
}