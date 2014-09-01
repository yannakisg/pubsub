package pubsub.tmc.topology;

import java.util.Collection;
import pubsub.tmc.graph.Link;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import org.apache.log4j.Logger;
import pubsub.ByteIdentifier;
import pubsub.tmc.TMCUtil;

/**
 *
 * @author John Gasparis
 */
public class WeightedDijkstra {

    private final Map<ByteIdentifier, Double> costMap;
    private final Map<ByteIdentifier, ByteIdentifier> predecessors;
    private final WeightedAdjacencyMap topology;
    private final List<DestEntry> costList;
    private static Logger logger = Logger.getLogger(WeightedDijkstra.class);

    public WeightedDijkstra(WeightedAdjacencyMap topology) {
        this.topology = topology;
        this.costMap = new HashMap<ByteIdentifier, Double>();
        this.predecessors = new HashMap<ByteIdentifier, ByteIdentifier>();
        this.costList = new LinkedList<DestEntry>();
    }
    
    private void debug() {
        Iterator<Entry<ByteIdentifier, Double>> entryIter = costMap.entrySet().iterator();
        Entry<ByteIdentifier, Double> entry;

        while (entryIter.hasNext()) {
            entry = entryIter.next();

            String msg = "Cost => " + entry.getValue() + "\nName: ";
            TMCUtil.debugByteArray(logger, msg, entry.getKey().getId());
        }
    }

    public Stack<ByteIdentifier> getPathOfNodes(ByteIdentifier src, ByteIdentifier dest) {
        Stack<ByteIdentifier> links = new Stack<ByteIdentifier>();
        ByteIdentifier prev = dest;
        ByteIdentifier temp;

        // logger.debug("Push " + dest);
        links.push(dest);

        while ((temp = predecessors.get(prev)) != null && (!temp.equals(src))) {
            links.push(temp);
            //    logger.debug("Push " + temp);
            prev = temp;
        }
        links.push(src);
        // logger.debug("Push " + src);

        return links;
    }

    private void initialize(ByteIdentifier src) {
        costMap.clear();
        predecessors.clear();
        costList.clear();

        Set<ByteIdentifier> keySet = topology.getAllNodes();
        ByteIdentifier id;
        synchronized (topology.getTopology()) {
            Iterator<ByteIdentifier> iter = keySet.iterator();

            while (iter.hasNext()) {
                id = iter.next();
                costMap.put(id, Double.MAX_VALUE);

                if (id.equals(src)) {
                    costList.add(new DestEntry(0.0, id));
                } else {
                    costList.add(new DestEntry(Double.MAX_VALUE, id));
                }
            }
        }

        costMap.put(src, 0.0);
        Collections.sort(costList);
    }

    public void shortestPaths(ByteIdentifier src) {
        Set<ByteIdentifier> Q = new HashSet<ByteIdentifier>();
        Collection<Link> neighbors = null;
        ByteIdentifier nodeID;
        double alt;

        initialize(src);
        
        Set<ByteIdentifier> nodes = topology.getAllNodes();
        synchronized (topology.getTopology()) {
            Iterator<ByteIdentifier> iter = nodes.iterator();
            while (iter.hasNext()) {
                Q.add(iter.next());
            }
        }

        while (!Q.isEmpty()) {
            nodeID = costList.remove(0).getNodeID();
            if (costMap.get(nodeID) == Double.MAX_VALUE) {
                break;
            }

            Q.remove(nodeID);

            Map<ByteIdentifier, Link> map = topology.getTopology().get(nodeID);
            neighbors = map.values();
            if (neighbors != null) {
                synchronized (map) {
                    Iterator<Link> iter = neighbors.iterator();
                    Link path;
                    while (iter.hasNext()) {
                        path = iter.next();
                        if (Q.contains(path.getEndpoint().getID())) {
                            alt = costMap.get(nodeID) + path.getCost();
                            if (/*!costMap.containsKey(path.getEndpoint().getID()) || */
                                alt < costMap.get(path.getEndpoint().getID())) {
                                costMap.put(path.getEndpoint().getID(), alt);
                                updateCostList(path.getEndpoint().getID(), alt);
                                predecessors.put(path.getEndpoint().getID(), nodeID);
                            }
                        }
                    }
                }
            }
            Collections.sort(costList);
        }

        //  debug();
    }

    public WeightedAdjacencyMap getTopology() {
        return this.topology;
    }

    public double getCost(ByteIdentifier nodeID) {
        return costMap.get(nodeID);
    }

    private void updateCostList(ByteIdentifier nodeID, double cost) {
        for (DestEntry entry : costList) {
            if (entry.getNodeID().equals(nodeID)) {
                entry.setCost(cost);
                return;
            }
        }
        costList.add(new DestEntry(cost, nodeID));
    }

    private class DestEntry implements Comparable<DestEntry> {

        private double cost;
        private final ByteIdentifier nodeID;

        public DestEntry(double cost, ByteIdentifier nodeID) {
            this.cost = cost;
            this.nodeID = nodeID;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getCost() {
            return cost;
        }

        public ByteIdentifier getNodeID() {
            return nodeID;
        }

        @Override
        public int compareTo(DestEntry o) {
            if (cost < o.cost) {
                return -1;
            } else if (cost > o.cost) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
