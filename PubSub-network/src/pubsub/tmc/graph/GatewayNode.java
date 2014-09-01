package pubsub.tmc.graph;

import java.nio.ByteBuffer;

import pubsub.BaseSerializableStruct;
import pubsub.ByteIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.TMC_Mode;
import pubsub.util.FwdConfiguration;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class GatewayNode extends BaseSerializableStruct implements Node {

    private BloomFilter vlidORlid = null;
    private BloomFilter lid;
    private ByteIdentifier id;
    private BloomFilter vlid;
    private TMC_Mode type = TMC_Mode.ROUTER;
    private final int GATEWAY_LENGTH = Util.SIZEOF_SHORT + FwdConfiguration.ZFILTER_LENGTH
            + TMCUtil.SHA1_LENGTH + FwdConfiguration.ZFILTER_LENGTH;

    public GatewayNode(BloomFilter vlid, ByteIdentifier routerID) {
        this.id = routerID;
        this.vlid = vlid;
    }

    private GatewayNode() {
        this(null, null);
    }

    public static GatewayNode createNew() {
        GatewayNode gw = new GatewayNode();
        return gw;
    }

    public static GatewayNode parseByteBuffer(ByteBuffer buffer) {
        GatewayNode gw = new GatewayNode();
        gw.readBuffer(buffer);
        return gw;
    }

    public static GatewayNode parseByteArray(byte[] data) {
        GatewayNode gw = new GatewayNode();
        gw.fromBytes(data);
        return gw;
    }

    public void setVLID(BloomFilter vlid) {
        this.vlid = vlid;
        clearVlidORLid();
    }

    @Override
    public int getSerializedLength() {
        return GATEWAY_LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        lid.writeTo(buff);

        id.writeTo(buff);

        vlid.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        lid = BloomFilter.parseByteBuffer(buff);

        id = ByteIdentifier.parseByteBuffer(buff);

        vlid = BloomFilter.parseByteBuffer(buff);
    }

    @Override
    public BloomFilter getVLID() {
        return this.vlid;
    }

    public BloomFilter getLID() {
        return this.lid;
    }

    public void setLID(BloomFilter lid) {
        this.lid = lid;
        clearVlidORLid();
    }

    @Override
    public ByteIdentifier getID() {
        return this.id;
    }

    @Override
    public String toString() {
        return TMCUtil.byteArrayToString(id.toBytes());
    }

    public boolean isAlive() {
        return true;
    }

    @Override
    public TMC_Mode getType() {
        return type;
    }

    @Override
    public void setID(ByteIdentifier id) {
        this.id = id;
    }

    @Override
    public BloomFilter getLidORVlid(BloomFilter lid) {
        if (vlidORlid == null) {
            vlidORlid = BloomFilter.OR(lid, vlid);
        }

        return vlidORlid;
    }

    @Override
    public void clearVlidORLid() {
        vlidORlid = null;
    }

    @Override
    public void setType(TMC_Mode mode) {
        this.type = TMC_Mode.ROUTER;
    }
}
