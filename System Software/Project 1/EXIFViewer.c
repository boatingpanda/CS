#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct EXIFTag{
	unsigned short startOfFile;
	unsigned short app1Marker;
	unsigned short lengthOfApp1;
	unsigned char exifString[4];
	unsigned short NTerminator;
	unsigned char endianness[2];
	unsigned short vNumber;
	unsigned int startOfExif;
};

struct TIFFTag{
	unsigned short tagID;
	unsigned short dataType;
	unsigned int numOfDataItem;
	unsigned int valOrOffset;
};

int main(int argc, char *argv[]){
	struct EXIFTag eTag;
	struct TIFFTag tTag;
	unsigned short count = 0;
	unsigned int filePos = 0;
	
	printf("\n");
	if(argv[1] == NULL){
		printf("ERROR! NO FILE NAME!\n");
		exit(0);
	}
	FILE *inputFile = fopen(argv[1], "rb");
	fread(&eTag, 20, 1, inputFile);
	filePos = filePos + 20;
	if(strcmp(eTag.exifString, "Exif") != 0){
		printf("Error! Tag not found!");
		return 0;
	}
	
	if(strcmp(eTag.endianness, "MM") == 0){
		printf("Error! Big endian system is not supported!");
		return 0;
	}
	
	//printf("size of eTag is %d\n", sizeof(eTag));
	
	//printf("Start of file: %#x\n", eTag.startOfFile);
	//printf("App1 Marker: %#x\n", eTag.app1Marker);
	//printf("Lengh of App1: %#x\n", eTag.lengthOfApp1);
	//printf("Exif String: %s\n", eTag.exifString);
	//printf("NTerminator and zero: %#x\n", eTag.NTerminator);
	//printf("Endianness: %s\n", eTag.endianness);
	//printf("Version number: %d\n", eTag.vNumber);
	//printf("Offset: %i\n", eTag.startOfExif);
	
	fread(&count, 2, 1, inputFile);
	filePos = filePos + 2;
	
	int i = 0;
	
	while(i < count){
		fread(&tTag, 12, 1, inputFile);
		filePos = filePos + 12;
		
		if(tTag.tagID == 0x8769){
			fseek(inputFile, tTag.valOrOffset+12, SEEK_SET);
			filePos = tTag.valOrOffset + 12;
			//fread(&eTag, 20, 1, inputFile);
			//filePos = filePos + 20;
			fread(&count, 2, 1, inputFile);
			filePos = filePos + 2;
			int k = 0;
			
			while(k < count){
				fread(&tTag, 12, 1, inputFile);
				filePos = filePos + 12;
				
				if(tTag.tagID == 0xA002){
					printf("Width\t\t: %i pixels\n", tTag.valOrOffset);
					k++;
				}
				
				else if(tTag.tagID == 0xA003){
					printf("Height\t\t: %i pixels\n", tTag.valOrOffset);
					k++;
				}
				
				else if(tTag.tagID == 0x8827){
					printf("ISO\t\t: ISO %i\n", tTag.valOrOffset);
					k++;
				}
				
				else if(tTag.tagID == 0x829A){
					unsigned int val = 0;
					fseek(inputFile, tTag.valOrOffset + 12, SEEK_SET);
					fread(&val, 1, tTag.numOfDataItem, inputFile);
					fseek(inputFile, filePos, SEEK_SET);
					unsigned int val2 = 0;
					fseek(inputFile, tTag.valOrOffset + 12 + 4, SEEK_SET);
					fread(&val2, 1, tTag.numOfDataItem, inputFile);
					printf("Exposure speed\t: %i/%i seconds\n", val, val2);
					fseek(inputFile, filePos, SEEK_SET);
					k++;
				}
				
				else if(tTag.tagID == 0x829D){
					unsigned int val = 0;
					fseek(inputFile, tTag.valOrOffset + 12, SEEK_SET);
					fread(&val, 1, tTag.numOfDataItem, inputFile);
					unsigned int val2 = 0;
					fseek(inputFile, tTag.valOrOffset + 12 + 4, SEEK_SET);
					fread(&val2, 1, tTag.numOfDataItem, inputFile);
					printf("F-Stop\t\t: f/%.1f\n", (double)val/val2);
					fseek(inputFile, filePos, SEEK_SET);
					k++;
				}
				
				else if(tTag.tagID == 0x920A){
					unsigned int val = 0;
					fseek(inputFile, tTag.valOrOffset + 12, SEEK_SET);
					fread(&val, 1, tTag.numOfDataItem, inputFile);
					unsigned int val2 = 0;
					fseek(inputFile, tTag.valOrOffset + 12 + 4, SEEK_SET);
					fread(&val2, 1, tTag.numOfDataItem, inputFile);
					printf("Focal Length\t: %.0f mm\n", (double)val/val2);
					fseek(inputFile, filePos, SEEK_SET);
					k++;
				}
				else if(tTag.tagID == 0x9003){
					char str[tTag.numOfDataItem];
					fseek(inputFile, tTag.valOrOffset + 12, SEEK_SET);
					fread(str, 1, tTag.numOfDataItem, inputFile);
					printf("Date Taken\t: %s\n", str);
					fseek(inputFile, filePos, SEEK_SET);
					k++;
				}
				
				else
					k++;
				
			}
		}
		
		else if(tTag.tagID == 0x010F){
			char str[tTag.numOfDataItem];
			fseek(inputFile, tTag.valOrOffset + 12, SEEK_SET);
			fread(str, tTag.numOfDataItem, 1, inputFile);
			printf("Manufacturer\t: %s\n", str);
			fseek(inputFile, filePos, SEEK_SET);
			i++;
		}
		
		else if(tTag.tagID == 0x0110){
			char str[tTag.numOfDataItem];
			fseek(inputFile, tTag.valOrOffset + 12, SEEK_SET);
			fread(str, 1, tTag.numOfDataItem, inputFile);
			printf("Model\t\t: %s\n", str);
			fseek(inputFile, filePos, SEEK_SET);
			i++;
		}
		
		else{
			i++;
		}
	}
	
	fclose(inputFile);
	printf("\n");
	return 0;
}
