/*************************************************************************
 *  Compilation:  javac DijkstraAllPairsSP.java
 *  Dependencies: EdgeWeightedDigraph.java Dijkstra.java
 *
 *  Dijkstra's algorithm run from each vertex. 
 *  Takes time proportional to E*V*log(V) and space proportional to EV (lecture says V).
 *  In Floyd's all pair shortest path:
 *  run time of V^3 and space requirement of V^2 because it uses an adjacency matrix
 *
 *************************************************************************/

public class DijkstraAllPairsSP {
    private DijkstraSP[] all;

    public DijkstraAllPairsSP(EdgeWeightedDigraph G) {
        all  = new DijkstraSP[G.V()];
        for (int v = 0; v < G.V(); v++)
            all[v] = new DijkstraSP(G, v);
    }

    public Iterable<DirectedEdge> path(int s, int t) {
        return all[s].pathTo(t);
    }

    public double dist(int s, int t) {
        return all[s].distTo(t);
    }
    
    public static void main(String[] args){
        In in = new In(args[0]);
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
        
        DijkstraAllPairsSP allpairs = new DijkstraAllPairsSP(G);
        
        for(int s = 0; s < G.V(); s++) {
           for (int t = 0; t < G.V(); t++) {
               if (allpairs.path(s,t) != null) {
                   StdOut.printf("%d to %d (%.2f)  ", s, t, allpairs.dist(s,t));
                   if (allpairs.path(s,t) != null) {
                       for (DirectedEdge e : allpairs.path(s,t)) {
                           StdOut.print(e + "   ");
                       }
                   }
                   StdOut.println();
               }
               else {
                   StdOut.printf("%d to %d         no path\n", s, t);
               }
           }
           StdOut.println();
        }
    }
}
