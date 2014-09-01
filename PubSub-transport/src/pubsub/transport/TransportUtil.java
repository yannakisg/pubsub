package pubsub.transport;

import org.apache.log4j.Logger;
import pubsub.ContentType;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.net.transport.ControlMessage;
import pubsub.messages.net.transport.DataMessage;
import pubsub.messages.net.transport.ChannelPacketMessage;
import pubsub.messages.net.transport.RequestChunkMessage;
import pubsub.transport.api.channel.PacketInfoEntry;

/**
 *
 * @author John Gasparis
 */
public class TransportUtil {

    private static final Logger logger = Logger.getLogger(TransportUtil.class);

    public static void sendRequestChunkMessage(int totalChunks, RequestChunkMessage message, LocRCClient locRCClient) {
        if (message.getChunkNum() >= totalChunks) {
            return;
        }
        message.publish(locRCClient, ContentType.REQUEST_IMMUTABLE_DATA, message.toBytes());
    }

    public static void sendControlMessage(ControlMessage message, LocRCClient locRCClient) {
       // logger.debug("Send Control Chunk");
        message.publish(locRCClient, ContentType.DOCUMENT, message.toBytes());
    }

    public static void sendDataMessage(PubSubID sid, PubSubID rid, byte[] frame, int chunkNum, long timestamp, ForwardIdentifier fid, LocRCClient locRCClient) {
        DataMessage message = new DataMessage(sid, rid, fid, chunkNum, frame, timestamp);

       // logger.debug("Send Data Chunk [" + chunkNum + "]");
      //  logger.debug("FID : " + fid.getBloomFilter().toBinaryString());

        message.publishImmutableData(locRCClient, message.toBytes());
    }

    public static void sendChannelPacket(int seq, PacketInfoEntry entry, ForwardIdentifier fid, LocRCClient locRCClient) {
        ChannelPacketMessage msg = new ChannelPacketMessage(entry.getSid(), entry.getRid(), fid, seq, entry.getPacketInfo());
        msg.publish(locRCClient, ContentType.CHANNEL, msg.toBytes());
    }
}
