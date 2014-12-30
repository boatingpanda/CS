import java.util.Scanner;

public class Decode{
	
	public static void main(String[] args){
		
		Scanner input = new Scanner(System.in);
		
		System.out.print("Enter the decoding exponent d: ");
		long d = input.nextLong();
		System.out.println();
		
		System.out.print("Enter the modulus n: ");
		long n = input.nextLong();
		System.out.println();
		
		System.out.print("Enter the number of integers to decode: ");
		int s = input.nextInt();
		System.out.println();
		
		long[] convert = new long[s];
		for(int i = 0; i < s; i++){
			convert[i] = input.nextLong();
		}
		
		System.out.print("Decoding ");
		for(int i = 0; i < s; i++){
			System.out.print(convert[i] + " ");
		}
		System.out.println("as: ");
		
		for(int i = 0; i < s; i++){
			
			char c = (char)expomod(convert[i],d,n);
			System.out.print(c);
			
		}
		System.out.println();
		
	}
	
	static long expomod(long a, long n, long z) {
		
		long r = a % z;
		
		for(long i = 1; i < n; i++) {
			
			r = (a * r) % z;
		}
		
		return r;
		
	 }
	
}