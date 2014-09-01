package pubsub.messages.net.tmc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.tmc.graph.MyRouterNode;
import pubsub.util.FwdConfiguration;
import pubsub.util.Util;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.Link;

/**
 *
 * @author John Gasparis
 */
public class LSAUpdate extends TMCNetMessage {

    private MyRouterNode node;
    private ByteIdentifier encapsulatedRouterID;
    private byte[] lsaBytes;
    private BloomFilter lid;
    private double cost;
    private int curSeqNum;
    private BloomFilter vlid;
    private static int LENGTH = -1;

    private LSAUpdate(MyRouterNode node) {
        super(MessageType.Type.LSA_UPDATE, null, node.getID());
        this.node = node;
    }

    public LSAUpdate(MyRouterNode node, ByteIdentifier routerID, byte[] lsaBytes, ForwardIdentifier fid) {
        super(MessageType.Type.LSA_UPDATE, fid, node.getID());

        this.lsaBytes = lsaBytes;
        this.node = node;

        Link link = node.getLink(node.getID(), routerID);
        this.lid = link.getLID();
        this.cost = link.getCost();
    }

    public ByteIdentifier getSenderID() {
        return super.getRouterID();
    }

    public void setSenderID(ByteIdentifier id) {
        super.setRouterID(id);
    }

    public ByteIdentifier getEncapsulatedRouterID() {
        return this.encapsulatedRouterID;
    }

    public BloomFilter getVLID() {
        return vlid;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + FwdConfiguration.ZFILTER_LENGTH + Util.SIZEOF_DOUBLE + Util.SIZEOF_INT;
        }
        return  LENGTH + lsaBytes.length;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        lid.writeTo(buff);
        buff.putDouble(cost);
        buff.putInt(lsaBytes.length);
        buff.put(lsaBytes);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        lid = BloomFilter.parseByteBuffer(buff);

        cost = buff.getDouble();

        int size = buff.getInt();
        lsaBytes = new byte[size];
        buff.get(lsaBytes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LSAUpdate other = (LSAUpdate) obj;
        if (!Arrays.equals(this.lsaBytes, other.lsaBytes)) {
            return false;
        }
        if (this.lid != other.lid && (this.lid == null || !this.lid.equals(other.lid))) {
            return false;
        }
        if (Double.doubleToLongBits(this.cost) != Double.doubleToLongBits(other.cost)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Arrays.hashCode(this.lsaBytes);
        hash = 61 * hash + (this.lid != null ? this.lid.hashCode() : 0);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.cost) ^ (Double.doubleToLongBits(this.cost) >>> 32));
        hash = 61 * hash + super.hashCode();
        return hash;
    }

    private void readEncapsulatedLSA() {
        LinkStateAdvertisement lsa = LinkStateAdvertisement.createNew(node, lsaBytes);

        encapsulatedRouterID = lsa.getRouterID();
        curSeqNum = lsa.getCurrentSeqNum();
        vlid = lsa.getVLID();
    }

    public int getSeqNum() {
        return this.curSeqNum;
    }

    public static LSAUpdate parseByteBuffer(MyRouterNode myNode, ByteBuffer buff) {
        LSAUpdate lsu = new LSAUpdate(myNode);
        lsu.readBuffer(buff);
        lsu.readEncapsulatedLSA();
        return lsu;
    }
    
    public static LSAUpdate parseByteArray(MyRouterNode myNode, byte [] data) {
        LSAUpdate lsu = new LSAUpdate(myNode);
        lsu.fromBytes(data);
        lsu.readEncapsulatedLSA();
        return lsu;
    }
}
