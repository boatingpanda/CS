/*
	FUSE: Filesystem in Userspace
	Copyright (C) 2001-2007  Miklos Szeredi <miklos@szeredi.hu>

	This program can be distributed under the terms of the GNU GPL.
	See the file COPYING.

*/

#define	FUSE_USE_VERSION 26

#include <fuse.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>

//size of a disk block
#define	BLOCK_SIZE 512

//we'll use 8.3 filenames
#define	MAX_FILENAME 8
#define	MAX_EXTENSION 3

//How many files can there be in one directory?
#define	MAX_FILES_IN_DIR (BLOCK_SIZE - (MAX_FILENAME + 1) - sizeof(int)) / \
	((MAX_FILENAME + 1) + (MAX_EXTENSION + 1) + sizeof(size_t) + sizeof(long))

//How much data can one block hold?
#define	MAX_DATA_IN_BLOCK BLOCK_SIZE

//How many pointers in an inode?
#define NUM_POINTERS_IN_INODE ((BLOCK_SIZE - sizeof(unsigned int) - sizeof(unsigned long)) / sizeof(unsigned long))

struct cs1550_directory_entry
{
	char dname[MAX_FILENAME	+ 1];	//the directory name (plus space for a nul)
	int nFiles;			//How many files are in this directory. 
					//Needs to be less than MAX_FILES_IN_DIR

	struct cs1550_file_directory
	{
		char fname[MAX_FILENAME + 1];	//filename (plus space for nul)
		char fext[MAX_EXTENSION + 1];	//extension (plus space for nul)
		size_t fsize;			//file size
		long nStartBlock;		//where the first block is on disk
	} files[MAX_FILES_IN_DIR];		//There is an array of these
};

typedef struct cs1550_directory_entry cs1550_directory_entry;
typedef struct cs1550_file_directory cs1550_file_directory;

struct cs1550_disk_block
{
	//And all of the space in the block can be used for actual data
	//storage.
	char data[MAX_DATA_IN_BLOCK];
};

typedef struct cs1550_disk_block cs1550_disk_block;

// grab the directory entry in the .directories file, returns 1 if successful, 0 if not successful
static int getSpecificDir(cs1550_directory_entry *dirPath, int i){
	
	int success = 0;
	int seek;
	int read;
	FILE *dirFile = fopen(".directories", "rb");
	
	seek = fseek(dirFile, sizeof(cs1550_directory_entry) * i, SEEK_SET);
	// if seek is -1, we're not successful, success is 0
	if(seek == -1){
		success = 0;
	}
	
	read = fread(dirPath, sizeof(cs1550_directory_entry), 1, dirFile);
	// if read is successful, set success to 1
	if(read == 1){
		success = 1;
	}
	
	fclose(dirFile);
	return success;
}

// returns the index of a directory in the .directory file, return -1 if not successful
static int findDirectory(char *dirName){
	
	cs1550_directory_entry tempEntry;
	int ret = -1;
	
	int i = 0;
	while(getSpecificDir(&tempEntry, i) != 0 && ret == -1){
		// if we have a match in directory name, set ret to index i
		if(strcmp(tempEntry.dname, dirName) == 0){
			ret = i;
		}
		i++;
	}
	
	return ret;
	
}

// find the matching file name and extension in a subdirectory, -1 means file not found
static int findFile(cs1550_directory_entry *dir, char *fileName, char *extension){
	
	int ret = -1;
	
	int i;
	for(i = 0; i < dir->nFiles; i++){
		
		if(strcmp(fileName, dir->files[i].fname) == 0 && strcmp(extension, dir->files[i].fext) == 0){
			ret = i;
		}
		
	}
	
	return ret;
	
}

