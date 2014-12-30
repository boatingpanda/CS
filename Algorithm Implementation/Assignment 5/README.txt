Adaptive Huffman Tree test cases:
	1. input: a
	Output:[(1,255,) (1,254,a) (0,253,) ]
			size = 3

	 
	2. input: aa
	Output:[(2,255,) (2,254,a) (0,253,) ]
			size = 3

	
	3. input: aab
	Output:[(3,255,) (2,254,a) (1,253,) (1,252,b) (0,251,) ]
			size = 5

	
	4. input: baa
	Output:[(3,255,) (2,254,a) (1,253,) (1,252,b) (0,251,) ]
			size = 5

	
	5: input: class2.bin file content
	Output: [(8,255,) (5,254,) (3,253,a) (3,252,) (2,251,d) (2,250,r) (1,249,) (1,248,v) (0,247,) ]
			size = 9

Compress test cases:
	1. input: a
	Output:61
			8 bits
	
	2. input: aa
	Output: 61 80
			16 bits
	
	3. input: aab
	Output: 61 98 80
			24 bits
	
	4: input: baa
	Output: 62 30 a0
			24 bits
	
	5. input: class2.bin file content
	Output: 61 9c 86 40 ec fc
			48 bits
