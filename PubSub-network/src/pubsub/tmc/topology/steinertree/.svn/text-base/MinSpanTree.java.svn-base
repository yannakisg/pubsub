package pubsub.tmc.topology.steinertree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pubsub.ByteIdentifier;

/**
 *
 * @author John Gasparis
 */
public class MinSpanTree {

    private List<Edge> minSpanEdges;
    
    private static Set<ByteIdentifier> trgVertices = new HashSet<ByteIdentifier>();
    
    public MinSpanTree(Graph graph) {
        this.minSpanEdges = new ArrayList<Edge>();
        Kruskal.computeMinSpanTree(graph, minSpanEdges);
        
        trgVertices.clear();
        
        for (Edge edge : minSpanEdges) {
            if (!trgVertices.add(edge.getTarget())) {
                
            }
        }
    }

    public List<Edge> getMinSpanEdges() {
        return this.minSpanEdges;
    }

    public void debug() {
        for (Edge edge : minSpanEdges) {
            System.out.println("\t" + edge.toString());
        }
    }
}