// set bit i to allocated in disk, indicating the disk block at i * 512 has been allocated
void setAllocated(int i){
	
	FILE *diskFile = fopen(".disk", "rb+");
	
	// find out which byte we have to work with
	int targetByte = i / 8;
	
	// find which bit we have to work with in the byte
	i = i % 8;
	
	// set the target bit in target by to 1
	fseek(diskFile, targetByte, SEEK_SET);
	unsigned char bitSet = 0;
	fread(&bitSet, sizeof(unsigned char), 1, diskFile);
	unsigned char operation = 1 << i;
	bitSet = bitSet | operation;
	
	// write the information to disk
	fseek(diskFile, -1, SEEK_CUR);
	fwrite(&bitSet, sizeof(unsigned char), 1, diskFile);
	fclose(diskFile);
	
}

// create a bit map on disk image that manages the block allocation
void createBitmap(){
	
	FILE *diskFile = fopen(".disk", "rb+");
	
	char start = 0;
	// check if a bitmap exists already by checking the byte at the very beginning
	fseek(diskFile, 0, SEEK_SET);
	fread(&start, sizeof(char), 1, diskFile);
	
	// if start is 1, then we have a bitmap and we don't have to worry. otherwise we have to make it
	if(start == 0){
		
		// calculate how big of a bitmap we'll need
		// first find out how many blocks are there on disk, which is the same as the number of bits needed in a bitmap
		fseek(diskFile, 0, SEEK_END);
		int numBytes = ftell(diskFile);
		int blocksOnDisk = numBytes / 512;
		
		// now find out how many blocks are required to store those bits
		// convert the bits to bytes
		int bitsToBytes = blocksOnDisk / 8;
		if(blocksOnDisk % 8 != 0){
			bitsToBytes++;
		}
		
		// calculate number of blocks needed to store these bytes
		int blocksForBitmap = bitsToBytes / 512;
		if(bitsToBytes % 512 != 0){
			blocksForBitmap++;
		}
		
		// now write the bitmap to disk
		int i;
		for(i = 0; i < blocksForBitmap; i++){
			
			setAllocated(i);
			
		}
		
	}
	fclose(diskFile);
	
}

// allocate n blocks of disk space starting from start
void allocateBlock(int start, int n){
	
	int i;
	for(i = start; i < start + n; i++){
		setAllocated(i);
	}
	
}

// set bit i to unallocated in disk
void setUnallocated(int i){
	
	FILE *diskFile = fopen(".disk", "rb+");
	
	// find out which byte we have to work with
	int targetByte = i / 8;
	
	// find which bit we have to work with in the byte
	i = i % 8;
	
	// set the target bit in target by to 0
	fseek(diskFile, targetByte, SEEK_SET);
	unsigned char bitSet = 0;
	fread(&bitSet, sizeof(unsigned char), 1, diskFile);
	unsigned char operation = 1 << i;
	operation = ~operation;
	bitSet = bitSet & operation;
	
	// write the information to disk
	fseek(diskFile, -1, SEEK_CUR);
	fwrite(&bitSet, sizeof(unsigned char), 1, diskFile);
	fclose(diskFile);
	
}

// unallocate n blocks of disk space starting from start
void unallocateBlock(int start, int n){
	
	int i;
	for(i = start; i < start + n; i++){
		setUnallocated(i);
	}
	
}

// find the first fitting free space based on the number of blocks needed
static int findFreeSpace(int numBlocksNeeded){
	
	FILE *diskFile = fopen(".disk", "r");
	int freeSpaceStart = -1;
	int contiguousFS = 0;
	
	// find the end of the file to get the number of possible blocks on disk, which is the number of bits we need
	fseek(diskFile, 0, SEEK_END);
	int numBytesTotal = ftell(diskFile);
	int blocksOnDisk = numBytesTotal / 512 - 1;
	fseek(diskFile, 0, SEEK_SET);
	
	// loop through each bit in the bitmap until we find an empty segment that fits our file
	int i;
	for(i = 0; i < blocksOnDisk; i++){
		
		int targetByte = i / 8;
		int targetBit = i % 8;
		
		// go to the target byte and check if the target bit is 1 or 0
		fseek(diskFile, targetByte, SEEK_SET);
		unsigned char curBit = 0;
		fread(&curBit, sizeof(unsigned char), 1, diskFile);
		unsigned char operation = 1 << targetBit;
		unsigned char result = targetByte & operation;
		result = result >> targetBit;
		
		// if result is a 0
		if(result == 0){
			
			// incrememt contiguous free space increments by 1
			contiguousFS++;
			
			// if this is the first time we see a 0 or right after we saw a 1, set free space to start at i
			if(freeSpaceStart == -1){
				freeSpaceStart = i;
			}
			
			// if our length of free space matches the size we need, break to return free space start
			if(contiguousFS == numBlocksNeeded){
				break;
			}
			
		}
		
		// if result is a 1, reset our contiguous free space counter and our start index
		else{
			
			freeSpaceStart = -1;
			contiguousFS = 0;
			
		}
		
	}
	
	fclose(diskFile);
	return freeSpaceStart;
	
}

