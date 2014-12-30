import java.util.*;
import java.io.*;

public class RVG{
	
	public static void main(String[] args) throws IOException {
		
		Scanner userInput = new Scanner(System.in);
		
		// Get the number of values to generate from user
		System.out.print("Please enter the number of values to generate (must be integers): ");
		int numGenerated = userInput.nextInt();
		
		// Get the generation method from user and create a write buffer
		System.out.println("Please select the generation method:\n1.Inverse Transform\n2.Accept/Reject\n3.Polar-Coordinate");
		int gMethod = userInput.nextInt();
		BufferedWriter writeFile = new BufferedWriter(new FileWriter("results.txt"));
		
		// Construct the core random number generator provided by Java
		Random rand = new Random(1259);
		
		// If user selected Inverse Transform method, perform this
		if(gMethod == 1){
			
			long startingTime = System.nanoTime();
			writeFile.write("Generated using Inverse Transform method:\n");
			
			int[] intervalCounts = new int[8];
			double[] val = new double[numGenerated];
			
			for(int i = 0; i < numGenerated; i++){
				
				// Generate a random number between 0 (inclusive) and 1 (exclusive)
				double randNum = rand.nextDouble();
				
				// The inverse function of y=1/(1+e^(-1.702x)) is y=-ln(1/x-1)/1.702
				// Put the random values into the val array
				val[i] = Math.log(1/randNum - 1) / (-1.702);
				writeFile.write(val[i] + "\n");
				
				// Check to see which interval it belongs to and increment that interval count
				checkInterval(val[i], intervalCounts);
				
			}
			
			// Write the interval count to file
			int startingInterval = -3;
			writeFile.write("Interval counts:\n");
			for(int i = 0; i < 8; i++){
				writeFile.write(startingInterval + "\t" + intervalCounts[i] + "\n");
				startingInterval++;
			}
			
			long endingTime = System.nanoTime();
			long timeElapsed = endingTime - startingTime;
			writeFile.write("\nTime Elapsed since method execution: " + timeElapsed + "ns");
			
			writeFile.close();
			System.out.println("Results are at results.txt");
			
		}
		
		// If user selected Accept/Reject method, perform this
		else if(gMethod == 2){
			
			long startingTime = System.nanoTime();
			writeFile.write("Generated Using Accept/Reject method:\n");
			
			// The function we're using is lambda(e^(-lambda * x)) where lambda is 1
			// g(x) is e^-x
			double x1, x2;
			int numRejected = 0;
			int numAccepted = 0;
			int totalNumGenerated = 0;
			
			int[] intervalCounts = new int[8];
			
			while(numAccepted < numGenerated){
				
				// Calculate X1 and X2
				x1 = -Math.log(rand.nextDouble());
				x2 = -Math.log(rand.nextDouble());
				totalNumGenerated++;
				
				// If X2 is greater or equal to (X1-1)^2/2, then accept
				if(x2 >= Math.pow(x1-1, 2)/2){
					
					// Determine whether to return positive or negative value for sample
					double randNum = rand.nextDouble();
					if(randNum < 0.5){
						x1 = x1 * (double)-1;
						writeFile.write("X = " + x1 + "\n");
					}
					else
						writeFile.write("X = " + x1 + "\n");
					
					numAccepted++;
					
					// Check to see which interval it belongs to and increment that interval count
					checkInterval(x1, intervalCounts);
					
				}
				
				else
					numRejected++;
				
			}
			
			// Write the interval count to file
			int startingInterval = -3;
			writeFile.write("Interval counts:\n");
			for(int i = 0; i < 8; i++){
				writeFile.write(startingInterval + "\t" + intervalCounts[i] + "\n");
				startingInterval++;
			}
			
			writeFile.write("Total numbers generated: " + totalNumGenerated + "\n");
			writeFile.write("Total numbers accepted: " + numAccepted + "\n");
			writeFile.write("Total numbers rejected: " + numRejected + "\n");
			double rejectPerc = (double)numRejected/(double)totalNumGenerated;
			writeFile.write("Average number of rejections: " + rejectPerc + "\n");
			
			long endingTime = System.nanoTime();
			long timeElapsed = endingTime - startingTime;
			writeFile.write("\nTime Elapsed since method execution: " + timeElapsed + "ns\n");
			writeFile.close();
			System.out.println("Results are at results.txt");
			
		}
		
		// If user selected Polar-Coordinate method, perform this using Box & Muller
		else if(gMethod == 3){
			
			long startingTime = System.nanoTime();
			writeFile.write("Generated Using Polar-Coordinate method:\n");
			
			int numAccepted = 0;
			
			int[] intervalCounts = new int[8];
			
			while(numAccepted < numGenerated){
				
				// Get 2 random numbers from -1 (inclusive) to 1 (exclusive)
				double v1 = (double)(rand.nextInt(Integer.MAX_VALUE) - Integer.MAX_VALUE/2) / (double)(Integer.MAX_VALUE/2);
				double v2 = (double)(rand.nextInt(Integer.MAX_VALUE) - Integer.MAX_VALUE/2) / (double)(Integer.MAX_VALUE/2);
				
				// Get the sum of V1 and V2
				double sum = v1*v1 + v2*v2;
				
				// if sum is less than 1, accept the value and use it to calculate Z1 and Z2
				if(sum < 1){
					sum = Math.sqrt(-2 * Math.log(sum) / sum);
					double z1 = v1 * sum;
					double z2 = v2 * sum;
					
					writeFile.write("Value of Z1 and Z2 respectively: " + z1 + "\t" + z2 + "\n");
					numAccepted += 2;
					
					// Check to see which interval it belongs to and increment that interval count
					checkInterval(z1, intervalCounts);
					checkInterval(z2, intervalCounts);
					
				}
				
			}
			
			// Write the interval count to file
			int startingInterval = -3;
			writeFile.write("Interval counts:\n");
			for(int i = 0; i < 8; i++){
				writeFile.write(startingInterval + "\t" + intervalCounts[i] + "\n");
				startingInterval++;
			}
			
			long endingTime = System.nanoTime();
			long timeElapsed = endingTime - startingTime;
			writeFile.write("\nTime Elapsed since method execution: " + timeElapsed + "ns\n");
			writeFile.close();
			System.out.println("Results are at results.txt");
			
		}
		
		else{
			
			System.out.println("\nInvalid input, only Inverse Transform, Accept/Reject, and Polar-Coordinate methods are supported. Valid input are 1, 2, and 3 respectively.\nProgram terminating.");
			writeFile.close();
			System.exit(0);
			
		}
		
	}
	
	static void checkInterval(double val, int[] arr){
		
		if(val < -3)
			arr[0]++;
		if(val >= -3 && val < -2)
			arr[1]++;
		if(val >= -2 && val < -1)
			arr[2]++;
		if(val >= -1 && val < 0)
			arr[3]++;
		if(val >= 0 && val < 1)
			arr[4]++;
		if(val >= 1 && val < 2)
			arr[5]++;
		if(val >= 2 && val < 3)
			arr[6]++;
		if(val >= 3)
			arr[7]++;
		
	}
	
}