package pubsub.messages.net.transport;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.messages.MessageType;
import pubsub.messages.net.NetMessage;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class RequestChunkMessage extends NetMessage {

    private long timestamp;
    private PubSubID appSID;
    private PubSubID appRID;
    private ForwardIdentifier pubToSub;
    private int chunkNum;
    private static int LENGTH = -1;

    public RequestChunkMessage(PubSubID sid, PubSubID rid, ForwardIdentifier fid,
            PubSubID appSID, PubSubID appRID, ForwardIdentifier pubToSub, int chunkNum) {
        super(MessageType.Type.REQUEST_MESSAGE, sid, rid, fid);

        this.appSID = appSID;
        this.appRID = appRID;
        this.pubToSub = pubToSub;
        this.chunkNum = chunkNum;
    }

    private RequestChunkMessage() {
        this(null, null, null, null, null, null, -1);
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + (appRID.getSerializedLength() << 1)
                    + pubToSub.getSerializedLength() + Util.SIZEOF_INT + Util.SIZEOF_LONG;
        }

        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        buff.putInt(chunkNum);
        buff.putLong(System.currentTimeMillis());
        appSID.writeTo(buff);
        appRID.writeTo(buff);
        pubToSub.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        chunkNum = buff.getInt();
        timestamp = buff.getLong();
        appSID = PubSubID.parseByteBuffer(buff);
        appRID = PubSubID.parseByteBuffer(buff);
        pubToSub = ForwardIdentifier.parseByteBuffer(buff);
    }

    public void setChunkNum(int chunkNum) {
        this.chunkNum = chunkNum;
    }

    public int getChunkNum() {
        return this.chunkNum;
    }

    public PubSubID getAppSID() {
        return appSID;
    }

    public PubSubID getAppRID() {
        return appRID;
    }

    public long getTimeStamp() {
        return this.timestamp;
    }

    public ForwardIdentifier getPubToSub() {
        return pubToSub;
    }

    public static int getChunkNum(ByteBuffer buffer) {
        return buffer.getInt(buffer.position() + Util.SIZEOF_BYTE + Util.SIZEOF_INT);
    }

    public static long getTimeStamp(ByteBuffer buffer) {
        return buffer.getLong(buffer.position() + Util.SIZEOF_BYTE + (Util.SIZEOF_INT << 1));
    }

    public static RequestChunkMessage parseByteArray(byte[] data) {
        RequestChunkMessage msg = new RequestChunkMessage();
        msg.fromBytes(data);
        return msg;
    }

    public static RequestChunkMessage parseByteBuffer(ByteBuffer buffer) {
        RequestChunkMessage msg = new RequestChunkMessage();
        msg.readBuffer(buffer);
        return msg;
    }
}
