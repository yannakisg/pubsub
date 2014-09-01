package pubsub.messages;

import java.nio.ByteBuffer;
import pubsub.BaseSerializableStruct;
import pubsub.ByteIdentifier;
import pubsub.ContentType;
import pubsub.PubSubID;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class RVAAnnouncement extends BaseSerializableStruct {

    private PubSubID sid;
    private PubSubID rid;
    private ContentType cType;
    private long lifeTime;

    //indicate whether is a publication, a subscription or an unsubscription announced
    public enum AnnouncementType {

        PUBLICATION((byte) 0),
        SUBSCRIPTION((byte) 1),
        UNSUBSCRIPTION((byte) 2);
        private byte index;

        private AnnouncementType(byte index) {
            this.index = index;
        }

        private byte getType() {
            return this.index;
        }

        private static AnnouncementType findBy(byte i) {
            AnnouncementType type = null;

            for (AnnouncementType t : values()) {
                if (t.index == i) {
                    type = t;
                    break;
                }
            }

            return type;
        }

        public static byte findBy(AnnouncementType type) {
            byte b = 0;

            for (AnnouncementType t : values()) {
                if (t == type) {
                    return b;
                }
                b++;
            }

            return -1;
        }
    }

    public enum RVPAction {

        DEFAULT((byte) 0),
        CHANNEL_STEINER_TREES((byte) 1),
        CHANNEL_UNICAST((byte) 2),
        CHANNEL_MULTICAST((byte) 3);
        private byte index;

        private RVPAction(byte index) {
            this.index = index;
        }

        private byte getAction() {
            return this.index;
        }

        private static RVPAction findBy(byte i) {
            RVPAction act = null;

            for (RVPAction a : values()) {
                if (a.index == i) {
                    act = a;
                    break;
                }
            }

            return act;
        }

        public static byte findBy(RVPAction action) {
            byte b = 0;

            for (RVPAction a : values()) {
                if (a == action) {
                    return b;
                }
                b++;
            }

            return -1;
        }
    }
    private RVPAction rvpAction;
    private AnnouncementType annType;
    private ByteIdentifier procID;
    private static int LENGTH = -1;

    private RVAAnnouncement() {
        this.sid = rid = null;
        this.lifeTime = -1;
    }

    public RVAAnnouncement(PubSubID sid, PubSubID rid, ContentType cType, AnnouncementType annType, RVPAction rvpAction, long lifeTime, ByteIdentifier procID) {

        this.sid = sid;
        this.rid = rid;
        this.cType = cType;
        this.annType = annType;
        this.rvpAction = rvpAction;
        this.lifeTime = lifeTime;
        this.procID = procID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        RVAAnnouncement other = (RVAAnnouncement) obj;
        return other.getSID().equals(sid) && other.getRID().equals(rid)
                && other.annType == annType && other.rvpAction == rvpAction;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.sid != null ? this.sid.hashCode() : 0);
        hash = 19 * hash + (this.rid != null ? this.rid.hashCode() : 0);
        hash = 19 * hash + (this.cType != null ? this.cType.hashCode() : 0);
        hash = 19 * hash + (this.rvpAction != null ? this.rvpAction.hashCode() : 0);
        hash = 19 * hash + (this.annType != null ? this.annType.hashCode() : 0);
        return hash;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = (3 * Util.SIZEOF_BYTE) + (PubSubID.ID_LENGTH << 1) + Util.SIZEOF_LONG + procID.getSerializedLength();
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.put(annType.getType());
        buff.put(rvpAction.getAction());
        buff.putLong(lifeTime);
        buff.put(cType.byteValue());

        sid.writeTo(buff);
        rid.writeTo(buff);
        procID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        annType = AnnouncementType.findBy(buffer.get());
        rvpAction = RVPAction.findBy(buffer.get());
        lifeTime = buffer.getLong();
        cType = ContentType.getType(buffer.get());

        sid = PubSubID.parseByteBuffer(buffer);
        rid = PubSubID.parseByteBuffer(buffer);
        procID = ByteIdentifier.parseByteBuffer(buffer);
    }
    
    public ByteIdentifier getProcID() {
        return procID;
    }

    public RVPAction getRVPAction() {
        return this.rvpAction;
    }

    public AnnouncementType getAnnouncementType() {
        return this.annType;
    }

    public boolean isPublication() {
        return this.annType == AnnouncementType.PUBLICATION;
    }

    public boolean isSubscription() {
        return this.annType == AnnouncementType.SUBSCRIPTION;
    }

    public boolean isUnSubscription() {
        return this.annType == AnnouncementType.UNSUBSCRIPTION;
    }

    public ContentType getContentType() {
        return cType;
    }

    public PubSubID getSID() {
        return this.sid;
    }

    public PubSubID getRID() {
        return this.rid;
    }

    public long getLifeTime() {
        return this.lifeTime;
    }

    public static RVAAnnouncement parseByteArray(byte[] data) {
        RVAAnnouncement message = new RVAAnnouncement();
        message.fromBytes(data);
        return message;
    }

    public static RVAAnnouncement parseByteBuffer(ByteBuffer buffer) {
        RVAAnnouncement message = new RVAAnnouncement();
        message.readBuffer(buffer);
        return message;
    }
}
