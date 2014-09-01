package pubsub.localrendezvous;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import pubsub.BaseSerializableStruct;
import pubsub.Publication;
import pubsub.Subscription;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class LocRCIPCMessage extends BaseSerializableStruct {

    public static enum MessageType {

        PUBLISH((byte) 0), SUBSCRIBE((byte) 1), UNSUBSCRIBE((byte) 2), UNKNOWN(
        (byte) -1);
        private final byte type;

        private MessageType(byte i) {
            this.type = i;
        }

        public byte getType() {
            return type;
        }

        public static MessageType findByType(byte i) {
            MessageType retval = null;
            for (MessageType t : values()) {
                if (t.getType() == i) {
                    retval = t;
                    break;
                }
            }
            return retval;
        }
    }
    public static final LocRCIPCMessage STOP_TOKEN = new LocRCIPCMessage();
    public static final int HEADER_LENGTH = 5;
    private MessageType type;
    private Publication publication = null;
    private Subscription subscription = null;

    public static LocRCIPCMessage read(InputStream input) throws IOException {
        LocRCIPCMessage ipcM = new LocRCIPCMessage();
        BufferedInputStream buin = new BufferedInputStream(input);
        DataInputStream dain = new DataInputStream(buin);

        byte type = dain.readByte();


        MessageType mType = MessageType.findByType(type);

        switch (mType) {
            case PUBLISH:
                readPublish(ipcM, dain);
                break;
            case SUBSCRIBE:
                readSubscribe(ipcM, dain);
                break;
            case UNSUBSCRIBE:
                readUnsubscribe(ipcM, dain);
                break;
            default:
                ipcM.type = MessageType.UNKNOWN;
                break;
        }
        return ipcM;
    }

    private static void readPublish(LocRCIPCMessage ipcM, DataInputStream dain)
            throws IOException {


        ipcM.type = MessageType.PUBLISH;
        int len = dain.readInt();
        byte[] data = new byte[len];        

        dain.readFully(data);

        Publication p = Publication.parseByteArray(data);
        ipcM.publication = p;
    }

    private static void readSubscribe(LocRCIPCMessage ipcM, DataInputStream dain)
            throws IOException {
        ipcM.type = MessageType.SUBSCRIBE;
        int len = dain.readInt();
        byte[] data = new byte[len];
        dain.readFully(data);

        Subscription s = Subscription.parseByteArray(data);
        ipcM.subscription = s;
    }

    private static void readUnsubscribe(LocRCIPCMessage ipcM, DataInputStream dain)
            throws IOException {
        ipcM.type = MessageType.SUBSCRIBE;
        int len = dain.readInt();
        byte[] data = new byte[len];
        dain.readFully(data);

        Subscription s = Subscription.parseByteArray(data);
        ipcM.subscription = s;
    }

    public static LocRCIPCMessage createPublicationMessage(Publication publication) {
        LocRCIPCMessage m = new LocRCIPCMessage();
        m.publication = publication;
        m.type = MessageType.PUBLISH;
        return m;
    }

    public static LocRCIPCMessage createSubscriptionMessage(Subscription subscription) {
        LocRCIPCMessage m = new LocRCIPCMessage();
        m.subscription = subscription;
        m.type = MessageType.SUBSCRIBE;
        return m;
    }

    public static LocRCIPCMessage createUnsubscribeMessage(Subscription subscription) {
        LocRCIPCMessage m = new LocRCIPCMessage();
        m.subscription = subscription;
        m.type = MessageType.UNSUBSCRIBE;
        return m;
    }

    public Publication getPublication() {
        return publication;
    }

    public boolean isPublication() {
        return type == MessageType.PUBLISH;
    }

    public boolean isSubscription() {
        return type == MessageType.SUBSCRIBE;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public boolean isUnsubscribe() {
        return type == MessageType.UNSUBSCRIBE;
    }

    public MessageType type() {
        return type;
    }

    @Override
    public String toString() {
        String str = "";
        str += "type " + type.toString();
        return str;
    }

    @Override
    public int getSerializedLength() {
        return HEADER_LENGTH + getFieldsLength();
    }

    private int getFieldsLength() {
        int len = 0;
        if (isPublication()) {
            len += this.publication.getSerializedLength();
        } else if (isSubscription() || isUnsubscribe()) {
            len += this.subscription.getSerializedLength();
        }
        return len;
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        byte t = buff.get();
        MessageType lType = MessageType.findByType(t);

        /*read 4 bytes*/
        buff.getInt();

        if (lType == MessageType.PUBLISH) {
            Publication p = Publication.parseByteBuffer(buff);
            this.publication = p;
            this.subscription = null;
        } else if (lType == MessageType.SUBSCRIBE || lType == MessageType.UNSUBSCRIBE) {
            Subscription s = Subscription.parseByteBuffer(buff);
            this.subscription = s;
            this.publication = null;
        }


    }

    @Override
    public void writeTo(ByteBuffer buff) {
        /*header*/
        buff.put(this.type.getType());
        buff.putInt(getFieldsLength());

        /*data*/
        if (isPublication()) {
            publication.writeTo(buff);
        } else if (isSubscription() || isUnsubscribe()) {
            subscription.writeTo(buff);
        }
    }

    public static LocRCIPCMessage parseBuffer(ByteBuffer mesgBuffer,
            MessageType msgType) {
        LocRCIPCMessage mesg = null;
        if (msgType == MessageType.PUBLISH) {
            Publication p = Publication.parseByteBuffer(mesgBuffer);
            mesg = createPublicationMessage(p);
        } else if (msgType == MessageType.SUBSCRIBE) {
            Subscription s = Subscription.parseByteBuffer(mesgBuffer);
            mesg = createSubscriptionMessage(s);
        } else if (msgType == MessageType.UNSUBSCRIBE) {
            Subscription s = Subscription.parseByteBuffer(mesgBuffer);
            mesg = createUnsubscribeMessage(s);
        }
        return mesg;
    }
}
