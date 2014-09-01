package pubsub.tmc;

import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import org.apache.log4j.PatternLayout;
import pubsub.ACKHandler;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.LinkEstablishAnnouncement;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.ipc.tmc.DefaultGWMessage;
import pubsub.messages.net.tmc.HelloMessage;
import pubsub.tmc.graph.GatewayNode;
import pubsub.tmc.graph.Node;

/**
 *
 * @author John Gasparis
 */
public abstract class TMCPublisher {

    protected static Logger logger = Logger.getLogger(TMCPublisher.class);
    protected LocRCClient locRCClient;
    protected Node myNode;
    protected ACKHandler handler;
    
    public TMCPublisher() {
        handler = new ACKHandler();
        handler.start();
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "tmcPub.log", false));
        } catch (IOException ex) {
        }
    }

    public void publishHelloMsg(LinkEstablishAnnouncement lea) {
        ForwardIdentifier fid = new ForwardIdentifier(BloomFilter.OR(lea.getLID(), lea.getVLID()), (short) 1);
        HelloMessage message = new HelloMessage(TMComponentFactory.TMC_MODE, myNode.getID(), myNode.getVLID(), fid);

        message.publishMutableData(locRCClient, message.toBytes());

      //  logger.debug("Published Hello Message");
        handler.addEntry(message);
    }

    public void publishDefaultGateway(GatewayNode gatewayNode) {
        DefaultGWMessage msg = new DefaultGWMessage(gatewayNode);

        msg.publishMutableData(locRCClient, msg.toBytes());
      //  logger.debug("Published default Gateway");
    }
}
