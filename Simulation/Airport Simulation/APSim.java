import java.util.*;

public class APSim{
    
    static Random rand = new Random();
    
    public static void main(String[] args){
        
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the simulation time in minutes: ");
        
        // set the simulation time (given by user)
        double simTime = input.nextDouble();
        
        // default is 24
        System.out.println("Please enter the number of commuter flights per day: ");
        int cFlights = input.nextInt();
        
        // default is 75
        System.out.println("Please enter the mean time of arrival before takeoff for international: ");
        double mu = input.nextDouble();
        
        // default is 50
        System.out.println("Please enter the variance of arrival before takeoff for international: ");
        double variance = input.nextDouble();
        
        // default is 0.85
        System.out.println("Please enter the chance of filling an international coach seat: ");
        double fillCoach = input.nextDouble();
        
        // default is 0.80
        System.out.println("Please enter the chance of filling an international first class seat: ");
        double fillFirstClass = input.nextDouble();
        
        // set the simulation's number of coach check-in counters
        System.out.println("Please enter the number of coach flyer check-in counters: ");
        int c = input.nextInt();
        
        // set the simulation's number of first class check-in counters
        System.out.println("Please enter the number of first class flyer check-in counters: ");
        int f = input.nextInt();
        
        // set the simulation's number of coach security scanners
        System.out.println("Please enter the number of coach flyer security scanners: ");
        int cScanNum = input.nextInt();
        
        // set the simulation's number of first class security scanners
        System.out.println("Please enter the number of first class flyer security scanners: ");
        int fScanNum = input.nextInt();
        
        double[] agentIdleCoach = new double[c];
        double[] agentIdleFirst = new double[f];
        double[] coachCheckInTime = new double[c];
        double[] firstClassCheckInTime = new double[f];
        double[] coachSecurityTime = new double[cScanNum];
        double[] firstClassSecurityTime = new double[fScanNum];
        
        /*
        *
        *   Generate all of possible commuters, the time the last commuter arrives must be smaller than the total simulation time
        *   Everyone else after the simulation time doesn't matter
        *
        */
        
        double lastCustTime = 0;
        double lambda = 40.0/60.0;
        double daysSim = simTime / 1440;
        double cFlightTime = cFlights * 60 * daysSim;
        ArrayList<Double> custList = new ArrayList<Double>();
        while(lastCustTime <= cFlightTime){
            
            // generate a random number uniformly distributed between 0 and 1 to be used for distributions
            double num = rand.nextDouble();
            double waitTime = expon(lambda, num);
            
            if(custList.size() > 0){
                waitTime += custList.get(custList.size()-1);
                custList.add(waitTime);
            }
            else
                custList.add(waitTime);
            lastCustTime = waitTime;
            
        }
        
        /*
        *
        *   Generate all of possible internationals, we can calculate the last internationals by the number of flights available in the time slot
        *
        */
        
        // each international flights are 6 hours apart, calculate the number of possible international flights within simulation time
        int numOfPlanes = (int)simTime / (6 * 60);
        
        // calculate the total capacity for each type of passengers and see if it's possible for each seats to be filled
        String[] firstClass = new String[numOfPlanes * 50];
        double[] firstClassTemp = new double[numOfPlanes * 50];
        
        String[] coach = new String[numOfPlanes * 150];
        double[] coachTemp = new double[numOfPlanes * 150];
        
        for(int k = 0; k < firstClass.length; k+=50){
            for(int i = 0; i < 50; i++){
                double num = rand.nextDouble();
                firstClassTemp[k+i] = fillSeat(fillFirstClass, num);
            }
        }
        
        for(int k = 0; k < coach.length; k+=150){
            for(int i = 0; i < 150; i++){
                double num = rand.nextDouble();
                coachTemp[k+i] = fillSeat(fillCoach, num);
            }
        }
        
        // calculate the arrival time of each passengers, normalize the arrival time, and sort it
        // for first class passengers
        double takeOffTime = 360;
        int countPlanes = 0;
        for(int i = 0; i < firstClass.length; i++){
            if(i % 50 == 0){
                countPlanes++;
            }
            if(firstClassTemp[i] > 0){
                double num = rand.nextDouble();
                double zValue = normal(num);
                double arriveAhead = calcX(zValue, mu, variance);
                firstClass[i] = (takeOffTime * countPlanes) - arriveAhead + " " + countPlanes;
            }
            else
                firstClass[i] = "0";
        }
        
        // sort the flights
        for(int i = 0; i < firstClass.length; i+=50){
            Arrays.sort(firstClass, i, (i+50));
        }
        
        // remove all 0's from the array
        ArrayList<String> temp = new ArrayList<String>();
        for(int i = 0; i < firstClass.length; i++){
            if(firstClass[i].equals("0"))
                continue;
            else{
                temp.add(firstClass[i]);
            }
        }
        firstClass = temp.toArray(new String[temp.size()]);
        
        // for coach passengers
        temp.clear();
        countPlanes = 0;
        for(int i = 0; i < coach.length; i++){
            if(i % 150 == 0){
                countPlanes++;
            }
            if(coachTemp[i] > 0){
                double num = rand.nextDouble();
                double zValue = normal(num);
                double arriveAhead = calcX(zValue, mu, variance);
                coach[i] = (takeOffTime * countPlanes) - arriveAhead + " " + countPlanes;
            }
            else
                coach[i] = "0";
        }
        
        for(int i = 0; i < coach.length; i+=150){
            Arrays.sort(coach, i, (i+150));
        }
        // remove all 0's from the array
        for(int i = 0; i < coach.length; i++){
            if(coach[i].equals("0"))
                continue;
            else{
                temp.add(coach[i]);
            }
        }
        coach = temp.toArray(new String[temp.size()]);
        
        /*
        *
        *   Figure out how many luggage each passenger is carrying
        *
        */
        
        // initialize the probability of passenger stop bringing bags
        double flyFarBags = 0.6;
        double flyShortBags = 0.8;
        
        // figure out how many bags the the commuters are carrying
        int[] localBags = new int[custList.size()];
        for(int i = 0; i < localBags.length; i++){
            
            double num = 0;
            int bags = 0;
            do{
                num = rand.nextDouble();
                if(num > flyShortBags){
                    bags++;
                }
                
            }while(num > flyShortBags);
            localBags[i] = bags;
            
        }
        
        // figure out how many bags the international first class passengers are carrying
        int[] firstClassBags = new int[firstClass.length];
        for(int i = 0; i < firstClassBags.length; i++){
            
            double num = 0;
            int bags = 0;
            do{
                num = rand.nextDouble();
                if(num > flyFarBags){
                    bags++;
                }
                
            }while(num > flyFarBags);
            firstClassBags[i] = bags;
            
        }
        
        // figure out how many bags the international coach passengers are carrying
        int[] longDistCoachBags = new int[coach.length];
        for(int i = 0; i < longDistCoachBags.length; i++){
            
            double num = 0;
            int bags = 0;
            do{
                num = rand.nextDouble();
                if(num > flyFarBags){
                    bags++;
                }
                
            }while(num > flyFarBags);
            longDistCoachBags[i] = bags;
            
        }
        
        /*
        *
        *   Simulate the check-in counter (max of 6 counters between coach and first class)
        *
        */
        
        // create an array of row c, where c + f <= 6, to serve coach passengers.
        double[] cCounters = new double[c];
        int cServer = 0;
        
        // create a matrix of row f, where c + f <= 6, to serve first class passengers.
        double[] fCounters = new double[f];
        int fServer = 0;
        
        // check the three arrays to see which person is supposed to get into the line next, put them in the appropriate line based on class
        // tag coach passengers with a c and first class with f
        int lIterator = 0;
        double[] commuter = new double[custList.size()];
        int fIterator = 0;
        int cIterator = 0;
        
        double[] cAgentIdleTime = new double[c];
        double[] fAgentIdleTime = new double[f];
        double[] coachWT = new double[c];
        double[] firstWT = new double[f];
        
        double localPass = 0.00;
        double interFirstClass = 0.00;
        double interFirstClassFlight = 0.00;
        double interCoach = 0.00;
        double interCoachFlight = 0.00;
        
        while(true){
            
            // if all three arrays are empty, then break out of our loop
            if(lIterator >= custList.size() && fIterator >= firstClass.length && cIterator >= coach.length){
                break;
            }
            
            else{
                
                if(lIterator < custList.size()){
                    localPass = custList.get(lIterator);
                }
                else{
                    localPass = Double.MAX_VALUE;
                }
                if(fIterator < firstClass.length){
                    String[] tempS = firstClass[fIterator].split(" ");
                    interFirstClass = Double.parseDouble(tempS[0]);
                    interFirstClassFlight = Double.parseDouble(tempS[1]);
                }
                else{
                    interFirstClass = Double.MAX_VALUE;
                }
                if(cIterator < coach.length){
                     String[] tempS = coach[cIterator].split(" ");
                     interCoach = Double.parseDouble(tempS[0]);
                     interCoachFlight = Double.parseDouble(tempS[1]);
                }
                else{
                    interCoach = Double.MAX_VALUE;
                }
                
                double wt = 0.00;
                // if local flight passenger came first, or international coach, put him in the coach counter
                if((localPass < interFirstClass && localPass < interCoach) || (interCoach < localPass && interCoach < interFirstClass)){
                    //System.out.println("counter0: " + cCounters[0] + " couter1: " + cCounters[1] + " counter2: " + cCounters[2]);
                    // determine if this is local or international, increment the appropriate iterator
                    if(localPass < interCoach){
                        // if the passenger's time of arrival > counter's time, then he doesn't have to wait, there's idle time. otherwise wait
                        if(localPass > cCounters[cServer]){
                            cAgentIdleTime[cServer] += localPass - cCounters[cServer];
                            cCounters[cServer] = localPass;
                        }
                        else{
                            coachWT[cServer] += cCounters[cServer] - localPass;
                        }
                        
                        double processTime = calcProcessTime(lIterator, localBags);
                        cCounters[cServer] += processTime;
                        localPass = cCounters[cServer];
                        commuter[lIterator] = localPass;
                        
                        lIterator++;
                    }
                    else{
                        // if the passenger's time of arrival > counter's time, then he doesn't have to wait, there's idle time. otherwise wait
                        if(interCoach > cCounters[cServer]){
                            cAgentIdleTime[cServer] += interCoach - cCounters[cServer];
                            cCounters[cServer] = interCoach;
                        }
                        else{
                            coachWT[cServer] += cCounters[cServer] - interCoach;
                            wt = cCounters[cServer] - interCoach;
                        }
                        
                        double processTime = calcProcessTime(cIterator, longDistCoachBags);
                        cCounters[cServer] += processTime;
                        interCoach = cCounters[cServer];
                        coach[cIterator] = interCoach + " " + interCoachFlight + " i " + wt;
                        
                        cIterator++;
                    }
                    
                    cServer++;
                    if(cServer == c)
                        cServer = 0;
                    
                }
                
                // otherwise this is a first class international passenger, put him in the first class counter
                else{
                    //System.out.println("counter0: " + fCounters[0] + " couter1: " + fCounters[1] + " counter2: " + fCounters[2]);
                    
                    // if the passenger's time of arrival > counter's time, then he doesn't have to wait, there's idle time. otherwise wait
                    if(interFirstClass > fCounters[fServer]){
                        fAgentIdleTime[fServer] += interFirstClass - fCounters[fServer];
                        fCounters[fServer] = interFirstClass;
                    }
                    else{
                        firstWT[fServer] += fCounters[fServer] - interFirstClass;
                        wt = fCounters[fServer] - interFirstClass;
                    }
                    
                    double processTime = calcProcessTime(fIterator, firstClassBags);
                    fCounters[fServer] += processTime;
                    interFirstClass = fCounters[fServer];
                    firstClass[fIterator] = interFirstClass + " " + interFirstClassFlight + " f " + wt;
                    
                    fIterator++;
                    fServer++;
                    if(fServer == f){
                        fServer = 0;
                    }
                    
                }
                
            }
            
        }
        
        Arrays.sort(commuter);
        
        Comparator<String> comp = new Comparator<String>(){
            public int compare(String s1, String s2){
                String[] temp1 = s1.split(" ");
                String[] temp2 = s2.split(" ");
                double d1 = Double.parseDouble(temp1[0]);
                double d2 = Double.parseDouble(temp2[0]);
                int result = Double.compare(d1, d2);
                return result;
            }
        };
        
        Arrays.sort(coach, comp);
        Arrays.sort(firstClass, comp);
        // for(int i = 0; i < firstClass.length; i++){
        //     System.out.println(firstClass[i]);
        // }
        
        /* 
        *
        *   Move the appropriate flyers into the appropriate queue
        *
        */
        
        // create an arraylist filled with coach flyers and another for first class
        String[] coachFlyerQueue = new String[commuter.length + coach.length];
        String[] firstClassQueue = new String[firstClass.length];
        lIterator = 0;
        fIterator = 0;
        cIterator = 0;
        int cQueueu = 0;
        int fQueueu = 0;
        
        while(true){
            
            // if all three arrays are empty, then break out of our loop
            if(lIterator >= commuter.length && fIterator >= firstClass.length && cIterator >= coach.length){
                break;
            }
            
            // otherwise add them into the appropriate queue
            else{
                
                if(lIterator < commuter.length){
                    localPass = commuter[lIterator];
                }
                else{
                    localPass = Double.MAX_VALUE;
                }
                if(fIterator < firstClass.length){
                    String[] tempS = firstClass[fIterator].split(" ");
                    interFirstClass = Double.parseDouble(tempS[0]);
                }
                else{
                    interFirstClass = Double.MAX_VALUE;
                }
                if(cIterator < coach.length){
                     String[] tempS = coach[cIterator].split(" ");
                     interCoach = Double.parseDouble(tempS[0]);
                }
                else{
                    interCoach = Double.MAX_VALUE;
                }
                
                // if local flight passenger came first, or international coach, put him in the coach counter
                if((localPass < interFirstClass && localPass < interCoach) || (interCoach < localPass && interCoach < interFirstClass)){
                    
                    if(localPass < interCoach){
                        coachFlyerQueue[cQueueu] = localPass + "";
                        lIterator++;
                    }
                    else {
                        coachFlyerQueue[cQueueu] = coach[cIterator];
                        cIterator++;
                    }
                    cQueueu++;
                }
                else {
                    firstClassQueue[fQueueu] = firstClass[fIterator];
                    fQueueu++;
                    fIterator++;
                }
                
            }
            
        }
        // for(int i = 0; i < coachFlyerQueue.length; i++){
        //     System.out.println(coachFlyerQueue[i]);
        // }
        
        /*
        *
        *   Start of the security gate
        *
        */
        
        // create the screening machines for both first class passengers and coach passengers
        double[] cScan = new double[cScanNum];
        double[] fScan = new double[fScanNum];
        
        // remember to reset cServer and fServer to 0 when they hit cScanNum and fScanNum
        cServer = 0;
        fServer = 0;
        
        // create the queue for local customers to wait in after they get to the gate and internationals for flight checking
        int localCount = 0;
        int interCount = 0;
        double[] coachSWT = new double[cScanNum];
        double[] firstSWT = new double[fScanNum];
        double[] localGateQueue = new double[commuter.length];
        String[] internationalQueue = new String[firstClass.length + coach.length];
        
        // process the coach customers
        for(int i = 0; i < coachFlyerQueue.length; i++){
            String[] split = coachFlyerQueue[i].split(" ");
            double flyerInfo = Double.parseDouble(split[0]);
            double wt = 0.00;
            
            // if the passenger's time of arrival > counter's time, then he doesn't have to wait, there's idle time. otherwise wait
            if(flyerInfo > cScan[cServer]){
                cScan[cServer] = flyerInfo;
            }
            else{
                coachSWT[cServer] += cScan[cServer] - flyerInfo;
                wt = cScan[cServer] - flyerInfo;
            }
            
            double num = rand.nextDouble();
            double sLambda = (double)1/3;
            double screenTime = expon(sLambda, num);
            
            cScan[cServer] += screenTime;
            flyerInfo = cScan[cServer];
            
            // if this passenger is a commuter, go to the local gate queue. otherwise go to international queue
            if(split.length == 1){
                localGateQueue[localCount] = flyerInfo;
                localCount++;
            }
            else{
                double totalWait = Double.parseDouble(split[3]);
                totalWait += wt;
                internationalQueue[interCount] = flyerInfo + " " + split[1] + " " + split[2] + " " + totalWait;
                interCount++;
            }
            
            cServer++;
            if(cServer == cScanNum)
                cServer = 0;
            
        }
        
        // process the first class customers
        for(int i = 0; i < firstClassQueue.length; i++){
            String[] split = firstClassQueue[i].split(" ");
            double flyerInfo = Double.parseDouble(split[0]);
            double totalWait = Double.parseDouble(split[3]);
            double wt = 0.00;
            
            // if the passenger's time of arrival > counter's time, then he doesn't have to wait, there's idle time. otherwise wait
            if(flyerInfo > fScan[fServer]){
                fScan[fServer] = flyerInfo;
            }
            else{
                firstSWT[fServer] += fScan[fServer] - flyerInfo;
                wt = fScan[fServer] - flyerInfo;
            }
            
            double num = rand.nextDouble();
            double sLambda = (double)1/3;
            double screenTime = expon(sLambda, num);
            
            fScan[fServer] += screenTime;
            flyerInfo = fScan[fServer];
            
            totalWait += wt;
            internationalQueue[interCount] = flyerInfo + " " + split[1] + " " + split[2] + " " + totalWait;
            interCount++;
            
            fServer++;
            if(fServer == fScanNum)
                fServer = 0;
            
        }
        Arrays.sort(coachFlyerQueue, comp);
        Arrays.sort(internationalQueue, comp);
        // for(int i = 0; i < internationalQueue.length; i++){
        //     System.out.println(internationalQueue[i]);
        // }
        /*
        *
        *   Flyers are at the gate, check their time to see who makes it and who doesn't, who to charge and who not to charge (refund)
        *
        */
        double totalProfit = 0.00;
        double localProfit = 0.00;
        double interProfit = 0.00;
        
        double firstClassTicket = 1000.00;
        double coachTicket = 500.00;
        double commuterTicket = 200.00;
        
        double internationalCost = 10000.00;
        double commuterCost = 1000.00;
        double agentCost = 25.00;
        
        int internationalMissed = 0;
        
        // check the international flyers and see who missed the fligh and who made it. charge those who made it
        // if they missed, see if they arrived 90 minutes before the flight. if not charge them, otherwise refund
        for(int i = 0; i < internationalQueue.length; i++){
            
            // 0 is their time, 1 is flight, 2 is bags, 3 is first class/coach, 4 is total time spent at the airport from check-in to security
            String[] flyerInfo = internationalQueue[i].split(" ");
            double timeCheck = Double.parseDouble(flyerInfo[0]);
            double flightNum = Double.parseDouble(flyerInfo[1]);
            String flyerClass = flyerInfo[2];
            double timeSpent = Double.parseDouble(flyerInfo[3]);
            
            // check their flight and see which flight they're on
            double takeOff = flightNum * 360;
            
            // if timeCheck is less than or equal to takeOff time, then the flyer made it, take their money
            if(timeCheck <= takeOff){
                // if flyer is coach
                if(flyerClass.equals("i")){
                    interProfit += coachTicket;
                }
                else{
                    interProfit += firstClassTicket;
                }
            }
            
            // if they didn't make it, refund (don't charge/add to profit) them if they got here 90 minutes before take off
            else{
                double arrivedBefore = takeOff - (timeCheck - timeSpent);
                // if they didn't make it and arrived less than 90 minutes before takeoff, charge them accordingly
                if(arrivedBefore < 90){
                    
                    if(flyerClass.equals("i")){
                        interProfit += coachTicket;
                    }
                    else{
                        interProfit += firstClassTicket;
                    }
                    
                }
                internationalMissed++;
            }
            
        }
        
        // calculate the statas of the local flyers
        localProfit = localGateQueue.length * commuterTicket;
        int localFlights = (int)(cFlightTime + 30)/60;
        int localFly = localFlights * 50;
        int localMiss = Math.max(localFly, localGateQueue.length) - Math.min(localFly, localGateQueue.length);
        
        // calculate how much money was spend on the check-in agents
        double wages = (int)(simTime/60) * (25.00 * (double)(c + f + cScanNum + fScanNum));
        
        localProfit = localProfit - (localFlights * commuterCost);
        interProfit = interProfit - (numOfPlanes * internationalCost);
        totalProfit = localProfit + interProfit - wages;
        
        double totalCWaitTime = 0;
        for(int i = 0; i < coachWT.length; i++){
            totalCWaitTime += coachWT[i];
        }
        
        double totalFWaitTime = 0;
        for(int i = 0; i < firstWT.length; i++){
            totalFWaitTime += firstWT[i];
        }
        
        double totalCSWaitTime = 0;
        for(int i = 0; i < coachSWT.length; i++){
            totalCSWaitTime += coachSWT[i];
        }
        
        double totalFSWaitTime = 0;
        for(int i = 0; i < firstSWT.length; i++){
            totalFSWaitTime += firstSWT[i];
        }
        
        double totalAgentIdle = 0;
        for(int i = 0; i < cAgentIdleTime.length; i++){
            totalAgentIdle += cAgentIdleTime[i];
        }
        for(int i = 0; i < fAgentIdleTime.length; i++){
            totalAgentIdle += fAgentIdleTime[i];
        }
        double avgAgentIdle = (totalAgentIdle / (firstClassQueue.length + coachFlyerQueue.length)) / (c + f);
        double percentMissed = (double)internationalMissed/(double)internationalQueue.length;
        
        System.out.println();
        System.out.println("Total commuter flights: " + localFlights);
        System.out.println("Total commuters: " + localGateQueue.length);
        System.out.println("Total commuters missed: " + localMiss);
        System.out.println("Commuter flight profits: $" + localProfit);
        System.out.println();
        System.out.println("Total international flights: " + numOfPlanes);
        System.out.println("International flight profits: $" + interProfit);
        System.out.println();
        System.out.println("Wages paid to check-in agents: $" + wages);
        System.out.println("Total profit: $" + totalProfit);
        System.out.println("% wages out of total profit: " + (wages/totalProfit * 100) +"%");
        System.out.println();
        System.out.println("Total wait time at check-in for coach class: " + totalCWaitTime);
        System.out.println("Average wait time at check-in per flyer for coach class: " + ((totalCWaitTime/coachFlyerQueue.length)/c));
        System.out.println();
        System.out.println("Total wait time at check-in for first class: " + totalFWaitTime);
        System.out.println("Average wait time at check-in per flyer for first class: " + ((totalFWaitTime/firstClassQueue.length)/f));
        System.out.println();
        System.out.println("Total agent idle time: " + totalAgentIdle);
        System.out.println("Idle percentage: " + (avgAgentIdle * 100) + "%");
        System.out.println();
        System.out.println("Total wait time at security for coach class: " + totalCSWaitTime);
        System.out.println("Average wait time at security per flyer for coach class: " + ((totalCSWaitTime/coachFlyerQueue.length))/cScanNum);
        System.out.println();
        System.out.println("Total wait time at security for first class: " + totalFSWaitTime);
        System.out.println("Average wait time at security per flyer for first class: " + ((totalFSWaitTime/firstClassQueue.length))/fScanNum);
        System.out.println();
        System.out.println("Total international flyers missed flight: " + internationalMissed);
        System.out.println("Total international flyers boarded flight: " + (internationalQueue.length - internationalMissed));
        System.out.println("Percent international flyers missed flight: " + (percentMissed * 100) + "%");
        
    }
    
    static double calcProcessTime(int iterator, int[] bags){
        double num = rand.nextDouble();
        double printPass = expon(0.5, num);
        
        double checkBags = 0;
        if(bags[iterator] != 0){
            for(int i = 0; i < bags[iterator]; i++){
                num = rand.nextDouble();
                checkBags += expon(1.0, num);
            }
        }
        
        num = rand.nextDouble();
        double ltemp = (double)1/3;
        double others = expon(ltemp, num);
        
        double processTime = printPass + checkBags + others;
        return processTime;
    }
    
    // decide whether a seat on the international flight is filled or not
    static double fillSeat(double rate, double num){
        if(num <= rate)
            return 1.0;
        
        else
            return 0.0;
    }
    
    static double expon(double lambda, double randNum){
        double result = Math.log(1 - randNum);
        result = result / -lambda;
        return result;
    }
    
    // calculate the x in Z value, which is z*sqrt(variance) + mu
    static double calcX(double zValue, double mu, double variance){
        return zValue * Math.sqrt(variance) + mu;
    }
    
    // calculate the z value given a normal distribution
    static double normal(double num){
        return Math.log(1/num - 1) / (-1.702);
    }
    
}