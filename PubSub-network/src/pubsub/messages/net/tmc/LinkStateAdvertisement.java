package pubsub.messages.net.tmc;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.messages.MessageType;
import pubsub.util.FwdConfiguration;
import pubsub.tmc.graph.MyRouterNode;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class LinkStateAdvertisement extends TMCNetMessage {

    private MyRouterNode node;
    private BloomFilter vlid;
    private int curSeqNum;
    private static int seqNum = 0;
    private static int LENGTH = -1;

    private LinkStateAdvertisement(MyRouterNode node) {
        this(node, null);
    }

    public LinkStateAdvertisement(MyRouterNode node, ForwardIdentifier fid) {
        super(MessageType.Type.LSA, fid, node.getID());
        this.node = node;
        this.vlid = node.getVLID();
    }

    public static void incrementCounter() {
        seqNum++;
    }

    public int getCurrentSeqNum() {
        return this.curSeqNum;
    }

    public void setVlid(BloomFilter vlid) {
        this.vlid = vlid;
    }

    public BloomFilter getVLID() {
        return this.vlid;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + Util.SIZEOF_INT + FwdConfiguration.ZFILTER_LENGTH;
        }
        return LENGTH + node.getSerializedLength();
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        buff.putInt(seqNum);
        vlid.writeTo(buff);
        node.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        curSeqNum = buff.getInt();

        vlid = BloomFilter.parseByteBuffer(buff);

        node.readBuffer(super.getRouterID(), buff);
    }

    public static LinkStateAdvertisement createNew(MyRouterNode node, byte [] data) {
        LinkStateAdvertisement helloUpdMsg = new LinkStateAdvertisement(node);
        helloUpdMsg.fromBytes(data);
        helloUpdMsg.setMessageType(MessageType.Type.LSA);
        return helloUpdMsg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkStateAdvertisement other = (LinkStateAdvertisement) obj;
        if (this.node != other.node && (this.node == null || !this.node.equals(other.node))) {
            return false;
        }
        if (this.vlid != other.vlid && (this.vlid == null || !this.vlid.equals(other.vlid))) {
            return false;
        }
        if (this.curSeqNum != other.curSeqNum) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.vlid != null ? this.vlid.hashCode() : 0);
        hash = 97 * hash + super.hashCode();
        return hash;
    }
}
