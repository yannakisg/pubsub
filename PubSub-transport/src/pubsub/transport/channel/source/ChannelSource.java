package pubsub.transport.channel.source;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ByteIdentifier;
import pubsub.ContentType;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.bloomfilter.BloomFilter;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.net.rva.InstructChannelTransfer;
import pubsub.messages.net.rva.InstructUnsubscribe;
import pubsub.messages.net.transport.ChannelPacketInfo;
import pubsub.rva.RVS;
import pubsub.transport.Source;
import pubsub.transport.TransportUtil;
import pubsub.transport.api.channel.PacketInfoEntry;
import pubsub.util.StoppableThread;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class ChannelSource extends Source {

    public enum ChannelType {

        UNICAST,
        MULTICAST,
        STEINER_TREE
    }
    private DataProducer dataProducer;
    private Worker worker = null;
    private Subscription notification;
    private ChannelType chType;
    private RVAAnnouncement.RVPAction rvpAction;
    private boolean isClosed;
    private BloomFilter zeroBloomFilter;
    private PubSubID sid;
    private PubSubID rid;
    private String chName;
    private final Logger logger;
    private int count = 0;
    private int counter = 0;
    private ByteIdentifier procID;

    public ChannelSource(ChannelType chType, String chName, long bitRate, int chunkSize) {
        super();
        this.chName = chName;
        this.dataProducer = new DataProducer(bitRate, chunkSize);
        this.chType = chType;
        this.isClosed = false;

        if (chType == ChannelType.UNICAST) {
            rvpAction = RVAAnnouncement.RVPAction.CHANNEL_UNICAST;
        } else if (chType == ChannelType.MULTICAST) {
            rvpAction = RVAAnnouncement.RVPAction.CHANNEL_MULTICAST;
        } else {
            rvpAction = RVAAnnouncement.RVPAction.CHANNEL_STEINER_TREES;
        }

        ByteBuffer buffer = ByteBuffer.allocate(Util.SIZEOF_INT + Util.SIZEOF_INT);
        buffer.putInt(Util.getPID());
        buffer.putInt(Util.getRandomInteger());

        this.procID = new ByteIdentifier(Util.sha256toBytes(buffer.array()));

        logger = Logger.getLogger(chName);
        zeroBloomFilter = BloomFilter.createZero();
        setName(chName);
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), chName + ".log", false));
        } catch (IOException ex) {
        }
    }

    public void announcePublication(PubSubID sid, PubSubID rid, long lifeTime) {
        //logger.debug("announcePublication");
        // pendingTable.createEntry();
        this.sid = sid;
        this.rid = rid;
        notification = createNotification(sid, rid);

        RVS.publish(sid, rid, ContentType.CHANNEL, notification, rvpAction, lifeTime, procID, locRCClient);
        locRCClient.subscribe(notification);
    }

    @Override
    protected void processPublication(Publication pub) {
        MessageType.Type msgType = Message.getMessageType(pub.getByteAt(0));

        if (msgType == MessageType.Type.INSTRUCT_CHANNEL_MESSAGE) {
            //logger.debug("Received an InstructChannelTransfer Message");
            processRVPInstructMessage(pub.wrapData());
        } else if (msgType == MessageType.Type.INSTRUCT_UNSUBSCRIBE_MESSAGE) {
            //logger.debug("Received an InstructUnsubscribe Message");
            processInstructUnsubscribeMessage(pub.wrapData());
        }
    }

    private void processRVPInstructMessage(ByteBuffer byteBuffer) {
        if (worker == null) {
            worker = new Worker();
            worker.start();
        }

        InstructChannelTransfer rvpInstruct = InstructChannelTransfer.parseByteBuffer(byteBuffer);
        if (chType != ChannelType.STEINER_TREE) {
            //logger.debug("Rcv PubToSub: " + rvpInstruct.getPubToSub().getBloomFilter());
            logger.debug("Total Subscribers[sub]: " + addFid(rvpInstruct.getPubToSub(), rvpInstruct.getProcID()));
        } else {
            setFinalFid(rvpInstruct.getPubToSub());
        }
    }

    private void processInstructUnsubscribeMessage(ByteBuffer buffer) {
        InstructUnsubscribe instruct = InstructUnsubscribe.parseByteBuffer(buffer);
        if (chType != ChannelType.STEINER_TREE) {
            // logger.debug("Del PubToSub: " + instruct.getPubToSub().getBloomFilter());
            logger.debug("Total Subscribers[unsub]: " + removeFid(instruct.getPubToSub(), instruct.getProcID()));

            if (isEmpty()) {
                if (worker != null) {
                    worker.close();
                    worker = null;
                }
            }
        } else {
            // logger.debug("Del (SteinerTREE) PubToSub: " + instruct.getPubToSub().getBloomFilter());
            setFinalFid(instruct.getPubToSub());
            if (instruct.getPubToSub().getBloomFilter().equals(zeroBloomFilter)) {
                if (worker != null) {
                    worker.close();
                    worker = null;
                }
            }
        }
    }

    public void close() {
        if (!isClosed) {
            isClosed = true;

            if (worker != null) {
                worker.close();
            }

            this.shutDown();
            this.interrupt();
        }
    }

    private class Worker extends StoppableThread {

        private boolean isClosed = false;

        @Override
        public void run() {
            setName(chName + "/Worker");
            PacketInfoEntry packetInfo;
            ForwardIdentifier fid;
            byte[] data;
            long time;
            int id = Util.getRandomInteger();


            while (!isShutDown()) {

                time = System.currentTimeMillis();
                data = dataProducer.produce();
                packetInfo = new PacketInfoEntry(sid, rid, new ChannelPacketInfo(id, data));

                if (chType == ChannelType.UNICAST) {
                    Map<ForwardIdentifier, Set<ByteIdentifier>> map = getMap();
                    Set<ForwardIdentifier> keySet = map.keySet();
                    synchronized (map) {
                        Iterator<ForwardIdentifier> iter = keySet.iterator();

                        while (iter.hasNext()) {
                            counter++;
                            logger.debug("Counter: " + counter);
                            TransportUtil.sendChannelPacket(count, packetInfo, iter.next(), locRCClient);
                        }
                    }
                } else if (chType == ChannelType.MULTICAST) {
                    counter++;
                    logger.debug("Counter: " + counter);
                    fid = getORedFid();
                    TransportUtil.sendChannelPacket(count, packetInfo, fid, locRCClient);
                } else {
                    counter++;
                    logger.debug("Counter: " + counter);
                    fid = getFinalFid();
                    TransportUtil.sendChannelPacket(count, packetInfo, fid, locRCClient);
                }

                count++;
                if (dataProducer.producedLastChunk()) {
                    try {
                        Thread.sleep(Math.abs(1000 - (System.currentTimeMillis() - time)));
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }

        public void close() {
            if (!isClosed) {
                isClosed = true;
                shutDown();
                interrupt();
            }
        }
    }
}
