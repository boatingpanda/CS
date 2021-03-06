import java.util.*;
import java.io.*;

public class LCG{
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Please choose your operation followed by how many numbers to generate (i.e., 1 30 generates 30 random numbers using Java's random number generator.)\n1. Generate random numbers using Java\n2. Generate random numbers using LCG default configuration\n3. Generate random numbers using LCG RANDU configuration");
		
		Scanner scan = new Scanner(System.in);
		int op = scan.nextInt();
		int loops = scan.nextInt();
		int maxLoops = (loops < 10000) ? loops : 10000;
		
		// this is the seed we're going to use for all three cases
		long x0 = 1259;
		double seed = x0;
		
		// this is used to store the random numbers
		double[] randNums = new double[loops];
		
		if(loops <= 0){
			System.out.println("\nInvalid input for number of random numbers to generate, must be greater than 0 (not including 0). Program terminating...");
			System.exit(0);
		}
		
		String fileName = "";
		
		if(op == 1)
			fileName = "Java_results";
		if(op == 2)
			fileName = "LCG_results";
		if(op == 3)
			fileName = "RANDU_results";
		
		BufferedWriter writeFile = new BufferedWriter(new FileWriter( fileName + ".txt"));
		
		// this is the random number returned by java's nextDouble using the seed
		if(op == 1){
			
			Random rand = new Random(x0);
			writeFile.write("Random number generated by java's nextDouble():\n");
			for(int i = 0; i < loops; i++){
				double num = rand.nextDouble();
				randNums[i] = num;
				writeFile.write(num + "\n");
			}
			
		}
		
		// implements LCG using a = 101427, c = 321, m = 216. Xi+1 = (aXi + c) mod m
		else if(op == 2){
			
			double xi = seed;
			writeFile.write("Random number generated by LCG using a = 101427, c = 321, m = 2^16:\n");
			for(int i = 0; i < loops; i++){
				
				xi = (101427*seed + 321) % Math.pow(2,16);
				randNums[i] = xi / Math.pow(2,16);
				seed = xi;
				writeFile.write(randNums[i] + "\n");
				
			}
			
		}
		
		// implements LCG using a = 65539, c = 0, m = 231
		else if(op == 3){
			
			double xi = seed;
			writeFile.write("Random number generated by LCG using a = 65539, c = 0, m = 2^31:\n");
			for(int i = 0; i < loops; i++){
				
				xi = (65539*seed + 0) % Math.pow(2,31);
				seed = xi;
				randNums[i] = xi / Math.pow(2,31);
				writeFile.write(randNums[i] + "\n");
				
			}
			
		}
		
		// if op is anything other than 1, 2, or 3, show help menu
		else{
			System.out.println("\nInvalid operation input. Please choose your operation followed by how many numbers to generate (i.e., 1 30 generates 30 random numbers using Java's random number generator.)\n1. Generate random numbers using Java\n2. Generate random numbers using LCG default configuration\n3. Generate random numbers using LCG RANDU configuration\nProgram shutting down...");
			System.exit(0);
		}
		
		// Ask the user for input on the desired statistical test
		System.out.println("\nPlease select a test to use:\n1. Chi-Square Frequency Test\n2. Kolmogorov-Smirnov Test\n3. Runs Test\n4. Autocorrelations Test\n");
		int test = scan.nextInt();
		
		// Conduct the Chi-Squared Frequency Test
		if(test == 1){
			
			// calculate the expected values for each intervals and fill in the observed value frequency for each intervals
			double[] expected = new double[10];
			double[] observed = new double[10];
			
			// initialize the array of expected value:
			for(int i = 0; i < expected.length; i++){
				
				expected[i] = maxLoops/10;
				
			}
			
			for(int i = 0; i < maxLoops; i++){
				
				if(randNums[i]<0.1){
					observed[0]++;
				}
				
				if(randNums[i]>=0.1 && randNums[i]<0.2){
					observed[1]++;
				}
				
				if(randNums[i]>=0.2 && randNums[i]<0.3){
					observed[2]++;
				}
				
				if(randNums[i]>=0.3 && randNums[i]<0.4){
					observed[3]++;
				}
				
				if(randNums[i]>=0.4 && randNums[i]<0.5){
					observed[4]++;
				}
				
				if(randNums[i]>=0.5 && randNums[i]<0.6){
					observed[5]++;
				}
				
				if(randNums[i]>=0.6 && randNums[i]<0.7){
					observed[6]++;
				}
				
				if(randNums[i]>=0.7 && randNums[i]<0.8){
					observed[7]++;
				}
				
				if(randNums[i]>=0.8 && randNums[i]<0.9){
					observed[8]++;
				}
				
				if(randNums[i]>=0.9 && randNums[i]<1){
					observed[9]++;
				}
				
			}
			
			// calculate the Chi-Squared value
			double sum = 0;
			for(int i = 0; i < 10; i++){
				
				double val = (observed[i] - expected[i]) * (observed[i] - expected[i]);
				sum += val/expected[i];
				
			}
			
			writeFile.write("\nChi-Squared value = " + sum + "\n");
			
		}
		
		// Conduct the Kolmogorov-Smirnov Test
		else if(test == 2){
			
			// determine the size of the test array for K-S test
			double arrMax = (loops < 100) ? loops : 100;
			
			// calculate the observed value frequency for each intervals
			double[] observed = new double[10];
			
			for(int i = 0; i < arrMax; i++){
				
				if(randNums[i]<=0.1){
					observed[0]++;
				}
				
				if(randNums[i]<=0.2){
					observed[1]++;
				}
				
				if(randNums[i]<=0.3){
					observed[2]++;
				}
				
				if(randNums[i]<=0.4){
					observed[3]++;
				}
				
				if(randNums[i]<=0.5){
					observed[4]++;
				}
				
				if(randNums[i]<=0.6){
					observed[5]++;
				}
				
				if(randNums[i]<=0.7){
					observed[6]++;
				}
				
				if(randNums[i]<=0.8){
					observed[7]++;
				}
				
				if(randNums[i]<=0.9){
					observed[8]++;
				}
				
				if(randNums[i]<1){
					observed[9]++;
				}
				
			}
			
			// find the max absolute value of Sn(x)-F(x)
			double topMax = 0;
			
			for(int i = 0; i < observed.length; i++){
				double f = (double)(i+1)*0.1;
				double d = observed[i]/arrMax;
				double tempVal = Math.abs(d - f);
				topMax = Math.max(topMax, tempVal);
			}
			
			writeFile.write("\nThe D value is: " + topMax + "\n");
			
		}
		
		// Conduct the Runs Test
		else if(test == 3){
			
			double runs = 0;
			double prevRun = 0;
			double currRun = 0;
			double aboveMean = 0;
			double belowMean = 0;
			double mean = 0;
			double sum = 0;
			
			// Calculate the sum and the mean of the randomly generated numbers
			for(int i = 0; i < maxLoops; i++){
				sum +=randNums[i];
			}
			mean = sum/maxLoops;
			//System.out.println(mean);
			
			// Loop through the randomly generated numbers and count how many are above the mean, below the mean, and number of new runs
			for(int i = 0; i < maxLoops; i++){
				if(randNums[i] < mean){
					currRun = -1;
					belowMean++;
				}
				else{
					currRun = 1;
					aboveMean++;
				}
				
				// if prevRun is different compared to currRun, then increment runs and set prevRun to currRun
				if(currRun != prevRun){
					runs++;
					prevRun = currRun;
				}
				
			}
			
			writeFile.write("\nNumbers above the mean count: " + aboveMean + "\nNumbers below the mean count: " + belowMean + "\n");
			
			// Calculate the expected mu and variance respectively
			double expectedMu = (2*aboveMean*belowMean)/(aboveMean+belowMean) + 1;
			writeFile.write("Expected mu: " + expectedMu + "\n");
			
			double expectedVar = (expectedMu-1)*(expectedMu-2)/(aboveMean+belowMean-1);
			writeFile.write("Expected variance: " + expectedVar + "\n");
			
			// Calculate the standard deviation
			double expectedSD = Math.sqrt(expectedVar);
			writeFile.write("Expected standard deviation: " + expectedSD + "\n");
			
			// Calculate the Z score, which is (observed runs - expected runs) / expected standard deviation
			double zScore = (runs - expectedMu)/expectedSD;
			writeFile.write("Z score: " + zScore + "\n");
			
		}
		
		// Conduct the Autocorrelation Test
		else if(test == 4){
			
			// Declare our fixed gap size array containing all of the gap sizes we'll be using for our test (l parameter in note)
			int[] gapSize = {2, 3, 5, 50};
			
			// Define our starting point (i parameter in note)
			int startingPoint = 1;
			
			writeFile.write("\n");
			// Calculate the maximum rounds of intervals we get (i+(M+1)l <= N, where N is the number of numbers generated)
			for(int i = 0; i < gapSize.length; i++){
				
				int maxInterval = (maxLoops - gapSize[i])/gapSize[i];
				maxInterval = maxInterval - 1;
				
				// Use a loop to calculate the sum of R(i+km)*R((k+1)m) from 0 to maxInterval
				double summation = 0;
				for(int k = 0; k <= maxInterval; k++){
					
					summation += randNums[startingPoint + k * gapSize[i]] * randNums[(k + 1) * gapSize[i]];
					
				}
				
				// Calculate p-hat for this iteration of gap size
				double pHat = ((double)1 / (maxInterval + 1)) * summation - 0.25;
				
				// Calculate sigma-hat for this iteration of gap size
				double sigmaHat = Math.sqrt(13 * maxInterval + 7) / (12 * (maxInterval + 1));
				
				writeFile.write("P-hat for gap size " + gapSize[i] + " is: " + pHat + "\n");
				writeFile.write("Sigma-hat for gap size " + gapSize[i] + " is: " + sigmaHat + "\n");
				
				// Calculate the Z value
				double zVal = (pHat-0)/sigmaHat;
				writeFile.write("Z Score for gap size " + gapSize[i] + " is: " + zVal + "\n");
				writeFile.write("\n");
				
			}
			
		}
		
		else{
			
			System.out.println("\nInvalid input, please select a test to use:\n1. Chi-Square Frequency Test\n2. Kolmogorov-Smirnov Test\n3. Runs Test\n4. Autocorrelations Test\nSystem terminating...");
			System.exit(0);
			
		}
		
		System.out.println("See " + fileName + ".txt for result output.");
		writeFile.close();
		
	}
	
}