/*
 * Called whenever the system wants to know the file attributes, including
 * simply whether the file exists or not. 
 *
 * man -s 2 stat will show the fields of a stat structure
 */
static int cs1550_getattr(const char *path, struct stat *stbuf)
{
	int res = -ENOENT;

	memset(stbuf, 0, sizeof(struct stat));
	
	// create the file if it doesn't exist, just to make sure, then closes it
	FILE *temp = fopen(".directories", "a");
	fclose(temp);
	
	// we'll use these char arrays to hold the target directory and file, if they exists. otherwise they will be left at default null
	char directory[MAX_FILENAME + 1];
	memset(directory, 0, MAX_FILENAME + 1);
	char fileName[MAX_FILENAME + 1];
	memset(fileName, 0, MAX_FILENAME + 1);
	char extension[MAX_EXTENSION + 1];
	memset(extension, 0, MAX_EXTENSION + 1);
	sscanf(path, "/%[^/]/%[^.].%s", directory, fileName, extension);
   
	//is path the root dir
	if (strcmp(path, "/") == 0) {
		stbuf->st_mode = S_IFDIR | 0755;
		stbuf->st_nlink = 2;
		res = 0; // no error
	}
	else {
		
		cs1550_directory_entry currentDir;
		int dirIndex = findDirectory(directory);
		
		// if the directory index is not -1, then the path might be a subdirectory
		if(dirIndex != -1){
			
			// grab the directory to see if it's a file or directory
			getSpecificDir(&currentDir, dirIndex);
			
			// if file name field is empty, it's a subdirectory
			if(strlen(fileName) == 0){
				stbuf->st_mode = S_IFDIR | 0755;
				stbuf->st_nlink = 2;
				res = 0; // no error
			}
			
			// otherwise it's a file
			else{
				
				int findF = findFile(&currentDir, fileName, extension);
                if(findF != -1){
                    stbuf->st_mode = S_IFREG | 0666; 
                    stbuf->st_nlink = 1;
                    stbuf->st_size = currentDir.files[findF].fsize;
                    stbuf->st_blksize = 512;
                    stbuf->st_blocks = currentDir.files[findF].fsize / 512;
                    // if modding the file size by 512 doesn't equal to 0, there's another block holding the extra information.
                    if(currentDir.files[findF].fsize % 512 != 0){
                    	stbuf->st_blocks++;
                    }
                    res = 0; //no error
                }
				
			}
			
		}
		
		// otherwise this file path doesn't exist
		else{
			res = -ENOENT;
		}
		
	}
	return res;
}

/* 
 * Called whenever the contents of a directory are desired. Could be from an 'ls'
 * or could even be when a user hits TAB to do autocompletion
 */
