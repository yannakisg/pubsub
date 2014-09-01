package pubsub.tmc.topology.steinertree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ByteIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.tmc.topology.WeightedAdjacencyMap;
import pubsub.tmc.topology.WeightedDijkstra;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class SteinerTree {

    private static final Logger logger = Logger.getLogger(SteinerTree.class);

    static {
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "steinerTree.log", false));
        } catch (IOException ex) {
        }
    }
    private MinSpanTree steinerTree;
    private WeightedAdjacencyMap topologyGraph;
    private ByteIdentifier[] steinerPoints;
    private Map<ByteIdentifier, WeightedDijkstra> mapDij;

    public SteinerTree(WeightedAdjacencyMap adjacencyMap) {
        this(adjacencyMap, null);
    }

    public SteinerTree(WeightedAdjacencyMap adjacencyMap, ByteIdentifier[] steinerPoints) {
        this.topologyGraph = adjacencyMap;
        this.mapDij = new HashMap<ByteIdentifier, WeightedDijkstra>();
        this.steinerPoints = steinerPoints;


        Set<ByteIdentifier> set = adjacencyMap.getAllGWs();//int i = 0;
        synchronized (adjacencyMap) {
            for (ByteIdentifier id : set) {
                WeightedDijkstra temp = new WeightedDijkstra(topologyGraph);
               // System.out.println(i++);
                temp.shortestPaths(id);
                mapDij.put(id, temp);
            }
        }
    }

    public void setSteinerPoints(ByteIdentifier[] steinerPoints) {
        this.steinerPoints = steinerPoints;
    }

    public MinSpanTree getSteinerTree() {
        return this.steinerTree;
    }

    public void createSteinerTree() {
      //  System.out.println("undirected");
        /* Step 1 : Construct the complete undirected distance Graph (G1) from Topology and SteinerPoints*/
        Graph undirectedDisGraph = createUndirectedDisGraph();
       // System.out.println("minspan0");
        /* Step 2 : Find the minimal spanning tree (T1) of G1 */
        MinSpanTree minSpanTreeOfDisGraph = createMinSpanTree(undirectedDisGraph);
      //   System.out.println("subgraph");
        /* Step 3 : Construct the subgraph (Gs) of Topology by replacing each edge in T1 
         *          by its corresponding shortest path in Topology
         */
        Graph subgraph = createSubgraph(minSpanTreeOfDisGraph);
     //    System.out.println("minspan1");
        /* Step 4 : Construct the Steiner Tree by finding the minimal spanning tree of Gs */
        steinerTree = createMinSpanTree(subgraph);
    }

    private Graph createUndirectedDisGraph() {
        Graph undirectedDisGraph = new Graph();

        for (int i = 0; i < steinerPoints.length; i++) {
            // wDijkstra.shortestPaths(steinerPoints[i]);

            for (int j = i; j < steinerPoints.length; j++) {
                if (!steinerPoints[j].equals(steinerPoints[i])) {
                    //  logger.debug("Adding Edge: " + steinerPoints[i] + "->" + steinerPoints[j]);
                    undirectedDisGraph.addEdge(steinerPoints[i], steinerPoints[j], mapDij.get(steinerPoints[i]).getCost(steinerPoints[j]));
                }
            }
        }

        return undirectedDisGraph;
    }

    private MinSpanTree createMinSpanTree(Graph graph) {
        MinSpanTree tree = new MinSpanTree(graph);
        // correctThePath(tree.getMinSpanEdges());
        return tree;
    }

    private Graph createSubgraph(MinSpanTree tree) {
        int i;
        double weight;
        ByteIdentifier v0, v1;
        Graph subgraph = new Graph();
        List<Edge> edges = tree.getMinSpanEdges();
        List<List<ByteIdentifier>> vertices = new ArrayList<List<ByteIdentifier>>();

        for (Edge edge : edges) {
            //wDijkstra.shortestPaths(edge.getSource());
            vertices.add(mapDij.get(edge.getSource()).getPathOfNodes(edge.getSource(), edge.getTarget()));
        }

        for (List<ByteIdentifier> list : vertices) {
            for (i = 0; i < list.size() - 1; i++) {
                v1 = list.get(i);
                v0 = list.get(i + 1);
                weight = topologyGraph.findEdgeWeight(v0, v1);
                //  logger.debug("Adding Edge(subgraph): " + v0 + "->" + v1);
                subgraph.addEdge(v0, v1, weight);
            }
        }

        return subgraph;
    }

    private void correctThePath(List<Edge> edges) {
        List<Edge> temp = new ArrayList<Edge>(edges);
        convertToDirectedGraph(temp, steinerPoints[0]);
    }

    private void convertToDirectedGraph(List<Edge> edges, ByteIdentifier source) {
        List<Edge> retEdges = findEdges(edges, source);
        if (retEdges.isEmpty()) {
            logger.debug("Unknown Edge");
            return;
        }
        // System.out.println("SOURCE: " + source);
        for (Edge e : retEdges) {
            // System.out.println("Source: " + e.getSource());
            //  System.out.println("Target: " + e.getTarget());

            edges.remove(e);
            if (e.getTarget().equals(source)) {
                //  System.out.println("Swap");
                e.swapEndpoints();
            }
            convertToDirectedGraph(edges, e.getTarget());
        }
    }

    private static List<Edge> findEdges(List<Edge> edges, ByteIdentifier id) {
        List<Edge> retEdges = new ArrayList<Edge>();
        for (Edge e : edges) {
            if (e.getSource().equals(id) || e.getTarget().equals(id)) {
                retEdges.add(e);
            }
        }

        return retEdges;
    }

    public static void main(String args[]) throws FileNotFoundException {
        String fileName = "/home/gaspar/Desktop/USB/3356.parsed";
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        String line;
        String str[];
        int v0, v1;
        ByteIdentifier id0, id1;
        ByteIdentifier myNodeID = null;
        BloomFilter myVlid = BloomFilter.createRandom(32, 6);
        WeightedAdjacencyMap map = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            str = line.split(":");
            v0 = Integer.parseInt(str[0]);
            v1 = Integer.parseInt(str[1]);

            id0 = new ByteIdentifier(Util.intToByteArray(v0));
            id1 = new ByteIdentifier(Util.intToByteArray(v1));
            if (myNodeID == null) {
                myNodeID = id0;
                map = new WeightedAdjacencyMap(myNodeID, myVlid);
            }

            map.addLink(id0, id1, BloomFilter.createRandom(32, 6), BloomFilter.createRandom(32, 6), 1.0);

        }
        int mode = 1;

        if (mode == 0) {
            Set<ByteIdentifier> gw = map.getAllGWs();
            ByteIdentifier src = null;//gw.iterator().next();

            Iterator<ByteIdentifier> iter = gw.iterator();
            ByteIdentifier node;
            while (iter.hasNext()) {
                node = iter.next();

                if (src == null) {
                    src = node;
                    map.getWeightedDijkstra().shortestPaths(src);
                } else {
                    Stack<ByteIdentifier> stack = map.getWeightedDijkstra().getPathOfNodes(src, node);
                    for (ByteIdentifier id : stack) {
                        if (!id.equals(src)) {
                            System.out.print(Util.byteArrayToInt(id.getId()) + ":");
                        } else {
                            System.out.print(Util.byteArrayToInt(id.getId()));
                        }
                    }
                    System.out.println();
                }
            }

        } else if (mode == 1) {

            List<int[]> list = new ArrayList<int[]>();
            
            System.out.println("Constructor");
            SteinerTree tree = new SteinerTree(map);
            int k = 0;
            File f = new File("/home/gaspar/temp");
            scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                //for (int[] array : list) {
                line = scanner.nextLine();
                line = line.trim();
                str = line.split(",");
                ByteIdentifier steinerPoints[] = new ByteIdentifier[str.length];
                for (int i = 0; i < str.length; i++) {
                    steinerPoints[i] = new ByteIdentifier(Util.intToByteArray(Integer.parseInt(str[i])));
                }


                tree.setSteinerPoints(steinerPoints);
                tree.createSteinerTree();
                MinSpanTree mTree = tree.getSteinerTree();
                for (Edge edge : mTree.getMinSpanEdges()) {
                    System.out.println(Util.byteArrayToInt(edge.getSource().getId()) + ":" + Util.byteArrayToInt(edge.getTarget().getId()));
                }
                System.out.println("-------");
                k++;
                if (k == 10) {
                    k = 0;
                    System.out.println("#######");
                }
            }
        }

    }
}
