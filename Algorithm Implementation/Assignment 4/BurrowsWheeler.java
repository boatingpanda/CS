import java.util.Arrays;

public class BurrowsWheeler{
	
	// apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
	public static void encode(){
		
		String s = BinaryStdIn.readString();
		CircularSuffixArray csArray = new CircularSuffixArray(s);
		char[] c = s.toCharArray();
		char[] lastColChar = new char[csArray.length()];
		int originalLoc = 0;
		
		for(int i = 0; i < csArray.length(); i++){
			
			if(csArray.index(i) == 0){ // find where the original string went and record its index
				originalLoc = i;
			}
			
			int lastLoc = csArray.index(i) - 1; // Stores the index of the last character in that specific suffix by subtracting 1
			if(lastLoc < 0){ // if lastLoc happens to be negative, then set it to the last character's index
				lastLoc = c.length - 1;
			}
			lastColChar[i] = c[lastLoc];
			
		}
		
		BinaryStdOut.write(originalLoc);
		
		for(int i = 0; i < lastColChar.length; i++){
			BinaryStdOut.write(lastColChar[i]);
		}
		BinaryStdOut.close();
		
	}
	
	// apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
	public static void decode(){
		
		int originalLoc = BinaryStdIn.readInt(); // the original location of the original string passed in from Burrows Wheeler encoding process
		String encoded = BinaryStdIn.readString(); // the last column characters passed in from Burrows Wheeler encoding process
		int[] next = new int[encoded.length()]; // the next array
		char[] lastColChar = encoded.toCharArray(); // the character array of the last column characters passed in from input
		
		Arrays.sort(lastColChar);
		int[] dictionary = new int[256];
		
		for(int i = 0; i < lastColChar.length; i++){
			
			int start = dictionary[(int)lastColChar[i]];
			for(int k = start; k < encoded.length(); k++){
				
				if(lastColChar[i] == encoded.charAt(k)){
					next[i] = k;
					dictionary[(int)lastColChar[i]] = k+1;
					break;
				}
				
			}
			
			
		}
		
		for(int i = 0; i < next.length; i++){
			BinaryStdOut.write(lastColChar[originalLoc]);
			originalLoc = next[originalLoc];
		}
		BinaryStdOut.close();
		
	}
	
	// if args[0] is '-', apply move-to-front encoding
	// if args[0] is '+', apply move-to-front decoding
	
	public static void main(String [] args){
		
		if(args[0].equals("+")){
			decode();
		}
		
		else if(args[0].equals("-")){
			encode();
		}
		
		else{
			throw new IllegalArgumentException("Illegal command line argument");
		}
		
	}
	
}