static int cs1550_readdir(const char *path, void *buf, fuse_fill_dir_t filler,
			 off_t offset, struct fuse_file_info *fi)
{
	//Since we're building with -Wall (all warnings reported) we need
	//to "use" every parameter, so let's just cast them to void to
	//satisfy the compiler
	(void) offset;
	(void) fi;
	
	char directory[MAX_FILENAME + 1], fileName[MAX_FILENAME + 1], extension[MAX_EXTENSION + 1];
	memset(directory, 0, MAX_FILENAME + 1);
	memset(fileName, 0, MAX_FILENAME + 1);
	memset(extension, 0, MAX_FILENAME + 1);
	sscanf(path, "/%[^/]/%[^.].%s", directory, fileName, extension);
	struct cs1550_directory_entry currentDir;
	
	//This line assumes we have no subdirectories, need to change
	// changed to assume we're reading the root directory
	if (strcmp(path, "/") == 0){
		//the filler function allows us to add entries to the listing
		//read the fuse.h file for a description (in the ../include dir)
		filler(buf, ".", NULL, 0);
		filler(buf, "..", NULL, 0);
		int i = 0;
		
		while(getSpecificDir(&currentDir, i) != 0){
			
			filler(buf, currentDir.dname, NULL, 0);
			i++;
			
		}
		
	}
	
	// otherwise go through and check to make sure it's a valid directory
	else{
		
		int dirIndex = findDirectory(directory);
		
		// if we can find this directory, print out all of its file's name and extension
		if(dirIndex != -1){
			
			int i;
			getSpecificDir(&currentDir, dirIndex);
			filler(buf, ".", NULL, 0);
			filler(buf, "..", NULL, 0);
			char fullName[MAX_FILENAME + MAX_EXTENSION + 2];
			
			for(i = 0; i < currentDir.nFiles; i++){
				
				strcpy(fullName, currentDir.files[i].fname);
				strcat(fullName, ".");
				strcat(fullName, currentDir.files[i].fext);
				
				/*
				//add the user stuff (subdirs or files)
				//the +1 skips the leading '/' on the filenames
				filler(buf, newpath + 1, NULL, 0);
				*/
				filler(buf, fullName, NULL, 0);
				
			}
			
		}
		
		else{
			return -ENOENT;
		}
		
	}
	
	return 0;
}

/* 
 * Creates a directory. We can ignore mode since we're not dealing with
 * permissions, as long as getattr returns appropriate ones for us.
 */
static int cs1550_mkdir(const char *path, mode_t mode)
{
	(void) path;
	(void) mode;
	
	char directory[MAX_FILENAME + 1], fileName[MAX_FILENAME + 1], extension[MAX_EXTENSION + 1];
	memset(directory, 0, MAX_FILENAME + 1);
	memset(fileName, 0, MAX_FILENAME + 1);
	memset(extension, 0, MAX_FILENAME + 1);
	sscanf(path, "/%[^/]/%[^.].%s", directory, fileName, extension);
	
	// check if the directory name is too long
	if(strlen(directory) > MAX_FILENAME){
		return -ENAMETOOLONG;
	}
	
	// check if the directory is not under the root directory, return -EPERM
	if(strlen(fileName) > 0){
		return -EPERM;
	}
	
	//check if the directory exists
	if(findDirectory(directory) != -1){
		
		return -EEXIST;
	}
	
	// otherwise the directory doesn't exist
	FILE *dirFile = fopen(".directories", "ab");
	struct cs1550_directory_entry newDir;
	memset(&newDir, 0, sizeof(cs1550_directory_entry));
	strcpy(newDir.dname, directory);
	newDir.nFiles = 0;
	fwrite(&newDir, sizeof(cs1550_directory_entry), 1, dirFile);
	fclose(dirFile);
	
	return 0;
}

/* 
 * Removes a directory.
 */
static int cs1550_rmdir(const char *path)
{
	(void) path;
    return 0;
}

/* 
 * Does the actual creation of a file. Mode and dev can be ignored.
 *
 */
