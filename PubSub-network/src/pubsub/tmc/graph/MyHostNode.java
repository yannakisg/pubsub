package pubsub.tmc.graph;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import pubsub.BaseSerializableStruct;
import pubsub.ByteIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.FwdUtils;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.TMC_Mode;
import pubsub.util.FwdConfiguration;

/**
 *
 * @author John Gasparis
 */
public class MyHostNode extends BaseSerializableStruct implements Node {

    private  BloomFilter vlidORlid = null;
    private final int HOST_LENGTH = FwdConfiguration.ZFILTER_LENGTH + TMCUtil.SHA1_LENGTH;
    private ByteIdentifier id;
    private BloomFilter vlid;
    private TMC_Mode type = TMC_Mode.HOST;

    public MyHostNode(ByteIdentifier id) {
        this.id = id;
        this.vlid = getVLID();
    }

    public MyHostNode(byte[] data) {
        this(new ByteIdentifier(data));
    }

    @Override
    public int getSerializedLength() {
        return HOST_LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        vlid.writeTo(buff);
        id.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        vlid = BloomFilter.parseByteBuffer(buff);

        id = ByteIdentifier.parseByteBuffer(buff);
    }

    @Override
    public final BloomFilter getVLID() {
        if (vlid == null) {
            TimeOutLocRCClient tClient = LocRCClientFactory.createTimeOutClient(this.getClass().getSimpleName());
            Logger logger = Logger.getLogger(MyHostNode.class);
            try {
                logger.debug("obtain VLID from FWD");
                this.vlid = FwdUtils.getFwdVLID(tClient);
                logger.debug("got VLID: " + this.vlid.toBinaryString());
            } catch (InterruptedException e) {
                throw new RuntimeException("cannot obtain vlid from FwdComp", e);
            } finally {
                tClient.close();
            }
        }
        return this.vlid;
    }
    
    @Override
    public ByteIdentifier getID() {
        return this.id;
    }

    @Override
    public void setID(ByteIdentifier id) {
        this.id = id;
    }

    @Override
    public TMC_Mode getType() {
        return type;
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
        this.type = TMC_Mode.HOST;
    }
}
