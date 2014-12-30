import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray{

	char[] suffixArray;
	int[] sIndex;
	
	public CircularSuffixArray(String s){  // circular suffix array of s
		
		final int lengthOfString = s.length();
		suffixArray = s.toCharArray();
		Integer[] tempIndex = new Integer[lengthOfString];
		sIndex = new int[lengthOfString];
		
		for(int i = 0; i < suffixArray.length; i++){
			tempIndex[i] = i;
		}
		
		Arrays.sort(tempIndex, new Comparator<Integer>(){ // sort the suffix arrays using arrays.sort, but uses custom compare method
			
			public int compare(Integer i1, Integer i2){
				
				int result = suffixArray[i1] - suffixArray[i2]; // compare a character from one index in suffix array with another character from another index
				for(int i = 1; i < lengthOfString; i++){
					
					if(result != 0){ // if the result is not 0, then we know which character is smaller/larger, then break out of loop
						break;
					}
					else{ // otherwise keep comparing
						result = suffixArray[(i1+i)%lengthOfString]-suffixArray[(i2+i)%lengthOfString]; // this is circular since we're using mod division
					}
					
				}
				
				return result;
				
			}
			
		});
		
		for(int i = 0; i < lengthOfString; i++){
			sIndex[i] = tempIndex[i];
		}
		
	}
	
	public int length(){                   // length of s
		
		return sIndex.length;
		
	}
	
	public int index(int i){               // returns index of ith sorted suffix - 10 points
		
		return sIndex[i];
		
	}
	
}