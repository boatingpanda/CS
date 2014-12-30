What works:
	- Malloc:
		- can allocate the first node and set as head
		- can allocate all subsequent nodes
		- can allocate based on next-fit
	- Free:
		- can free up the last node and remove it off of the list
		- can free up a node in the middle
		- can free up 2 or more nodes in the middle
		- can free up nodes that merges with the tail node (and delete them off of the list)
		- can remove head node if it's the only node in the list
		- can free head node if there are other non-free nodes connected to it
		- can merge three nodes together if there are 2 free nodes next to the to be free node.

What doesn't work/untested/unimplemented:
	- cannot split node to minimize internal segmentation
	- other situations that might come up for free() is not tested