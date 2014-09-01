package pubsub.messages.net.rva;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.Subscription;
import pubsub.messages.MessageType;

/**
 *
 * @author John Gasparis
 */
public class InstructChannelTransfer extends BaseRVANetMessage {

    private ForwardIdentifier pubToSub;
    private ForwardIdentifier pubtoRVP;
    private Subscription itemName;
    private ByteIdentifier procID;
    private static int LENGTH = -1;

    private InstructChannelTransfer() {
        this(null, null, null, null, null);
    }

    public InstructChannelTransfer(ByteIdentifier procID, Subscription itemName, ForwardIdentifier pubToSub, ForwardIdentifier pubtoRVP, ForwardIdentifier fid) {
        super(MessageType.Type.INSTRUCT_CHANNEL_MESSAGE, fid);
        this.procID = procID;
        this.pubtoRVP = pubtoRVP;
        this.pubToSub = pubToSub;
        this.itemName = itemName;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + itemName.getSerializedLength() + (pubToSub.getSerializedLength() << 1) + procID.getSerializedLength();
        }

        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        itemName.writeTo(buff);
        pubToSub.writeTo(buff);
        pubtoRVP.writeTo(buff);
        procID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        itemName = Subscription.parseByteBuffer(buff);
        pubToSub = ForwardIdentifier.parseByteBuffer(buff);
        pubtoRVP = ForwardIdentifier.parseByteBuffer(buff);
        procID = ByteIdentifier.parseByteBuffer(buff);
    }
    
    public ByteIdentifier getProcID() {
        return procID;
    }

    public Subscription getItemName() {
        return itemName;
    }

    public ForwardIdentifier getPubToSub() {
        return pubToSub;
    }

    public ForwardIdentifier getPubtoRVP() {
        return pubtoRVP;
    }

    public static InstructChannelTransfer parseByteBuffer(ByteBuffer buffer) {
        InstructChannelTransfer msg = new InstructChannelTransfer();
        msg.readBuffer(buffer);
        return msg;
    }

    public void setPubToSub(ForwardIdentifier pubToSub) {
        this.pubToSub = pubToSub;
    }
}
