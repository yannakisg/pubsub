package pubsub.experiments.channel.publisher;

import java.util.List;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import pubsub.transport.channel.source.ChannelSource;
import pubsub.transport.channel.source.ChannelSource.ChannelType;
import pubsub.util.XMLParser;

/**
 *
 * @author John Gasparis
 */
public class XMLChannelPublisherParser extends XMLParser {

    private static final Logger logger = Logger.getLogger(XMLChannelPublisherParser.class);

    public XMLChannelPublisherParser(String fileName) {
        super(fileName);
    }
    
    public void printData() {
        List<XMLElement> nodeList = super.getNodeList();
        logger.debug("Number of Nodes => " + nodeList.size());
        
        XMLChannelNode node;
        for (XMLElement element : nodeList) {
            node = (XMLChannelNode) element;
            logger.debug("[" + node.getChannelName() + "] , [" + node.getBitRate() + "] , [" + node.getChannelType() + "]");
        }
    }

    @Override
    protected XMLElement getXMLElement(Element nodeElement) {
        String channelName;
        String bitRate;
        String channelType;

        channelName = super.getTextValue(nodeElement, "name");
        bitRate = super.getTextValue(nodeElement, "bitrate");
        channelType = super.getTextValue(nodeElement, "type");

        return new XMLChannelNode(channelName, bitRate, channelType);
    }

    public class XMLChannelNode implements XMLParser.XMLElement {

        private String channelName;
        private long bitRate;
        private ChannelSource.ChannelType channelType;

        public XMLChannelNode(String channelName, String bitRate, String channelType) throws NumberFormatException {
            this.bitRate = Long.parseLong(bitRate);
            this.channelName = channelName;
            channelType = channelType.toLowerCase();
            
            if (channelType.equals("unicast")) {
                this.channelType = ChannelSource.ChannelType.UNICAST;
            } else if (channelType.equals("multicast")) {
                this.channelType = ChannelSource.ChannelType.MULTICAST;
            } else if (channelType.equals("steiner_tree")) {
                this.channelType = ChannelSource.ChannelType.STEINER_TREE;
            } else {
                throw new IllegalArgumentException("Unknown Channel Type");
            }


        }

        public long getBitRate() {
            return bitRate;
        }

        public String getChannelName() {
            return channelName;
        }

        public ChannelType getChannelType() {
            return channelType;
        }
    }
}
