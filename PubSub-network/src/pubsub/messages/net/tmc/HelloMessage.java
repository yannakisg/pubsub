package pubsub.messages.net.tmc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.bloomfilter.BloomFilter;
import pubsub.tmc.TMC_Mode;
import pubsub.util.FwdConfiguration;
import pubsub.messages.MessageType;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class HelloMessage extends TMCNetMessage {

    private TMC_Mode type;
    private BloomFilter vlid;
    private static int LENGTH = -1;

    public HelloMessage(TMC_Mode type, ByteIdentifier routerID, BloomFilter vlid, ForwardIdentifier fid) {
        super(MessageType.Type.HELLO, fid, routerID);
        this.type = type;
        this.vlid = vlid;
    }

    private HelloMessage() {
        this.type = TMC_Mode.HOST;
        this.vlid = null;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + Util.SIZEOF_BYTE + FwdConfiguration.ZFILTER_LENGTH;
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        buff.put(type.getMode());
        vlid.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        type = TMC_Mode.findBy(buff.get());

        vlid = BloomFilter.parseByteBuffer(buff);
    }

    public BloomFilter getVLID() {
        return this.vlid;
    }

    public TMC_Mode getType() {
        return this.type;
    }

    public static HelloMessage parseByteBuffer(ByteBuffer buff) {
        HelloMessage msg = new HelloMessage();
        msg.readBuffer(buff);
        return msg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HelloMessage other = (HelloMessage) obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.vlid != other.vlid && (this.vlid == null || !this.vlid.equals(other.vlid))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.vlid != null ? this.vlid.hashCode() : 0);
        hash = 67 * hash + +super.hashCode();
        return hash;
    }
}
