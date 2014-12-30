import java.util.Scanner;

public class Encode{
	
	public static void main(String[] args){
		
		Scanner input = new Scanner(System.in);
		
		System.out.print("Enter the string to encode: ");
		String s = input.nextLine();
		System.out.println();
		
		System.out.print("Enter the encoding exponent e: ");
		long e = input.nextLong();
		System.out.println();
		
		System.out.print("Enter the modulus n: ");
		long n = input.nextLong();
		System.out.println();
		
		System.out.print("Transmitting encoded " + s + " as: ");
		
		for(int i = 0; i < s.length(); i++){
			
			
			long c = expomod(s.charAt(i),e,n);
			System.out.print(c + " ");
			
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