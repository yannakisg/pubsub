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
public class InstructUnsubscribe extends BaseRVANetMessage {

    private ForwardIdentifier pubToSub;
    private ForwardIdentifier pubToRVP;
    private Subscription itemName;
    private ByteIdentifier procID;
    private static int LENGTH = -1;

    public InstructUnsubscribe(ByteIdentifier procID, Subscription itemName, ForwardIdentifier pubtoRVP, ForwardIdentifier pubToSub, ForwardIdentifier rvpToPub) {
        super(MessageType.Type.INSTRUCT_UNSUBSCRIBE_MESSAGE, rvpToPub);
        
        this.procID = procID;
        this.pubToSub = pubToSub;
        this.pubToRVP = pubtoRVP;
        this.itemName = itemName;
    }

    private InstructUnsubscribe() {
        super(MessageType.Type.NOTHING, null);
        this.pubToRVP = null;
        this.itemName = null;
        this.procID = null;
    }
    
    public ByteIdentifier getProcID() {
        return procID;
    }

    public Subscription getItemName() {
        return itemName;
    }

    public ForwardIdentifier getPubToRVP() {
        return pubToRVP;
    }

    public ForwardIdentifier getPubToSub() {
        return pubToSub;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + itemName.getSerializedLength() + (pubToRVP.getSerializedLength() << 1) + procID.getSerializedLength();
        }

        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        itemName.writeTo(buff);
        pubToRVP.writeTo(buff);
        pubToSub.writeTo(buff);
        procID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        itemName = Subscription.parseByteBuffer(buff);
        pubToRVP = ForwardIdentifier.parseByteBuffer(buff);
        pubToSub = ForwardIdentifier.parseByteBuffer(buff);
        procID = ByteIdentifier.parseByteBuffer(buff);
    }

    public static InstructUnsubscribe parseByteBuffer(ByteBuffer buff) {
        InstructUnsubscribe msg = new InstructUnsubscribe();
        msg.readBuffer(buff);
        return msg;
    }
}
