package pubsub.messages.ipc.rva;

import java.nio.ByteBuffer;

import pubsub.Subscription;
import pubsub.messages.MessageType;
import pubsub.messages.RVAAnnouncement;

/**
 *
 * @author John Gasparis
 */
public class HostAppToRVAIPCMessage extends BaseRVAIPCMessage {

    private RVAAnnouncement message;
    //used for feedback
    private Subscription notificationName;

    public HostAppToRVAIPCMessage(RVAAnnouncement message) {
        this(message, null);
    }

    public HostAppToRVAIPCMessage(RVAAnnouncement message, Subscription notificationName) {
        super(MessageType.Type.RVA_FORWARD_IPC_MESSAGE);
        this.message = message;
        this.notificationName = notificationName;
    }

    private HostAppToRVAIPCMessage() {
        super(MessageType.Type.RVA_FORWARD_IPC_MESSAGE);
        this.message = null;
        this.notificationName = null;
    }

    @Override
    public int getSerializedLength() {
        int length = super.getSerializedLength() + message.getSerializedLength();

        if (message.isPublication()) {
            length += notificationName.getSerializedLength();
        }

        return length;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        message.writeTo(buff);

        if (message.isPublication()) {
            notificationName.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buffer) {
        super.readBuffer(buffer);
        message = RVAAnnouncement.parseByteBuffer(buffer);

        if (message.isPublication()) {
            notificationName = Subscription.parseByteBuffer(buffer);
        }
    }

    public RVAAnnouncement getRVAAnnouncement() {
        return this.message;
    }

    public Subscription getNotificationName() {
        return this.notificationName;
    }

    public static HostAppToRVAIPCMessage parseByteBuffer(ByteBuffer data) {
        HostAppToRVAIPCMessage msg = new HostAppToRVAIPCMessage();
        msg.readBuffer(data);
        return msg;
    }
}
