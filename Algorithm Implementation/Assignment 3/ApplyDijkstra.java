public class ApplyDijkstra{
	
	public static void main(String[] args){
		
		// Read in the G''
		int totalV = StdIn.readInt();
		//StdOut.println(totalV); // for debugging only
		EdgeWeightedDigraph G = new EdgeWeightedDigraph(totalV);
		
		int edges = StdIn.readInt();
		if (edges < 0) throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
		//StdOut.println(edges);//for debugging only
		
		// Stream in the graph data
		for (int i = 0; i < edges; i++) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            if (v < 0 || v >= G.V()) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (G.V()-1));
            if (w < 0 || w >= G.V()) throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (G.V()-1));
            double weight = StdIn.readDouble();
            G.addEdge(new DirectedEdge(v, w, weight));
        }
        
        //StdOut.println(G); // For debugging only
        
        // Read in the array of vertex weights
        int arrLength = StdIn.readInt();
        double[] vWeight = new double[arrLength];
        
        for(int i = 0; i < arrLength; i++){
	        double vertWeights = StdIn.readDouble();
	        vWeight[i] = vertWeights;
        }
        
		int s = Integer.parseInt(args[0]);
		
		// Calculate the shortest path using Dijkstra and recalibrate the edge weights
		DijkstraSP sp = new DijkstraSP(G, s);
		for (int t = 0; t < G.V(); t++) {
            if (sp.hasPathTo(t)) {
            
                //StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
                double totalWeight = 0;
				StringBuilder outputEdges = new StringBuilder();
                if (sp.hasPathTo(t)) {
					
                    for (DirectedEdge e : sp.pathTo(t)) {
	                    int to = e.to();
	                    int from = e.from();
	                    double weight = e.weight();
	                    double newWeight = edgeWeightCalculation(weight, vWeight[from], vWeight[to]);
						totalWeight = totalWeight + newWeight;
	                    e = new DirectedEdge(from, to, newWeight);
                        //StdOut.print(e + "   ");
						outputEdges.append(e + "   ");
						
                    }
                    
                }
				StdOut.printf("%d to %d (%.2f)  ", s, t, totalWeight);
				StdOut.print(outputEdges);
                StdOut.println();
                
            }
            else {
                StdOut.printf("%d to %d         no path\n", s, t);
            }
        }
		
	}
	
	public static double edgeWeightCalculation(double edgeWeight, double from_vWeight, double to_vWeight){
		
		double newEdgeWeight = edgeWeight + to_vWeight - from_vWeight;
		return newEdgeWeight;
		
	}
	
}