package pubsub.transport.api.channel;

import pubsub.PubSubID;
import pubsub.messages.net.transport.ChannelPacketInfo;

/**
 *
 * @author John Gasparis
 */
public class PacketInfoEntry {

    private ChannelPacketInfo packetInfo;
    private PubSubID sid;
    private PubSubID rid;

    public PacketInfoEntry(PubSubID sid, PubSubID rid, ChannelPacketInfo packetInfo) {
        this.sid = sid;
        this.rid = rid;
        this.packetInfo = packetInfo;
    }

    public ChannelPacketInfo getPacketInfo() {
        return packetInfo;
    }

    public PubSubID getRid() {
        return rid;
    }

    public PubSubID getSid() {
        return sid;
    }
}
