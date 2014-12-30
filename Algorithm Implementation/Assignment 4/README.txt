Burrows-Wheeler assignment

All of the input/output of this assignment uses BinaryStdIn/BinaryStdOut respectively. Therefore, unless the output can be displayed on screen as a character, everything else must use java HexDump in order to view.

Program has been tested with abra.txt and aesop.txt and didn't find any errors/bugs.

About Circular Suffix Array's sorting/comparing method:
	The circular suffix array uses the default Arrays.sort from java.util.Arrays, but uses a custom sort method which compares the suffixes character by character. Since each character has an int value, it's possible to compare them by doing char1 - char2 and stores the result as an integer. If the number is larger than 0, then char1 is larger than char2, 0 means the two are equal, and negative means char1 is smaller than char2. The for-loop is used to compare each suffix character by character until it finds two with a result that's non-zero, which means we've found which suffix is the larger/smaller suffix. The sorting is left to Arrays.sort.

About Burrows Wheeler encoding:
	Encoding is done by first creating a circular suffix array of the string passed into the encoding method, then turn it into a character array. Using a for loop, we can loop through the circular suffix array and see where the original string is by checking whether the index of a certain suffix at i is 0 or not, a 0 means that is the location of the original string. In the same loop, we can also find and create a character array of the last characters in the sorted suffix array. Since the index of the ith suffix is the first character of the sorted suffix, we can find the last character by subtracting 1 from the index (except for the 0th index, in which case we simply subtract 1 from the total length of the string).
	
About Burrows Wheeler decoding:
	Decoding is done by storing the input stream of the index where the original string is located and the string composed of the last characters of each suffix. Sorting of the string of suffix is done by converting it to a character array and sort using Arrays.sort(). By using a int array as dictionary of the extended ASCII characters (0-255), it's possible to limit the usage of space for decoding to N + 256, and it helps us to keep track of where we've seen each letters in the array while we decode to deal with repeating characters by keeping track of where we've last seen that character and add one to it so we can start right after where we left off.