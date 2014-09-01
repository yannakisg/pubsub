package pubsub.node.controller.xml;

import java.util.ArrayList;
import java.util.List;
import pubsub.util.XMLParser;

/**
 *
 * @author John Gasparis
 */
public class XMLNode implements XMLParser.XMLElement{

    private String type;
    private String name;
    private String ip;
    private double x;
    private double y;    
    private List<AttachedNode> attNodes;

    public XMLNode(String type, String name, String ip, double x, double y) {
        this.type = type;
        this.name = name;
        this.ip = ip;
        this.x = x;
        this.y = y;
        this.attNodes = new ArrayList<AttachedNode>();
    }

    public void addConnectionNode(String conNodeName, int port, double weight) {
        attNodes.add(new AttachedNode(conNodeName, port, weight));
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;        
    }

    public String getName() {
        return this.name;
    }

    public String getIP() {
        return this.ip;
    }

    public String getType() {
        return this.type;
    }

    public List<AttachedNode> getAttachedNodes() {
        return this.attNodes;
    }

    public class AttachedNode {

        private String conNodeName;
        private int port;
        private double weight;

        public AttachedNode(String conNodeName, int port, double weight) {
            this.conNodeName = conNodeName;
            this.port = port;
            this.weight = weight;
        }

        public String getConnectionNodeName() {
            return this.conNodeName;
        }

        public int getPort() {
            return this.port;
        }
        
        public double getWeight() {
            return this.weight;
        }
    }
}
