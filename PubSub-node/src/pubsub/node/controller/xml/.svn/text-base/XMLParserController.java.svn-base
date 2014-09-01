package pubsub.node.controller.xml;

import java.util.List;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pubsub.util.XMLParser;

/**
 *
 * @author John Gasparis
 */
public class XMLParserController extends XMLParser {

    private static final Logger logger = Logger.getLogger(XMLParserController.class);

    public XMLParserController(String fileName) {
        super(fileName);
    }

    public void printData() {
        List<XMLElement> nodeList = super.getNodeList();
        logger.debug("Number of Nodes => " + nodeList.size());
        
        XMLNode node;
        for (XMLElement element : nodeList) {
            node = (XMLNode) element;
            logger.debug("Node[" + node.getName() + "] , [" + node.getType() + "] , [" + node.getIP() + "]");

            for (XMLNode.AttachedNode attNode : node.getAttachedNodes()) {
                logger.debug("\tAttachNode[" + attNode.getConnectionNodeName() + "] , [" + attNode.getPort() + "] , [" + attNode.getWeight() + "]");
            }
        }
    }

    @Override
    protected XMLNode getXMLElement(Element element) {
        String type;
        String name;
        String ip;
        int port;
        double weight;
        double x;
        double y;
        String conNodeName;
        XMLNode node;
        NodeList nList;

        type = element.getAttribute("type");
        name = element.getAttribute("name");
        x = Double.parseDouble(element.getAttribute("x"));
        y = Double.parseDouble(element.getAttribute("y"));
        ip = super.getTextValue(element, "ip");
        
        node = new XMLNode(type, name, ip, x, y);

        nList = element.getElementsByTagName("connection");
        if (nList != null && nList.getLength() > 0) {
            for (int i = 0; i < nList.getLength(); i++) {
                Element elmnt = (Element) nList.item(i);

                conNodeName = elmnt.getAttribute("node");
                port = Integer.parseInt(elmnt.getAttribute("port"));
                weight = Double.parseDouble(elmnt.getAttribute("weight"));

                node.addConnectionNode(conNodeName, port, weight);
            }
        }

        return node;
    }
}
