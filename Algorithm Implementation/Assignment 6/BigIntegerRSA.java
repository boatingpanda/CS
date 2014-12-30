import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class BigIntegerRSA {

	public static void main(String args[]) {
		
		// determine whether we're getting the number of bits in the primes or encoding/decoding
		
		boolean value = isInt(args[0]);
		
		if(value){
			int primeBits = Integer.parseInt(args[0]);
			
			// get a random number
			Random rnd = new Random();
			
			//get two distinct primes of size primeBits
			BigInteger p = new BigInteger(primeBits,128,rnd);
			BigInteger q;
			
			do q = new BigInteger(primeBits,128,rnd);
			while(p.compareTo(q) == 0);
			
			KeyGen(p,q);
			
		}
		else if(args[0].equals("-encode")){
			System.out.println("You want to encode " + args[1]);
			Encode(args[1]);
			
		}
		else if(args[0].equals("-decode")){
			System.out.println("You want to decode " + args[1]);
			Decode(args[1]);
		}
		else{
			System.out.println("ERROR: No instruction given, enter a number greater than 2, '-encode file.txt' to encode file.txt, or '-decode file.txt.enc' to decode file.txt.enc");
			System.exit(0);
		}
	
	}
	
	// determine whether input s is a String or an integer
	static boolean isInt(String s){
		
		boolean result = true;
		
		try{
			int i = Integer.parseInt(s);
		}
		catch(NumberFormatException e){
			result = false;
		}
		
		return result;
		
	}
	
	// write XGCD based on slides
	static BigInteger[] XGCD(BigInteger e, BigInteger m){
		
		if(m.compareTo(BigInteger.ZERO) == 0){
			return new BigInteger[] {e, BigInteger.valueOf(1), BigInteger.valueOf(0)};
		}
		
		else{
			
			BigInteger[] calc = XGCD(m, e.mod(m));
			BigInteger d = calc[0];
			BigInteger t = calc[2];
			BigInteger s = calc[1].subtract((e.divide(m).multiply(calc[2])));
			
			return new BigInteger[] {d, t, s};
			
		}
		
	}
	
	static void KeyGen(BigInteger p, BigInteger q){
		
		BigInteger n = p.multiply(q);
			
		// compute m = phi(n)
		BigInteger pMinus1 = p.subtract(BigInteger.valueOf(1));
		
		BigInteger qMinus1 = q.subtract(BigInteger.valueOf(1));
		
		BigInteger m = pMinus1.multiply(qMinus1);
		
		// get e relatively prime to m
		BigInteger e = BigInteger.valueOf(3);
		
		while(e.gcd(m).compareTo(BigInteger.valueOf(1)) > 0)
			e = e.add(BigInteger.valueOf(2));
		
		// compute d the decryption exponent, remember to change modInverse() to XGCD()
		BigInteger[] temp = XGCD(e, m);
		BigInteger d = temp[0];// e.modInverse(m);
		System.out.println("e = " + e + "\nd = " + d + "\nn=" + n + "\nphi = " + m);
		
		try{
			BufferedWriter out1 = new BufferedWriter(new FileWriter("public.txt"));
			out1.write(n.toString());
			out1.newLine();
			out1.write(e.toString());
			out1.close();
			
			BufferedWriter out2 = new BufferedWriter(new FileWriter("private.txt"));
			out2.write(n.toString());
			out2.newLine();
			out2.write(d.toString());
			out2.close();
		}
		catch(IOException except){
			System.out.println("Cannot write to file.");
			System.exit(0);
		}
		
	}
	
	static void Encode(String s){
		
		BigInteger n = BigInteger.valueOf(0);
		BigInteger e = BigInteger.valueOf(0);
		
		// read in public.txt for n and e values
		try{
			BufferedReader read = new BufferedReader(new FileReader("public.txt"));
			n = new BigInteger(read.readLine());
			e = new BigInteger(read.readLine());
			read.close();
		}
		catch(IOException except){
			System.out.println("Cannot read this file.");
			System.exit(0);
		}
		
		// see if we're reading a file for encryption or just plain input
		if(s.contains(".txt")){
			
			try{
				BufferedReader file = new BufferedReader(new FileReader(s));
				BufferedWriter toEncrypt = new BufferedWriter(new FileWriter("encrypted.txt.enc"));
				Scanner scan = new Scanner(file);
				
				while(scan.hasNext()){
					
					String temp = scan.nextLine();
					
					for(BigInteger i = BigInteger.valueOf(0); i.compareTo(BigInteger.valueOf(temp.length())) == -1; i = i.add(BigInteger.valueOf(1))){
						
						// compute x = a^n mod z.
						BigInteger c = BigInteger.valueOf((int)temp.charAt(i.intValue())).modPow(e,n);
						toEncrypt.write(c + " ");
						
					}
					
				}
				
				toEncrypt.close();
			}
			catch(IOException except){
				System.out.println("Cannot write to file.");
				System.exit(0);
			}
			
			System.out.println("Encryption complete!");
			
		}
		
		else{
			
			try{
				BufferedWriter toEncrypt = new BufferedWriter(new FileWriter("encrypted.txt.enc"));
				
				for(BigInteger i = BigInteger.valueOf(0); i.compareTo(BigInteger.valueOf(s.length())) == -1; i = i.add(BigInteger.valueOf(1))){
					
					// compute x = a^n mod z.
					BigInteger c = BigInteger.valueOf((int)s.charAt(i.intValue())).modPow(e,n);
					toEncrypt.write(c + " ");
					
				}
				
				toEncrypt.close();
			}
			catch(IOException except){
				System.out.println("Cannot write to file.");
				System.exit(0);
			}
			
			System.out.println("Encryption complete!");
			
		}
		
	}
	
	static void Decode(String s){
		
		BigInteger n = BigInteger.valueOf(0);
		BigInteger d = BigInteger.valueOf(0);
		
		// read in public.txt for n and e values
		try{
			BufferedReader read = new BufferedReader(new FileReader("private.txt"));
			n = new BigInteger(read.readLine());
			d = new BigInteger(read.readLine());
			read.close();
		}
		catch(IOException except){
			System.out.println("Cannot read this file.");
			System.exit(0);
		}
		
		try{
			// decoding from file .txt.enc and output to .txt.cop
			BufferedReader file = new BufferedReader(new FileReader(s));
			BufferedWriter toDecrypt = new BufferedWriter(new FileWriter("decoded.txt.cop"));
			Scanner scan = new Scanner(file);
			
			while(scan.hasNext()){
				
				int temp = scan.nextInt();
					
				// compute x = a^n mod z.
				BigInteger cTemp = BigInteger.valueOf(temp).modPow(d,n);
				char c = (char)cTemp.intValue();
				toDecrypt.write(c);
				
			}
			
			toDecrypt.close();
		}
		catch(IOException except){
			System.out.println("Cannot write to file.");
			System.exit(0);
		}

		System.out.println("Decoding complete!");
			
	}
	
}