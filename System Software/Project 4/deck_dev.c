#include <linux/init.h>
#include <linux/module.h>
#include <linux/random.h>
#include <linux/kernel.h>	// printk()
#include <linux/slab.h> 	// kmalloc()
#include <linux/fs.h>		// everything
#include <linux/errno.h>	// error codes
#include <linux/types.h>	// ssize_t
#include <linux/fcntl.h>	// O_ACCMODE
#include <linux/miscdevice.h>
#include <asm/uaccess.h>	// copy_to_user copy_from_user

MODULE_LICENSE("GPL");

unsigned int face; // this is the face of the card, goes from 2-14 where above 10: 11 is Jack, 12 is Queen, 13 is King, and 14 is Ace
unsigned char suit; // this is the suit of the card, S for spade, H for heart, D for diamond, and C for club
unsigned char shuffle;
unsigned int faceCount = 2;
unsigned int position = 0; // points to where we are in the deck
int deckFace[52];
char deckSuit[52];
int seen[52];

// read 2 bytes from the deck
static ssize_t deck_read(struct file * file, char * buf, size_t count, loff_t *ppos){
	
	char outBuffer[2];
	
	if(count != 2){
		return -EINVAL;
	}
	
	/*
	*	if shuffle is 0, then just return the deck as is
	*/
	if(shuffle == '0'){
		
		if(position < 52){
			face = deckFace[position];
			suit = deckSuit[position];
			
			outBuffer[0] = face;
			outBuffer[1] = suit;
			position++;
		}
		
		if(position >=52){
			position = 0;
		}
		
	}
	
	/*
	*	if shuffle is 1, randomly generate a number and return the card at that index
	*/
	if(shuffle == '1'){
		
		unsigned char c;
		get_random_bytes(&c, 1);
		c = c % 52;
		
		while(seen[c] == 1){
			get_random_bytes(&c, 1);
			c = c % 52;
			if(seen[c] == 0){
				seen[c] = 1;
				break;
			}
		}
		
		face = deckFace[c];
		suit = deckSuit[c];
		
		outBuffer[0] = face;
		outBuffer[1] = suit;
		
	}
	
	// transfer data to user space
	if(copy_to_user(buf, outBuffer, 2)){
		return -EINVAL;
	}
	
	return 2;
	
}

// write to see whether we need to shuffle the deck or not, 0 = not, 1 = shuffle
static ssize_t deck_write(struct file * file, const char * buf, size_t count, loff_t *ppos){
	
	char inBuffer;
	
	if(count != 1){
		return -EINVAL;
	}
	
	if(copy_from_user(&inBuffer, buf, 1)){
		return -EINVAL;
	}
	
	shuffle = inBuffer;
	
	int i;
	for(i = 0; i < 52; i++){
		seen[i] = 0;
	}
	
	return 1;
	
}

static const struct file_operations deck_file_operations = {
	.owner		= THIS_MODULE,
	.read		= deck_read,
	.write		= deck_write
};

static struct miscdevice deck_dev = {
	MISC_DYNAMIC_MINOR,
	"deck",
	&deck_file_operations
};

static int __init deck_init(void){
	
	int val;
	
	// initialize the array of decks
	int count;
	for(count = 0; count < 52; count++){
		
		// fill the suit array with the appropriate number of suits
		if(count < 13){
			deckSuit[count] = 'S';
		}
		
		if(count > 12 && count < 26){
			deckSuit[count] = 'H';
		}
		
		if(count > 25 && count < 39){
			deckSuit[count] = 'D';
		}
		
		if(count > 38 && count < 52){
			deckSuit[count] = 'C';
		}
		
		// fill the face array with the appropriate number of faces
		if(faceCount < 15){
			deckFace[count] = faceCount;
			
			// reset faceCount to 2 once we've hit 14
			if(faceCount == 14){
				faceCount = 1;
			}
		}
		faceCount++;
		
	}
	
	val = misc_register(&deck_dev);
	if (val){
		printk(KERN_ERR "Unable to register \"deck\" misc device\n");
	}
	
	return val;
	
}
module_init(deck_init);

static void __exit deck_exit(void){
	
	misc_deregister(&deck_dev);
	
}
module_exit(deck_exit);