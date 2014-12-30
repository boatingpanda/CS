import java.util.ArrayList;

public class MoveToFront{
	
	static int SIZE = 256;
	
	// apply move-to-front encoding, reading from standard input and writing to standard output
	public static void encode(){
		
		ArrayList<Character> asciiChar = new ArrayList<Character>(SIZE);
		
		for (int i = 0; i < SIZE; i++){
			asciiChar.add((char)i);
		}
		
		while(!BinaryStdIn.isEmpty()){
			char c = BinaryStdIn.readChar();
			
			for(int i = 0; i < SIZE; i++){
			
				if(asciiChar.get(i) == c){
					char index = (char)i;
					BinaryStdOut.write(index);
					char temp = asciiChar.get(i);
					
					/*for(int k = i; k > 0; k--){
						asciiChar.set(k, asciiChar.get(k-1));
					}*/
					asciiChar.remove(i);
					asciiChar.add(0,temp);
				}
				
			}
		}
		BinaryStdOut.close();
		
	}
	
	// apply move-to-front decoding, reading from standard input and writing to standard output
	public static void decode(){
		
		ArrayList<Character> asciiChar = new ArrayList<Character>(SIZE);
		
		for (int i = 0; i < SIZE; i++){
			asciiChar.add((char)i);
		}
		
		while(!BinaryStdIn.isEmpty()){
			
			int asciiCode = BinaryStdIn.readByte();
			
			BinaryStdOut.write(asciiChar.get(asciiCode));
			char temp = asciiChar.get(asciiCode);
			
			/*for(int i = asciiCode; i > 0; i--){
				asciiChar.set(i, asciiChar.get(i-1));
			}*/
			asciiChar.remove(asciiCode);
			asciiChar.add(0,temp);
			
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