static int cs1550_mknod(const char *path, mode_t mode, dev_t dev)
{
	(void) mode;
	(void) dev;
	(void) path;
	
	char directory[MAX_FILENAME + 1], fileName[MAX_FILENAME + 1], extension[MAX_EXTENSION + 1];
	memset(directory, 0, MAX_FILENAME + 1);
	memset(fileName, 0, MAX_FILENAME + 1);
	memset(extension, 0, MAX_FILENAME + 1);
	sscanf(path, "/%[^/]/%[^.].%s", directory, fileName, extension);
	struct cs1550_directory_entry currentDir;
	printf("directory: %s\n", directory);
	printf("file name: %s\n", fileName);
	printf("extension: %s\n", extension);
	// check to make sure the file isn't being created in root
	if(strlen(fileName) == 0){
		return -EPERM;
	}
	
	// check if file name or extension is too long
	if(strlen(fileName) > MAX_FILENAME || strlen(extension) > MAX_EXTENSION+1){
		return -ENAMETOOLONG;
	}
	
	// make sure the directory exist and the file exist in the directory
	int dirIndex = findDirectory(directory);
	printf("directory index: %d\n", dirIndex);
	getSpecificDir(&currentDir, dirIndex);
	int fileIndex = findFile(&currentDir, fileName, extension);
	printf("file index: %d\n", fileIndex);
	// if file doesn't exist, create it
	if(fileIndex == -1){
		
		int newFileIndex = currentDir.nFiles;
		if(newFileIndex > MAX_FILES_IN_DIR){
			return -EPERM;
		}
		
		strcpy(currentDir.files[newFileIndex].fname, fileName);
		strcpy(currentDir.files[newFileIndex].fext, extension);
		currentDir.files[newFileIndex].fsize = 0;
		currentDir.files[newFileIndex].nStartBlock = -1;
		currentDir.nFiles++;
		
		FILE *dirFile = fopen(".directories", "rb+");
		fseek(dirFile, sizeof(cs1550_directory_entry) * dirIndex, SEEK_SET);
		fwrite(&currentDir, sizeof(cs1550_directory_entry), 1, dirFile);
		fclose(dirFile);
		
	}
	
	// otherwise it does exist
	else{
		return -EEXIST;
	}
	
	
	return 0;
}

/*
 * Deletes a file
 */
static int cs1550_unlink(const char *path)
{
    (void) path;
    char directory[MAX_FILENAME + 1], fileName[MAX_FILENAME + 1], extension[MAX_EXTENSION + 1];
	memset(directory, 0, MAX_FILENAME + 1);
	memset(fileName, 0, MAX_FILENAME + 1);
	memset(extension, 0, MAX_FILENAME + 1);
	sscanf(path, "/%[^/]/%[^.].%s", directory, fileName, extension);
	struct cs1550_directory_entry currentDir;
    
    // remove the file from directory, remember to change the number of files
    // if path is a directory, return -EISDIR
    if(strlen(fileName) == 0){
    	return -EISDIR;
    }
    
    // if file is not found, return -ENOENT
    int dirIndex = findDirectory(directory);
	getSpecificDir(&currentDir, dirIndex);
	int fileIndex = findFile(&currentDir, fileName, extension);
	if(fileIndex == -1){
		return -ENOENT;
	}
	else{
		
		int startBlock = currentDir.files[fileIndex].nStartBlock;
		int fileSize = currentDir.files[fileIndex].fsize;
		
		// remove the file from .directories
		currentDir.files[fileIndex] = currentDir.files[currentDir.nFiles-1];
		currentDir.nFiles = currentDir.nFiles - 1;
		FILE *dirFile = fopen(".directories", "r+");
		fwrite(&currentDir, sizeof(cs1550_directory_entry), 1, dirFile);
		fclose(dirFile);
		
		// reset the bitmap where this file is to 0
		int numBlocks = fileSize / BLOCK_SIZE;
		if(fileSize % BLOCK_SIZE != 0){
			numBlocks++;
		}
		unallocateBlock(startBlock, numBlocks);
		
	}
    
    return 0;
}

/* 
 * Read size bytes from file into buf starting from offset
 *
 */
static int cs1550_read(const char *path, char *buf, size_t size, off_t offset,
			  struct fuse_file_info *fi)
{
	(void) buf;
	(void) offset;
	(void) fi;
	(void) path;
	
