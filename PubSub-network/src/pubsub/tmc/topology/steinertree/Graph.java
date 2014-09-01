package pubsub.tmc.topology.steinertree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import pubsub.ByteIdentifier;
import pubsub.util.SortedList;

/**
 *
 * @author John Gasparis
 */
public class Graph {

    private Map<ByteIdentifier, Map<ByteIdentifier, Double>> graphMap;
    private Set<Edge> graphEdges;
    private List<Edge> sortedList = null;
    private boolean toSort = false;

    public Graph() {
        graphMap = new HashMap<ByteIdentifier, Map<ByteIdentifier, Double>>();
        graphEdges = new HashSet<Edge>();
    }

    public void addEdge(ByteIdentifier v0, ByteIdentifier v1, double weight) {
        Map<ByteIdentifier, Double> adjacencies0 = createAdjacenciesList(v0);
        Map<ByteIdentifier, Double> adjacencies1 = createAdjacenciesList(v1);
        Edge edge = new Edge(v0, v1, weight);

        adjacencies0.put(v1, weight);
        adjacencies1.put(v0, weight);

        graphEdges.add(edge);
        toSort = true;
        //if (!graphEdges.contains(edge)) {
        //  graphEdges.add(edge);
        //}        
    }

    private Map<ByteIdentifier, Double> createAdjacenciesList(ByteIdentifier v) {
        Map<ByteIdentifier, Double> adjacencies = graphMap.get(v);

        if (adjacencies == null) {
            adjacencies = new HashMap<ByteIdentifier, Double>();
            graphMap.put(v, adjacencies);
        }

        return adjacencies;
    }

    public void debug() {
        for (Entry<ByteIdentifier, Map<ByteIdentifier, Double>> entry : graphMap.entrySet()) {
            System.out.println(entry.getKey().toString());

            for (Entry<ByteIdentifier, Double> e : entry.getValue().entrySet()) {
                System.out.println("\t" + e.getKey() + " : " + e.getValue());
            }
        }
    }

    public double findEdgeWeight(ByteIdentifier v0, ByteIdentifier v1) throws NullPointerException {
        return graphMap.get(v0).get(v1);
    }

    public Map<ByteIdentifier, Double> getAdjacencies(ByteIdentifier v) {
        return graphMap.get(v);
    }

    public Set<ByteIdentifier> getVertices() {
        return this.graphMap.keySet();
    }

    public int getSize() {
        return this.graphMap.size();
    }

    public int getTotalEdges() {
        return graphEdges.size();
    }

    public Set<Edge> getGraphEdges() {
        return graphEdges;
    }

    public List<Edge> getSortedGraphEdges() {
        if (toSort || sortedList == null) {
            sortedList = new ArrayList<Edge>(graphEdges);
            Collections.sort(sortedList, new Comparator<Edge>() {

                @Override
                public int compare(Edge e1, Edge e2) {
                    return Double.compare(e1.getWeight(), e2.getWeight());
                }
            });
        }
        return sortedList;
    }
}
