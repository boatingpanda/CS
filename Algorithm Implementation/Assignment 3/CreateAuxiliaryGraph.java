public class CreateAuxiliaryGraph{
	
	public static void main(String[]args){
		
		int totalV = StdIn.readInt();
		//StdOut.println(totalV); //for debugging only
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
		
		StdOut.println(G.toConsole()); // for debugging
		
		int newV = G.V() + 1;
		EdgeWeightedDigraph newG = new EdgeWeightedDigraph(newV);
		
		// Copy the edges of old graph over
		Iterable<DirectedEdge> oldEdges = G.edges();
		
		for(DirectedEdge e : oldEdges){
			newG.addEdge(e);
		}
			
		// Add new edges from new vertex to existing vertices
		for(int k = 0; k < G.V(); k++){
			DirectedEdge e = new DirectedEdge(newG.V()-1, k, 0.00);
			newG.addEdge(e);
		}
		
		StdOut.println(newG.toConsole());
		
	}
	
}