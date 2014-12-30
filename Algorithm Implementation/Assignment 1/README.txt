To run the progam, simply compile with javac WordSearch.java and type out java WordSearch -help for command line help:
	-dict FILENAME to enter dictionary's file name plus extention
	-board FILENAME to enter file's name which contains number of rows and columns and the matrix
	-cols NUMCOLS to indicate number of columns the program will display endgame results
	-help for this help menu
	
NOTE: MAKE SURE TO KEEP ALL OF THE FILES FROM THE ZIP IN THE SAME FOLDER OR ELSE THIS WON'T WORK
	
HOW THE PROGRAM FOUND WORDS ON THE GAME BOARD:
	For horizontal and vertical directions:
		The program uses a regular nested for-loop on the outter two loops to traverse the matrix in the desired direction, the inner nested for-loops is used to set a hard limit on how far the loop may travel while building a string of word using StringBuilder.
		Once the desired length of word has been reached, the program checks for a wildcard inside the word. If there's a wildcard character, it utilizes TST's wildcardMatch() to find all the words from the dictionary that matches the word's pattern then place it in the gameTST. If a wildcard character is not found, it will simply check if the word is in the dictionary TST and not already in game's TST. If found to be true, then it will be placed in the game's TST
		
	For diagonal going from Northwest to Southeast:
		The pattern of travel is such that when we isolate each paths (for instance, (0,0) -> (0, 1),(1,0) -> (0,2),(1,1),(2,0) ...) the sum of each point on the path are the same and grows until the max sum is reached at row + col - 2. Therefore we can use a normal nested for-loop to traverse through the array, then add another for-loop outside of this to count the sum and make sure we don't go pass the max sum in the matrix. Therefore, each desired letter can be obtained by testing if row + col - sum is equal to 0. Again, this uses the same method to check for wildcard character as the horizontal and vertical methods.
		
	For diagonal going from Northeast to Southwest:
		Simply make a mirror image of the game board and use the same code used for the previous diagonal
		
	For all directions:
		since there is a word going in the opposite direction for all directions, all words used StringBuilder's .reverse() to obtain all possible words

TO CHECK WHETHER THE PLAYER HAS THE RIGHT WORD:
	The program simply compares the player's input with the words in game's TST. If the word exists, then the player has the right word.