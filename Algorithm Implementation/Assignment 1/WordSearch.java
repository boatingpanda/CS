public class WordSearch{
	
	public static void main(String [] args){
		
		boolean loop = true;
		while(loop){
			StdOut.println("Welcome to Word Search! Run with \"-help\" for command line options.");
			
			String boardName = null;
			String dict = null;
			int cols = 6;
			char[][] board = null;
			Bag<String>bg = new Bag();
			TST dictTST = new TST();
			TST gameTST = new TST();
			
			for(int i = 0; i < args.length; i++){
				
				if(args[i].equals("-dict")){
					i++;
					dict = args[i];
				}
				
				else if(args[i].equals("-board")){
					i++;
					boardName = args[i];
				}
				
				else if(args[i].equals("-cols")){
					i++;
					cols = Integer.parseInt(args[i]);
				}
				
				else{
					StdOut.println("Options:");
					StdOut.println("\"-board FILENAME\": Specifies game board file.");
					StdOut.println("\"-dict FILENAME\": Specifies dictionary file.");
					StdOut.println("\"-cols NUMCOLS\": Specifies the number of columns for printing words.");
					System.exit(0);
				}
				
			}
			
			// Takes care of what happens if board name and/or dictionary is null
			if(boardName == null){
				StdOut.print("Please enter the name of the file containing game board information: ");
				boardName = StdIn.readString();
				StdOut.println("");
			}
			if(dict == null){
				StdOut.print("Please enter the name of the file containing dictionary: ");
				dict = StdIn.readString();
				StdOut.println("");
			}
			
			// Process dictionary reading
			In dictInBag = new In(dict);
			Stopwatch swBag = new Stopwatch();
			while(dictInBag.hasNextLine()){
				bg.add(dictInBag.readLine());
			}
			StdOut.println("Time to read dictionary from disk to Bag: " + swBag.elapsedTime());
			
			int count = 0;
			Stopwatch swTST = new Stopwatch();
			for(String temp : bg){
				dictTST.put(temp,count);
				count++;
			}
			StdOut.println("Time to move dictionary from Bag to TST: " + swTST.elapsedTime());

			// Process game board		
			In readBoard = new In(boardName);
			int row = Integer.parseInt(readBoard.readLine());
			int col = row;
			int counter = 0;
			board = new char[row][col];
			
			while(readBoard.hasNextLine()){
				String temp = readBoard.readLine().replaceAll("\\s+","");
				for(int k = 0; k < col; k++){
					board[counter][k] = temp.charAt(k);
				}
				counter++;
			}
			
			Stopwatch swWordInGameBoard = new Stopwatch();
			// Find the words in the game board
			StringBuilder gameWord = new StringBuilder();
			int value = 0;
			// horizontal
			for(int i = 0; i < row; i++){
				for(int k = 0; k < col; k++){
					for(int m = k+3; m < col; m++){
						for(int n = k; n <= m; n++){
							gameWord.append(board[i][n]);
						}
						String temp = gameWord.toString();
						// if the string we're looking at has a wildcard, put the closest matched words in Iterable s and add them into the gameTST. Also work on normal words
						if(temp.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(temp.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(temp) && dictTST.contains(temp.toLowerCase())){
								gameTST.put(temp,value);
								value++;
							}
						}
						// Now do the same for reverseed string
						String tempReverse = gameWord.reverse().toString();
						if(tempReverse.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(tempReverse.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(tempReverse) && dictTST.contains(tempReverse.toLowerCase())){
								gameTST.put(tempReverse,value);
								value++;
							}
						}
						gameWord.delete(0, m+1);
						
					}
				}
			}
			
			
			// vertical- iterate through the board vertically and set a hard limit on how long the word can be (4 minimum, column is the max)
			for(int i = 0; i < col; i++){
				for(int k = 0; k < row; k++){
					for(int m = k+3; m < row; m++){
						for(int n = k; n <= m; n++){
							gameWord.append(board[n][i]); // start building a word that's 4 letters long
						}
						String temp = gameWord.toString();
						// Do the same thing as we did for horizontal lines, find the wildcard and replace it. Also take care of normal words
						if(temp.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(temp.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(temp) && dictTST.contains(temp.toLowerCase())){
								gameTST.put(temp,value);
								value++;
							}
						}
						
						String tempReverse = gameWord.reverse().toString();
						if(tempReverse.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(tempReverse.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(tempReverse) && dictTST.contains(tempReverse.toLowerCase())){
								gameTST.put(tempReverse,value);
								value++;
							}
						}

						gameWord.delete(0, m+1);
						
					}
				}
			}
			
			// diagonal 1- iterate through the board diagonally starting at (0,0) -> (0, 1),(1,0) -> (0,2),(1,1),(2,0) ...
			int maxSum = row + col - 2;
			
			for(int sum = 0; sum < maxSum; sum++){
				for(int i = 0; i < row; i++){
					for(int k = 0; k < col; k++){
						if(i + k - sum == 0){
							gameWord.append(board[i][k]);
						}
					}
				}
				// if the string of word is less than 4 characters long, reset gameWord and go to the next iteration
				if(gameWord.length() < 4){
					gameWord.delete(0, gameWord.length());
					continue;
				}
				// otherwise load gameWord and its reverse into the gameTST, reset gameWord once it's done and proceed to the next iteration
				else{
					for(int i = gameWord.length(); i >= 4; i--){
						// Take care of the wildcard for diagonal 1 and normal words
						String temp = gameWord.substring(0, i);
						if(temp.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(temp.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(temp) && dictTST.contains(temp.toLowerCase())){
								gameTST.put(temp,value);
								value++;
							}
						}
						
						String tempReverse = "";
						for(int k = temp.length()-1; k >= 0; k--){
							tempReverse += temp.charAt(k);
						}
						if(tempReverse.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(tempReverse.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(tempReverse) && dictTST.contains(tempReverse.toLowerCase())){
								gameTST.put(tempReverse,value);
								value++;
							}
						}
						
					}
					gameWord.delete(0, gameWord.length());
				}
				
			}
			
			// diagonal 2
			// first we make a temporary 2D array, which will be the mirror image of the original
			char[][] boardMirror = new char[row][col];
			for(int i = 0; i < row; i++){
				for(int k = 0; k < col; k++){
					boardMirror[i][k] = board[i][col-1-k];
				}
			}
			// then we use the same algorithm we used for diagonal 1
			int maxSumDiag = row + col - 2;
			
			for(int sum = 0; sum < maxSumDiag; sum++){
				for(int i = 0; i < row; i++){
					for(int k = 0; k < col; k++){
						if(i + k - sum == 0){
							gameWord.append(boardMirror[i][k]);
						}
					}
				}
				// if the string of word is less than 4 characters long, reset gameWord and go to the next iteration
				if(gameWord.length() < 4){
					gameWord.delete(0, gameWord.length());
					continue;
				}
				// otherwise load gameWord and its reverse into the gameTST, reset gameWord once it's done and proceed to the next iteration
				else{
					for(int i = gameWord.length(); i >= 4; i--){
						// Take care of the wildcard for diagonal 1 and normal words
						String temp = gameWord.substring(0, i);
						if(temp.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(temp.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(temp) && dictTST.contains(temp.toLowerCase())){
								gameTST.put(temp,value);
								value++;
							}
						}
						
						String tempReverse = "";
						for(int k = temp.length()-1; k >= 0; k--){
							tempReverse += temp.charAt(k);
						}
						if(tempReverse.indexOf('*') > 0){
							Iterable s = dictTST.wildcardMatch(tempReverse.toLowerCase());
							for(Object tempString : s){
								gameTST.put(tempString.toString().toUpperCase(), value);
								value++;
							}
						}
						else{
							if(!gameTST.contains(tempReverse) && dictTST.contains(tempReverse.toLowerCase())){
								gameTST.put(tempReverse,value);
								value++;
							}
						}
						
					}
					gameWord.delete(0, gameWord.length());
				}
				
			}
			
			StdOut.println("Time to find all words in game board: " + swWordInGameBoard.elapsedTime());
			
			// Print out the game board
			StdOut.println();
			StdOut.println("Game board: ");
			for(int i = 0; i < row; i++){
				for(int k = 0; k < col; k++){
					StdOut.print(board[i][k] + " ");
				}
				StdOut.println("");
			}
			
			// Let player enters a word and checks whether it's in the game board or not. Input is not case sensitive.
			int score = 0;
			String playerAnswer = null;
			TST playerCorrectWords = new TST();
			StdOut.printf("You can now enter a word. Try to find as many words you think are in this board. Enter \"quit\" to end the game and reveal the answers.\n");
			
			playerAnswer = StdIn.readString().toLowerCase();
			while(!playerAnswer.equals("quit")){
				if(gameTST.contains(playerAnswer.toUpperCase())){
					StdOut.println("Great! " + playerAnswer + " is in the dictionary and on the game board!");
					playerCorrectWords.put(playerAnswer,score);
					score++;
					playerAnswer = StdIn.readString().toLowerCase();
				}
				else{
					StdOut.println("Sorry, " + playerAnswer + " is not both in the dictionary and on the game board!");
					playerAnswer = StdIn.readString().toLowerCase();
				}
			}
			
			// Handles what happens when player quits
			StdOut.println("");
			StdOut.println("List of words that are on the game board and in the dictionary:");
			StdOut.println("");
			// Prints out the list of words that are on the game board and in the dictionary in number of columns specified by user, default is 6
			int tstCounter = 0;
			
			for( Object s : gameTST.keys()){
				if(tstCounter == 0){
					StdOut.printf(s + "\t");
				}
				else if(tstCounter % cols == (cols-1)){
					StdOut.printf(s + "\n");
				}
				else{
					StdOut.printf(s + "\t");
				}
				tstCounter++;
			}
			StdOut.println("");
			StdOut.println("");
			StdOut.println("List of words that you found: ");
			StdOut.println("");
			// Prints out the list of words found by the player in number of columns specified by user, default is 6
			int playerTstCounter = 0;
			for( Object s : playerCorrectWords.keys()){
				if(playerTstCounter == 0){
					StdOut.printf(s + "\t");
				}
				else if(playerTstCounter % cols == (cols-1)){
					StdOut.printf(s + "\n");
				}
				else{
					StdOut.printf(s + "\t");
				}
				playerTstCounter++;
			}
			StdOut.println("");
			StdOut.println("");
			
			// Calculate the final player score and ask if player would like to play again
			double playerScore = (double)score/(double)gameTST.size()*100;
			StdOut.printf("You've found " + score + " out of " + gameTST.size() + " words, score is: %.2f percent.\n", playerScore);
			StdOut.println("Play again? [Y to continue, anything else to quit]");
			String playerResponse = StdIn.readString();
			if(playerResponse.toUpperCase().equals("Y")){
				loop = true;
			}
			else{
				System.exit(0);
			}
		}
	}
	
}