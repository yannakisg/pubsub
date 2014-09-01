package pubsub.tmc.router;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.net.tmc.TopologyMessage;
import pubsub.tmc.topology.WeightedAdjacencyMap;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public class TopologySender extends StoppableThread {

    private static final Logger logger = Logger.getLogger(TopologySender.class);
    private boolean canSend;
    private final Lock lock = new ReentrantLock();
    private final Condition readyToSend = lock.newCondition();
    private WeightedAdjacencyMap adjacencyMap;
    private ByteIdentifier myRouterID;
    private ForwardIdentifier proxyToRVP;
    private LocRCClient locRCCLient;
    private final int CHUNK_SIZE = 3800;
    private TopologyMessage msg;
    private ByteBuffer buffer;
    private int size;

    public TopologySender(WeightedAdjacencyMap adjacencyMap, ByteIdentifier myRouterID, LocRCClient locRCClient) {
        setName("TopologySender");

        this.canSend = false;
        this.adjacencyMap = adjacencyMap;
        this.myRouterID = myRouterID;
        this.locRCCLient = locRCClient;
        this.msg = TopologyMessage.createEmptyTopologyMsg();
    }

    public void setProxyToRVP(ForwardIdentifier fid) {
        this.proxyToRVP = fid;
    }

    @Override
    public void run() {
        int i;
        int n;
        int newLimit;
        int newPosition;

        while (!isShutDown()) {
            lock.lock();

            try {
                while (!canSend) {
                    readyToSend.await();
                }
            } catch (InterruptedException iex) {
                logger.error(iex.getMessage(), iex);
            } finally {
                this.canSend = false;
                lock.unlock();
            }


            if (proxyToRVP == null) {
                logger.debug("ProxyToRVP is null. Continue...");
                continue;
            }

            size = adjacencyMap.getSerializedLength();
            buffer = ByteBuffer.allocate(size);
            adjacencyMap.writeTo(buffer);

            n = size / CHUNK_SIZE;
            if (size % CHUNK_SIZE != 0) {
                n++;
            }
            msg.setTotalChunks(n);
            msg.setRouterID(myRouterID);
            msg.setFID(proxyToRVP);

            logger.debug("Size => " + size + "\nTotal Chunks => " + n);

            for (i = 0; i < n; i++) {
                logger.debug("Publish Topology Chunk [" + i + "]");
                newPosition = i * CHUNK_SIZE;

                newLimit = newPosition + CHUNK_SIZE;

                if (newLimit >= size) {
                    newLimit = size;
                }
                buffer.limit(newLimit);

                msg.setArray(Arrays.copyOfRange(buffer.array(), newPosition, newLimit));
                msg.setChunkNum(i);

                msg.publishMutableData(locRCCLient, msg.toBytes());
            }
        }
    }

    public void wakeUp() {
        lock.lock();
        try {
            logger.debug("Let's wake up the topology sender");
            this.canSend = true;
            this.readyToSend.signal();
        } finally {
            lock.unlock();
        }
    }

    public void sendChunk(int chunkNum) {
        int newLimit;
        int newPosition;

        lock.lock();
        try {

            newPosition = chunkNum * CHUNK_SIZE;

            newLimit = newPosition + CHUNK_SIZE;
            if (newLimit >= size) {
                newLimit = size;
            }

            msg.setArray(Arrays.copyOfRange(buffer.array(), newPosition, newLimit));
            msg.setChunkNum(chunkNum);

            logger.debug("Publish Topology Chunk [" + chunkNum + "]");

            msg.publishMutableData(locRCCLient, msg.toBytes());
        } finally {
            lock.unlock();
        }
    }
}
