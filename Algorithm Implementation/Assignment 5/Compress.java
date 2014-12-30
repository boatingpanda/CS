import java.io.*;

public class Compress{
	
	public static void main(String[] args) throws IOException{
		
		AdaptiveHuffmanTree tree = new AdaptiveHuffmanTree();
		Out file = new Out("statistics.txt");
		String toFile = "";
		int bitsRead = 0;
		int bitTrans = 0;
		double compRat = 0;
		
		while(!BinaryStdIn.isEmpty()){
			
			char c = BinaryStdIn.readChar();
			toFile += c;
			
			if(tree.characterInTree(c) == false){
				
				String s = tree.getCodeWordForNYT().toString();
				
				for(int i = 0; i < s.length(); i++){
					
					if(s.charAt(i) == '0'){
						BinaryStdOut.write(false);
					}
					if(s.charAt(i) == '1'){
						BinaryStdOut.write(true);
					}
					bitTrans++;
					
				}
				BinaryStdOut.write(c);
				bitTrans += 8;
				
			}
			
			else{
				
				String s = tree.getCodeWordFor(c).toString();
				
				for(int i = 0; i < s.length(); i++){
					
					if(s.charAt(i) == '0'){
						BinaryStdOut.write(false);
					}
					if(s.charAt(i) == '1'){
						BinaryStdOut.write(true);
					}
					bitTrans++;
					
				}
				
			}
			
			tree.update(c);
			
		}
		
		bitsRead = toFile.length() * 8;
		compRat = (1 - ((double)bitTrans/(double)bitsRead)) * 100;
		
		file.println(toFile);
		file.println("bits read: " + bitsRead);
		file.println("bits transmitted: " + bitTrans);
		file.println("compression ratio: "+ compRat);
		
		BinaryStdOut.close();
		file.close();
		
	}
	
}