	char directory[MAX_FILENAME + 1], fileName[MAX_FILENAME + 1], extension[MAX_EXTENSION + 1];
	memset(directory, 0, MAX_FILENAME + 1);
	memset(fileName, 0, MAX_FILENAME + 1);
	memset(extension, 0, MAX_FILENAME + 1);
	sscanf(path, "/%[^/]/%[^.].%s", directory, fileName, extension);
	struct cs1550_directory_entry currentDir;
	int readIn = 0;

	//check to make sure path exists
	int dirIndex = findDirectory(directory);
	getSpecificDir(&currentDir, dirIndex);
	int fileIndex = findFile(&currentDir, fileName, extension);
	if(dirIndex != -1 && fileIndex != -1){
		
		cs1550_file_directory currentFile = currentDir.files[fileIndex];
		//check that size is > 0
		if(size > 0){
			
			//check that offset is <= to the file size
			if(offset <= currentFile.fsize){
				
				//find out where we're reading from
				int readStart = currentFile.nStartBlock;
				printf("start block at: %d\n", readStart);
				
				// find out where to stop reading
				int readUpto = currentFile.fsize - offset;
				// make sure we don't read more than we need to
				if(readUpto < size){
					size = readUpto;
				}
				
				// read the information
				FILE *diskFile = fopen(".disk", "rb");
				fseek(diskFile, readStart * BLOCK_SIZE + offset, SEEK_SET);
				readIn = fread(buf, 1, size, diskFile);
				fclose(diskFile);
				
			}
			else{
				return -EPERM;
			}
			
		}
		// otherwise return size is 0
		else{
			return 0;
		}
		
	}
	// otherwise path doesn't exist
	else{
		return -ENOENT;
	}

//	size = 0;

	return readIn;
}

/* 
 * Write size bytes from buf into file starting from offset
 *
 */
static int cs1550_write(const char *path, const char *buf, size_t size, 
			  off_t offset, struct fuse_file_info *fi)
{
	(void) buf;
	(void) offset;
	(void) fi;
	(void) path;
	
	//create a bitmap on the disk image to manage which block on disk is allocated or unallocated
	createBitmap();
	
	char directory[MAX_FILENAME + 1], fileName[MAX_FILENAME + 1], extension[MAX_EXTENSION + 1];
	memset(directory, 0, MAX_FILENAME + 1);
	memset(fileName, 0, MAX_FILENAME + 1);
	memset(extension, 0, MAX_FILENAME + 1);
	sscanf(path, "/%[^/]/%[^.].%s", directory, fileName, extension);
	cs1550_directory_entry currentDir;
	
	//check to make sure path exists
	int dirIndex = findDirectory(directory);
	getSpecificDir(&currentDir, dirIndex);
	int fileIndex = findFile(&currentDir, fileName, extension);
	if(dirIndex != -1 && fileIndex != -1){
		
		cs1550_file_directory currentFile = currentDir.files[fileIndex];
		//check that size is > 0 or is 0 and start block is -1
		// if it's 0 and -1, find new blocks to put the information and write to .disk
		if(size > 0){
			
			//check that offset is <= to the file size
			if(offset <= currentFile.fsize){
				
				//write data
				int newsize = offset + size;
				int blocksNeeded = newsize / 512;
				if(newsize % 512 != 0){
					blocksNeeded++;
				}
				
				// calculate number of blocks we have currently
				int currentBlocks = currentFile.fsize / 512;
				if(currentFile.fsize % 512 != 0){
					currentBlocks++;
				}
				
				// if we require more blocks than we have, copy everything out to our temporary buffer and unallocate that disk space
				if(blocksNeeded > currentBlocks){
					
					// temporary buffer to hold stuff from the disk
					char *tempBuf = malloc(BLOCK_SIZE * currentBlocks);
					FILE *tempFile = fopen(".disk", "r");
					fread(&tempBuf, sizeof(BLOCK_SIZE), currentBlocks, tempFile);
					fclose(tempFile);
					
					// if nStartBlock isn't -1, unallocate the blocks, if it's -1, then we don't have to do anything
					if(currentFile.nStartBlock != -1){
						unallocateBlock(currentFile.nStartBlock, currentBlocks);
					}
					
					// find free space to put the files
					int newFreeSpace = findFreeSpace(blocksNeeded);
					printf("start block at: %d\n", newFreeSpace);
					allocateBlock(newFreeSpace, blocksNeeded);
					
					// write to the disk
					FILE *diskFile = fopen(".disk", "rb+");
					fseek(diskFile, newFreeSpace * BLOCK_SIZE, SEEK_SET);
					fwrite(tempBuf, sizeof(tempBuf), 1, diskFile);
					fseek(diskFile, newFreeSpace * BLOCK_SIZE + offset, SEEK_SET);
					fwrite(buf, size, 1, diskFile);
					fclose(diskFile);
					
					// change the information in .directories
					currentDir.files[fileIndex].nStartBlock = newFreeSpace;
					currentDir.files[fileIndex].fsize = newsize;
					FILE *dirFile = fopen(".directories", "rb+");
					fseek(dirFile, sizeof(cs1550_directory_entry) * dirIndex, SEEK_SET);
					fwrite(&currentDir, sizeof(cs1550_directory_entry), 1, dirFile);
					fclose(dirFile);
					
				}
				
				// otherwise just write to the offset
				else{
					
					FILE *diskFile = fopen(".disk", "rb+");
					fseek(diskFile, currentFile.nStartBlock * BLOCK_SIZE + offset, SEEK_SET);
					fwrite(buf, size, 1, diskFile);
					fclose(diskFile);
					
					// change the information in .directories
					currentDir.files[fileIndex].fsize = newsize;
					FILE *dirFile = fopen(".directories", "rb+");
					fseek(dirFile, sizeof(cs1550_directory_entry) * dirIndex, SEEK_SET);
					fwrite(&currentDir, sizeof(cs1550_directory_entry), 1, dirFile);
					fclose(dirFile);
				}
				
			}
			// otherwise return -EFBIG
			else{
				return -EFBIG;
			}
			
		}
		// otherwise the size is 0
		else{
			return 0;
		}
		
	}
	// otherwise the file doesn't exist
	else{
		return -ENOENT;
	}

	return size;
}

