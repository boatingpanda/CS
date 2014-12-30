#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>

int main()
{
	char returnStatus;
	if(fork() == 0)
	{
		char *args[3];
		args[0] = "lss";
		args[1] = "-l";
		args[2] = NULL;

		freopen("lsOutput.txt", "w", stdout);
		if(execvp(args[0], args) == -1)
		{
			perror("execvp");
			exit(-1);
		}
	}
	else
	{
		int status;
		wait(&status);
	}

	return 0;
}
