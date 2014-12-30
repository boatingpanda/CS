import java.util.Arrays;

public class AdaptiveHuffmanTree{
     Node[] rlo;          //list of nodes in the tree in reverse-level order rlo[255], rlo[254], ...
     Node NYT;            //special node representing unseen characters
     Node[] characters;   //pointers to the nodes in the tree holding each character, if null then the character has not been seen
     int size;            //number of Nodes in the tree

     public AdaptiveHuffmanTree(){
          characters = new Node[256];
          rlo = new Node[256];

          for(int k=0; k<256; k++){
              characters[k] = null;
          }

          NYT = new Node(0);
          NYT.place = 255;               //NYT is going to be placed at the root of the tree
          NYT.weight = 0;                //NYT has weight 0
          NYT.label = '0';               //NYT is always a leftchild
          NYT.leaf = true;               //NYT is always a leaf
          rlo[NYT.place] = NYT;          //place NYT at the root of the tree
          NYT.parent = null;             //the root of the tree has no parent
          size = 1;
     }

   /*
    * Update the adaptive Huffman tree after inserting character c. This is the UPDATE procedure discussed in class.
    */
   public void update(char c){
      
      if(characterInTree((int)c) == false){ // if this is the first time encountering the character
          
          NYT.leaf = false;
          
          Node newNYT = new Node(0); // create new NYT node and set weight to 0
          newNYT.parent = NYT; // this NYT node's parent is the old NYT node
          newNYT.place = NYT.place - 2;
          newNYT.weight = 0;
          newNYT.label = '0'; // NYT node is always on the left
          newNYT.leaf = true; // this is always the leaf node
          rlo[newNYT.place] = newNYT;
          size++;
          
          Node extNode = new Node(1, c); // create new node to store the new character c
          extNode.parent = NYT; // it's parent node is old NYT
          extNode.place = NYT.place - 1;
          extNode.label = '1'; // extNode is always on the right
          extNode.leaf = true; // this is always the leaf node
          rlo[extNode.place] = extNode;
          characters[(int)c] = extNode;
          size++;
          
          NYT = newNYT;
          
          Node temp = NYT;
          while(NYT.parent != null){
            
            NYT.parent.weight++;
            NYT = NYT.parent;
            
          }
          NYT = temp;
          
          while(satisfiesSiblingProperty() == false){
            
            arrangeNodes();
            
          }
          
      }
      
      else{ //go to symbol external node
          
          Node curr = characters[(int)c]; // find where the external node is located
          
          while(curr != null){ // update the current node and all of its parents
            
            curr.weight++;
            curr = curr.parent;
            
          }
          
          while(satisfiesSiblingProperty() == false){
            
            arrangeNodes();
            
          }
          
      }
      
   }


   /*
    * Swap Nodes u and v in the reverse-level ordered array. Note: Node u is at index u.place!
    */
   private void swap(Node u, Node v){
      
      int tempPos = u.place; // put u's place to tempPos
      u.place = v.place;
      v.place = tempPos;
      
      char tempLabel = u.label;
      u.label = v.label;
      v.label = tempLabel;
      
      Node tempParent = u.parent;
      u.parent = v.parent;
      v.parent = tempParent;
      
      rlo[v.place] = v; // set where u used to be to v;
      rlo[u.place] = u; // set where v used to be to tempNode, which is u
      
      updateNodeWeights();
      
   }
   
   public void updateNodeWeights(){
     
    for(int i = NYT.place; i < 256; i++){
      
      if(rlo[i].leaf == false){
        rlo[i].weight = 0;
      }
      
    }
    
    for(int i = NYT.place; i < 256; i++){
      
      if(rlo[i].leaf == true){
        int w = rlo[i].weight;
        Node temp = rlo[i];
        
        do{
          
          temp = temp.parent;
          temp.weight += w;
          
        }while(temp.parent != null);
        
      }
      
    }
    
   }

   /*
    * Return true if character c has been seen, otherwise, return false.
    */
   public boolean characterInTree(int  c){
      
      if(characters[c] == null){
          return false;
      }
      
      else{
          return true;
      }
      
   }


   /*
    * Return true if the reverse-level order traversal is a monotonically decreasing sequence, otherwise, return false.
    */
   private boolean satisfiesSiblingProperty(){
      
      boolean hasSP = true;
      
      loop:
      for(int i = 254; i>=0; i--){
        
        if(rlo[i] != null){
          
          if(rlo[i+1].weight < rlo[i].weight){
            hasSP = false;
            break loop;
          }
          
        }
        
      }
      
      return hasSP;
      
   }
   
   public void arrangeNodes(){
      
      Node u = new Node(0);
      Node v = new Node(0);
      
      for(int i = rlo.length-1; i > 0; i--){
        
        if(rlo[i] == null){
          
          break;
          
        }
        
        if(rlo[i].weight < rlo[i-1].weight){
          u = rlo[i-1];
          v = rlo[i];
          break;
        }
        
      }
      
      swap(u, v);
    
   }


   /*
    * Return the sequence of labels (characters) from the root to the NYT node.
    */
   public StringBuffer getCodeWordForNYT(){
      StringBuffer sb = new StringBuffer();
      Node n = NYT;
      while(n.parent != null){
        
        sb.append(n.label);
        n = n.parent;
        
      }
      
      sb = sb.reverse();
      
      return sb;   //change to the correct return value.
   }


   /*
    * Return the sequence of labels (characters from the root to the Node for character c.
    */
   public StringBuffer getCodeWordFor(char c){
      StringBuffer sb = new StringBuffer();
      Node n = characters[(int)c];
      while(n.parent != null){
        
        sb.append(n.label);
        n = n.parent;
        
      }
      
      sb = sb.reverse();
      
      return sb;    //change to the correct return value.
   }


   /*
    * return the reference to the root node.
    */
   public Node root(){
      return rlo[255];   //change to the correct return value.
   }


   public int size(){
      return size;
   }


   /*
    * I've provided this to help debug your tree.
    */
   public String toString(){
       String result = "[";

       for(int k=255; k>=0 ; k--){
           if(rlo[k] != null)
                result += "(" + rlo[k].weight + "," + rlo[k].place + "," + rlo[k].character + ") ";
       }

       return result + "]\nsize = " + size;
   }
   
   // This test method is used to print out all of the leaf nodes for debugging purposes
   public void print(){
    
    StdOut.println("leaf nodes are: ");
    for(int i = 255; i >= 0; i--){
      
      if(rlo[i] != null && rlo[i].leaf == true){
        StdOut.println(i);
      }
      
    } 
    
   }
   
   public static void main(String[] args){
      
      AdaptiveHuffmanTree testTree = new AdaptiveHuffmanTree();
      
      while(!BinaryStdIn.isEmpty()){
        
        char c = BinaryStdIn.readChar();
        testTree.update(c);
        
      }
      // testTree.update('A');
      // testTree.update('B');
      // testTree.update('B');
      
      //testTree.print();
      StdOut.println(testTree);
      
   }
   
}