/******************************************************************************
 *
 *  DO NOT MODIFY ANYTHING BELOW THIS LINE
 *
 *****************************************************************************/

/*
 * truncate is called when a new file is created (with a 0 size) or when an
 * existing file is made shorter. We're not handling deleting files or 
 * truncating existing ones, so all we need to do here is to initialize
 * the appropriate directory entry.
 *
 */
static int cs1550_truncate(const char *path, off_t size)
{
	(void) path;
	(void) size;

    return 0;
}


/* 
 * Called when we open a file
 *
 */
static int cs1550_open(const char *path, struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;
    /*
        //if we can't find the desired file, return an error
        return -ENOENT;
    */

    //It's not really necessary for this project to anything in open

    /* We're not going to worry about permissions for this project, but 
	   if we were and we don't have them to the file we should return an error

        return -EACCES;
    */

    return 0; //success!
}

/*
 * Called when close is called on a file descriptor, but because it might
 * have been dup'ed, this isn't a guarantee we won't ever need the file 
 * again. For us, return success simply to avoid the unimplemented error
 * in the debug log.
 */
static int cs1550_flush (const char *path , struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;

	return 0; //success!
}


//register our new functions as the implementations of the syscalls
static struct fuse_operations hello_oper = {
    .getattr	= cs1550_getattr,
    .readdir	= cs1550_readdir,
    .mkdir	= cs1550_mkdir,
	.rmdir = cs1550_rmdir,
    .read	= cs1550_read,
    .write	= cs1550_write,
	.mknod	= cs1550_mknod,
	.unlink = cs1550_unlink,
	.truncate = cs1550_truncate,
	.flush = cs1550_flush,
	.open	= cs1550_open,
};

//Don't change this.
int main(int argc, char *argv[])
{
	return fuse_main(argc, argv, &hello_oper, NULL);
}
