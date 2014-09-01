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
public class Kruskal {

    private static List<Set<ByteIdentifier>> vertexGroups = new ArrayList<Set<ByteIdentifier>>();

    public static void computeMinSpanTree(Graph graph, List<Edge> minSpanEdges) {
        List<Edge> graphEdges = graph.getSortedGraphEdges();
        ByteIdentifier endpoint0;
        ByteIdentifier endpoint1;
        Set<ByteIdentifier> vGroupSrc;
        Set<ByteIdentifier> vGroupTrg;

        init(minSpanEdges);

        for (Edge edge : graphEdges) {
            endpoint0 = edge.getSource();
            endpoint1 = edge.getTarget();

            vGroupSrc = getVertexGroup(endpoint0);
            vGroupTrg = getVertexGroup(endpoint1);
            
            if (vGroupSrc == null) {
                minSpanEdges.add(edge);

                if (vGroupTrg == null) {
                    vGroupTrg = new HashSet<ByteIdentifier>();
                    vGroupTrg.add(endpoint0);
                    vGroupTrg.add(endpoint1);
                    vertexGroups.add(vGroupTrg);
                } else {
                    vGroupTrg.add(endpoint0);
                }
            } else {
                if (vGroupTrg == null) {
                    vGroupSrc.add(endpoint1);
                    minSpanEdges.add(edge);
                } else if (!vGroupTrg.equals(vGroupSrc)) {
                    vGroupSrc.addAll(vGroupTrg);
                    vertexGroups.remove(vGroupTrg);
                    minSpanEdges.add(edge);
                }
            }
        }
    }

    private static void init(List<Edge> minSpanEdges) {
        minSpanEdges.clear();
        vertexGroups.clear();
    }

    private static Set<ByteIdentifier> getVertexGroup(ByteIdentifier vertex) {
        for (Set<ByteIdentifier> vGroup : vertexGroups) {
            if (vGroup.contains(vertex)) {
                return vGroup;
            }
        }
        return null;
    }
}
