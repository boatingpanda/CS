import java.util.*;
import java.io.*;

public class vmsim{
    
    public static void main(String[] args) throws IOException {
        
        // Obtain all the necessary information through command line input
        int numOfFrames = Integer.parseInt(args[1]);
        String algorithm = args[3];
        // the refresh frame is only for NRU algorithm
        int refresh = 0;                                                               
        String fileName = "";
        if( algorithm.equals("nru")){
            refresh = Integer.parseInt(args[5]);
            fileName = args[6];
        }
        else{
             fileName = args[4];
        }
        
        int totalMemAccessed = 0;
        int totalPageFaults = 0;
        int totalWritesDisk = 0;
        
        // Opt algorithm implementation
        if(algorithm.equals("opt")){
            
            // Create a buffered reader to read through fileName
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            
            // read in the file's data so we don't have to scan through the file multiple times
            // this is done by adding each line into a hashmap with the page frame number as key and its occurence as value in an arraylist for fast lookup in the future
            // then add every line into an arraylist so we don't have to reopen the file and read through it again
            ArrayList<String> fileIn = new ArrayList<String>();
            HashMap<String, ArrayList<Integer>> future = new HashMap<String, ArrayList<Integer>>();
            int occ = 0;
            
            String line = "";
            while((line = reader.readLine()) != null){
                
                // split the line into a string array, the first index should be the address in hex and the second index is either R or W
                String[] split = line.split(" ");
                
                String frameNum = split[0].substring(0, 5);
                fileIn.add(frameNum + " " + split[1]);
                
                // if we have seen this address, simply add the second index to the arraylist in the corresponding key
                if(future.containsKey(frameNum)){
                    ArrayList<Integer> temp = future.get(frameNum);
                    temp.add(occ);
                }
                
                // otherwise just add the key/value into the hashmap
                else{
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    temp.add(occ);
                    future.put(frameNum, temp);
                }
                
                occ++;
                
            }
            reader.close();
            
            // create the proper sized frame
            ArrayList<String> pageFrame = new ArrayList<String>(numOfFrames);
            ArrayList<String> rwRecord = new ArrayList<String>(numOfFrames);
            int noEvict = 0;
            
            for(int i = 0; i < fileIn.size(); i++){
                
                String[] split = fileIn.get(i).split(" ");
                String frameNum = split[0];
                String rw = split[1];
                totalMemAccessed++;
                
                // if page frame isn't full and the page isn't in our page frame, give it the next free frame
                // go to the future HashMap and remove its occurrence
                if(noEvict < numOfFrames && pageFrame.contains(frameNum) == false){
                    pageFrame.add(frameNum);
                    rwRecord.add(rw);
                    totalPageFaults++;
                    noEvict++;
                    ArrayList<Integer> temp = future.get(frameNum);
                    temp.remove(0);
                    System.out.println(frameNum + " page fault - no eviction");
                }
                
                // if the page is in our page frame, then we have a hit and we can continue
                else if(pageFrame.contains(frameNum)){
                    System.out.println(frameNum + " hit");
                    if(rw.equals("W")){
                        int index = pageFrame.indexOf(frameNum);
                        rwRecord.remove(index);
                        rwRecord.add(index, rw);
                    }
                    ArrayList<Integer> temp = future.get(frameNum);
                    if(temp.size() > 0)
                        temp.remove(0);
                }
                
                // if the page frame is full and doesn't have what the page, evict the page that will be used furthest in the future
                else if(noEvict == numOfFrames && pageFrame.contains(frameNum) == false){
                    
                    int furthest = 0;
                    int kickThis = 0;
                    // iterate through each of the pages on the page frame
                    for(int k = 0; k < pageFrame.size(); k++){
                        
                        ArrayList<Integer> futureUse = future.get(pageFrame.get(k));
                        // if we have an element in the future use arraylist, record it if it appears further than the current furthest
                        if(futureUse.size() > 0 && futureUse.get(0) > furthest){
                            furthest = futureUse.get(0);
                            kickThis = k;
                        }
                        
                        // if we'll never use the frame again, set it to the furthest and break
                        if(futureUse.size() == 0){
                            kickThis = k;
                            break;
                        }
                        
                    }
                    
                    // figure out if this evicts a dirty page or a clean page
                    if(rwRecord.get(kickThis).equals("W")){
                        System.out.println(frameNum + " page fault - evict dirty");
                        totalWritesDisk++;
                    }
                    else
                        System.out.println(frameNum + " page fault - evict clean");
                    
                    // kick out the page that will be used furthest in the future and give it to the current one
                    pageFrame.remove(kickThis);
                    rwRecord.remove(kickThis);
                    pageFrame.add(kickThis, frameNum);
                    rwRecord.add(kickThis, rw);
                    ArrayList<Integer> temp = future.get(frameNum);
                    if(temp.size() > 0)
                        temp.remove(0);
                    
                    totalPageFaults++;
                    
                }
                
            }
            
        }
        
        // Clock algorithm implementation
        else if(algorithm.equals("clock")){
            
            // Create a buffered reader to read through fileName
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            
            // create the proper sized frame
            ArrayList<String> pageFrame = new ArrayList<String>(numOfFrames);
            ArrayList<String> rwRecord = new ArrayList<String>(numOfFrames);
            ArrayList<Integer> refRecord = new ArrayList<Integer>(numOfFrames);
            int iterator = 0;
            String line = "";
            
            // to traverse the frames like a clock, we'll reset our iterator to the beginning when we get to the end of our ArrayList
            while((line = reader.readLine()) != null){
                
                String[] split = line.split(" ");
                String frameNum = split[0].substring(0, 5);
                String rw = split[1];
                totalMemAccessed++;
                
                // if the file's in our page frame, we have a hit.
                // make sure to change its read/write property if the new page is a write instruciton
                if(pageFrame.contains(frameNum)){
                    
                    System.out.println(frameNum + " hit");
                    int index = pageFrame.indexOf(frameNum);
                    if(rw.equals("W")){
                        rwRecord.remove(index);
                        rwRecord.add(index, rw);
                    }
                    refRecord.remove(index);
                    refRecord.add(index, 1);
                    
                }
                
                // if the file's not in our page frame and our page frame isn't full, give the page the next free spot
                // remember to add the corresponding referenced bit and dirty bit to the appropriate spot in their arraylist
                else if(pageFrame.size() < numOfFrames && !pageFrame.contains(frameNum)){
                    
                    System.out.println(frameNum + " page fault - no eviction");
                    pageFrame.add(frameNum);
                    rwRecord.add(rw);
                    refRecord.add(1);
                    totalPageFaults++;
                    
                }
                
                // if the page frame is full and doesn't have what the page, evict the first page that wasn't referenced. loop back if 404
                else{
                    
                    while(true){
                        
                        if(iterator == numOfFrames){
                            iterator = 0;
                        }
                        
                        // if the referenced bit for this page is 0, we've found the one we can evict and evict it and increment the iterator
                        if(refRecord.get(iterator) == 0){
                            
                            if(rwRecord.get(iterator).equals("W")){
                                totalWritesDisk++;
                                System.out.println(frameNum + " page fault - evict dirty");
                            }
                            else
                                System.out.println(frameNum + " page fault - evict clean");
                            pageFrame.remove(iterator);
                            rwRecord.remove(iterator);
                            refRecord.remove(iterator);
                            
                            pageFrame.add(iterator, frameNum);
                            rwRecord.add(iterator, rw);
                            refRecord.add(iterator, 1);
                            
                            totalPageFaults++;
                            iterator++;
                            break;
                            
                        }
                        
                        // if the referenced bit for this page is 1, then set it to 0 and increment the iterator
                        else{
                            refRecord.remove(iterator);
                            refRecord.add(iterator, 0);
                            iterator++;
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
        // NRU algorithm implementation
        else if(algorithm.equals("nru")){
            
            // Create a buffered reader to read through fileName
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            
            // create the proper sized frame
            ArrayList<String> pageFrame = new ArrayList<String>(numOfFrames);
            ArrayList<String> rwRecord = new ArrayList<String>(numOfFrames);
            ArrayList<Integer> refRecord = new ArrayList<Integer>(numOfFrames);
            String line = "";
            
            // to find something to kick, we must find the one that's oldest, preferably not dirty
            while((line = reader.readLine()) != null){
                
                // clear the referenced bit at a given interval
                if(totalMemAccessed%refresh == 0){
                    refRecord.clear();
                    for(int i = 0; i < numOfFrames; i++){
                        refRecord.add(0);
                    }
                }
                
                String[] split = line.split(" ");
                String frameNum = split[0].substring(0, 5);
                String rw = split[1];
                totalMemAccessed++;
                
                // if the file's in our page frame, we have a hit.
                // make sure to change its read/write property if the new page is a write instruciton
                if(pageFrame.contains(frameNum)){
                    
                    System.out.println(frameNum + " hit");
                    if(rw.equals("W")){
                        int index = pageFrame.indexOf(frameNum);
                        rwRecord.remove(index);
                        rwRecord.add(index, rw);
                    }
                    
                }
                
                // if the file's not in our page frame and our page frame isn't full, give the page the next free spot
                // remember to add the corresponding referenced bit and dirty bit to the appropriate spot in their arraylist
                else if(pageFrame.size() < numOfFrames && !pageFrame.contains(frameNum)){
                    
                    System.out.println(frameNum + " page fault - no eviction");
                    pageFrame.add(frameNum);
                    rwRecord.add(rw);
                    refRecord.add(1);
                    totalPageFaults++;
                    
                }
                
                // if the page frame is full and doesn't have what the page, evict the oldest page, highest preferred page based on referenced bit and dirty bit
                else{
                    
                    // find the page to kick from highest preference to lowest, if we can find the higher one, we'll kick it
                    int pref1 = -1, pref2 = -1, pref3 = -1, pref4 = -1;
                    
                    // find preference level 1, not referenced and not dirty
                    for(int i = 0; i < numOfFrames; i++){
                        if(refRecord.get(i) == 0 && rwRecord.get(i).equals("R")){
                            pref1 = i;
                            break;
                        }
                    }
                    
                    // find preference level 2, not referenced and dirty
                    for(int i = 0; i < numOfFrames; i++){
                        if(refRecord.get(i) == 0 && rwRecord.get(i).equals("W")){
                            pref2 = i;
                            break;
                        }
                    }
                    
                    // find preference level 3, referenced and not dirty
                    for(int i = 0; i < numOfFrames; i++){
                        if(refRecord.get(i) == 1 && rwRecord.get(i).equals("R")){
                            pref3 = i;
                            break;
                        }
                    }
                    
                    // find preference level 4, referenced and dirty
                    for(int i = 0; i < numOfFrames; i++){
                        if(refRecord.get(i) == 1 && rwRecord.get(i).equals("W")){
                            pref4 = i;
                            break;
                        }
                    }
                    
                    if(pref1 > -1){
                        System.out.println(frameNum + " page fault - evict clean");
                        pageFrame.remove(pref1);
                        rwRecord.remove(pref1);
                        refRecord.remove(pref1);
                        
                        pageFrame.add(pref1, frameNum);
                        rwRecord.add(rw);
                        refRecord.add(1);
                        
                        totalPageFaults++;
                    }
                    else if(pref2 > -1){
                        totalWritesDisk++;
                        System.out.println(frameNum + " page fault - evict dirty");
                        pageFrame.remove(pref2);
                        rwRecord.remove(pref2);
                        refRecord.remove(pref2);
                        
                        pageFrame.add(pref2, frameNum);
                        rwRecord.add(rw);
                        refRecord.add(1);
                        
                        totalPageFaults++;
                    }
                    else if(pref3 > -1){
                        System.out.println(frameNum + " page fault - evict clean");
                        pageFrame.remove(pref3);
                        rwRecord.remove(pref3);
                        refRecord.remove(pref3);
                        
                        pageFrame.add(pref3, frameNum);
                        rwRecord.add(rw);
                        refRecord.add(1);
                        
                        totalPageFaults++;
                    }
                    else{
                        totalWritesDisk++;
                        System.out.println(frameNum + " page fault - evict dirty");
                        pageFrame.remove(pref4);
                        rwRecord.remove(pref4);
                        refRecord.remove(pref4);
                        
                        pageFrame.add(pref4, frameNum);
                        rwRecord.add(rw);
                        refRecord.add(1);
                        
                        totalPageFaults++;
                    }
                    
                }
                
            }
            
        }
        
        // Random algorithm implementation
        else if(algorithm.equals("rand")){
            
            Random rand = new Random();
            
            // Create a buffered reader to read through fileName
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            
            // create the proper sized frame
            ArrayList<String> pageFrame = new ArrayList<String>(numOfFrames);
            ArrayList<String> rwRecord = new ArrayList<String>(numOfFrames);
            String line = "";
            
            // to find something to kick, we'll use a random number generator to pick a page
            while((line = reader.readLine()) != null){
                
                String[] split = line.split(" ");
                String frameNum = split[0].substring(0, 5);
                String rw = split[1];
                totalMemAccessed++;
                
                // if the file's in our page frame, we have a hit.
                // make sure to change its read/write property if the new page is a write instruciton
                if(pageFrame.contains(frameNum)){
                    
                    System.out.println(frameNum + " hit");
                    if(rw.equals("W")){
                        int index = pageFrame.indexOf(frameNum);
                        rwRecord.remove(index);
                        rwRecord.add(index, rw);
                    }
                    
                }
                
                // if the file's not in our page frame and our page frame isn't full, give the page the next free spot
                // remember to add the corresponding dirty bit to the appropriate spot in their arraylist
                else if(pageFrame.size() < numOfFrames && !pageFrame.contains(frameNum)){
                    
                    System.out.println(frameNum + " page fault - no eviction");
                    pageFrame.add(frameNum);
                    rwRecord.add(rw);
                    totalPageFaults++;
                    
                }
                
                // if the page frame is full and doesn't have what the page, randomly pick on and kick it
                else{
                    
                    int kickThis = rand.nextInt(numOfFrames);
                    
                    if(rwRecord.get(kickThis).equals("W")){
                        totalWritesDisk++;
                        System.out.println(frameNum + " page fault - evict dirty");
                    }
                    else
                        System.out.println(frameNum + " page fault - evict clean");
                    
                    pageFrame.remove(kickThis);
                    rwRecord.remove(kickThis);
                    pageFrame.add(kickThis, frameNum);
                    rwRecord.add(kickThis, rw);
                    totalPageFaults++;
                    
                }
                
            }
            
        }
        
        // print out error message for the user due to invalid input or unsupported algorithm
        else{
            System.out.println("Error: invalid format or unsupported algorithm. Please use the following format:\njava vmsim -n <numFrames> -a <algorithm> [-r refresh] <tracefile>\n\nSupported formats are opt, clock, nru, and rand. Refresh is only required for NRU algorithm.\n\nProgram terminating.");
        }
        
        System.out.println("Number of frames: " + numOfFrames);
        System.out.println("Total Memory Accesses: " + totalMemAccessed);
        System.out.println("Total page faults: " + totalPageFaults);
        System.out.println("Total writes to disk: " + totalWritesDisk);
        
    }
    
}