package pubsub.experiments.channel.subscriber;

import java.util.List;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import pubsub.util.XMLParser;

/**
 *
 * @author John Gasparis
 */
public class XMLChannelSubscriberParser extends XMLParser {
    
    private static final Logger logger = Logger.getLogger(XMLChannelSubscriberParser.class);
    
    public XMLChannelSubscriberParser(String fileName) {
        super(fileName);
    }
    
    public void printData() {
        List<XMLElement> nodeList = super.getNodeList();
        logger.debug("Number of Nodes => " + nodeList.size());
        
        XMLChannelNode node;
        for (XMLElement element : nodeList) {
            node = (XMLChannelNode) element;
            logger.debug("[" + node.getChannelName() + "] , [" + node.getDuration() + "]");
        }
    }
    
    @Override
    protected XMLElement getXMLElement(Element nodeElement) {
        String channelName;
        String duration;
        
        channelName = super.getTextValue(nodeElement, "name");
        duration = super.getTextValue(nodeElement, "duration");
        
        return new XMLChannelNode(channelName, duration);
    }
    
    public class XMLChannelNode implements XMLParser.XMLElement {
        private String channelName;
        private int duration;
        
        public XMLChannelNode(String channelName, String duration) throws NumberFormatException {
            this.channelName = channelName;
            this.duration = Integer.parseInt(duration);
        }

        public String getChannelName() {
            return channelName;
        }

        public int getDuration() {
            return duration;
        }
    }
}
