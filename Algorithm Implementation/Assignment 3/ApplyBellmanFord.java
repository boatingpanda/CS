public class ApplyBellmanFord{
	
	public static void main(String[] args){
		
		// Read in the old graph 1
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
		//StdOut.println(G); // for debugging only
		
		// Read in G'
		totalV = StdIn.readInt();
		//StdOut.println(totalV); // for debugging only
		EdgeWeightedDigraph G2 = new EdgeWeightedDigraph(totalV);
		
		edges = StdIn.readInt();
		if (edges < 0) throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
		//StdOut.println(edges);//for debugging only
		
		// Stream in the graph data
		for (int i = 0; i < edges; i++) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            if (v < 0 || v >= G2.V()) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (G.V()-1));
            if (w < 0 || w >= G2.V()) throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (G.V()-1));
            double weight = StdIn.readDouble();
            G2.addEdge(new DirectedEdge(v, w, weight));
        }
		//StdOut.println(G2); // for debugging only
		
		EdgeWeightedDigraph G3 = new EdgeWeightedDigraph(G.V()); // Create an empty graph with G.V number of vertices
		
		int s = Integer.parseInt(args[0]);
		//StdOut.println(s); // for debugging only
		BellmanFordSP spFromNewVert = new BellmanFordSP(G2, s);
		
		// Check whether this graph has a negative cycle, abort if true.
		if(spFromNewVert.hasNegativeCycle()){
			StdOut.println("This graph has a negative cycle, aborting process!");
			System.exit(0);
		}
		
		// Otherwise update graph G's edge weights by creating a new graph with the updated edge weights
		else{
		
			double[] vWeight = new double[G.V()];
			for(int v = 0; v < G.V(); v++){
				vWeight[v] = spFromNewVert.distTo(v); // Set vWeight[] to the edge weight of edges from 6, this will be used for the new graph's edge weight
			}
			
			// Calculate the new edge weight using edgeWeighCalculation function and add it to graph 3
			Iterable<DirectedEdge> oldEdges = G.edges();
		
			for(DirectedEdge e : oldEdges){
				int from = e.from();
				int to = e.to();
				double oldWeight = e.weight();
				double weight = edgeWeightCalculation(oldWeight, vWeight[from], vWeight[to]);
				e = new DirectedEdge( from, to, weight);
				G3.addEdge(e);
			}
			
			StdOut.println(G3.toConsole());
			StringBuilder tempS = new StringBuilder();
			tempS.append(vWeight.length + " ");
			for(int i = 0; i < vWeight.length; i++){
				tempS.append(vWeight[i] + " ");
			}
			StdOut.println(tempS);
			
		}
		
	}
	
	public static double edgeWeightCalculation(double edgeWeight, double from_vWeight, double to_vWeight){
		
		double newEdgeWeight = edgeWeight + from_vWeight - to_vWeight;
		return newEdgeWeight;
		
	}
	
}