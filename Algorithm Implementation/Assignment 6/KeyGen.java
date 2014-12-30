import java.util.Scanner;

public class KeyGen{
	
	public static void main(String[] args){
		
		long p;
		long q;
		Scanner input = new Scanner(System.in);
		
		// Ask the user to input p and q
		System.out.println("Please enter a prime number: ");
		p = input.nextLong();
		
		System.out.println("Please enter a prime number that's larger than the previous one: ");
		q = input.nextLong();
		
		// choose n as the product of p and q
		// no known algorithm can recompute p and q from n within  
		// a reasonable period of time for large n.
		long n = p * q;
		System.out.println("The value of n = " + n);
		
		// Compute phi = (p-1)*(q-1). 

		long phi = (p - 1) * ( q - 1);
		System.out.println("The value of PHI = " + phi);
		
		// choose a random prime e between 1 and phi, exclusive,  
		// so that e has no common factors with phi.
		long e = findfirstnocommon(phi);
		System.out.println("The public exponent (e) = " + e);
		
		// Compute d as the multiplicative inverse of e
		// modulo phi(n).
		long d = findinverse(e,phi); 
		System.out.println("The private key (d) is " + d);
		
	}
	
	static long findfirstnocommon(long n) {
		
		long j;
		
		for(j = 2; j < n; j++)
			if(euclid(n,j) == 1)  return j;
		
		return 0;
		
	}

	static long findinverse(long n, long phi) {
		
		long i = 2;
		
		while( ((i * n) % phi) != 1) i++;
		
		return i;
		
	}
	
	static long euclid(long m, long n) {
		
		// pre: m and n are two positive integers (not both 0)
		// post: returns the largest integer that divides both
		// m and n exactly
		
		while(m > 0) {
			
			long t = m;
			m = n % m;
			n = t;
		}
		
		return n;

	}
	
}