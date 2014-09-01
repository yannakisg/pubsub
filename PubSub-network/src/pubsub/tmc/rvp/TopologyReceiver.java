package pubsub.tmc.rvp;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import pubsub.ForwardIdentifier;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.net.tmc.InterestTopologyChunk;
import pubsub.messages.net.tmc.TopologyMessage;
import pubsub.tmc.graph.MyRVPNode;

/**
 *
 * @author John Gasparis
 */
public class TopologyReceiver {
    
    private static final Logger logger = Logger.getLogger(TopologyReceiver.class);
    private TopologyMessage[] messages;
    private MyRVPNode myNode;
    private ForwardIdentifier rvpToProxy;
    private LocRCClient locRCClient;
    private Timer timer;
    private final Lock lock = new ReentrantLock(true);
    private final int DELAY = 5000;
    private final int PERIOD = 5000;
    private int count = 0;
    private int totalSize = 0;

    public TopologyReceiver(MyRVPNode myNode, LocRCClient locRCClient) {
        this.myNode = myNode;
        this.locRCClient = locRCClient;
    }
    
    public void setRVPtoProxy(ForwardIdentifier fid) {
        this.rvpToProxy = fid;
    }

    public void deliverMessage(ByteBuffer buffer) {
        TopologyMessage msg = TopologyMessage.parseByteBuffer(buffer);

        lock.lock();
        try {
            if (messages == null) { 
                timer = new Timer();
                messages = new TopologyMessage[msg.getTotalChunks()];
                timer.scheduleAtFixedRate(new TransmitInterests(), DELAY, PERIOD);
            }
            
            if (messages[msg.getChunkNum()] == null) {
                count++;
                totalSize += msg.getArray().length;
            }
            
            logger.debug("Received Topology Chunk [" + msg.getChunkNum() + "]");
            messages[msg.getChunkNum()] = msg;
            
            if (count == messages.length) {
                timer.cancel();
                
                logger.debug("Topology was successfully received. Size => " + totalSize);
                myNode.saveTopology(messages, totalSize);
                count = 0;
                totalSize = 0;
                messages = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private class TransmitInterests extends TimerTask {

        @Override
        public void run() {
            InterestTopologyChunk interest = InterestTopologyChunk.createEmptyMessage();
            interest.setFID(rvpToProxy);
            interest.setRouterID(myNode.getID());
            
            for (int i = 0; i < messages.length; i++) {
                lock.lock();
                try {
                    if (messages[i] != null) {
                        logger.debug("Request Topology Chunk [" + i + "]");
                        interest.setChunkNum(i);
                        interest.publishMutableData(locRCClient, interest.toBytes());
                    } 
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
