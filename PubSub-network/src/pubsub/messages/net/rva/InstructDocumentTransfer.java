package pubsub.messages.net.rva;

import java.nio.ByteBuffer;

import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.Subscription;
import pubsub.messages.MessageType;

/**
 * 
 * @autor John Gasparis
 */
public class InstructDocumentTransfer extends BaseRVANetMessage {

    private ForwardIdentifier subtoPub;
    private ForwardIdentifier pubtoSub;
    private ForwardIdentifier pubtoRVP;
    private Subscription itemName;
    private ByteIdentifier procID;
    private static int LENGTH = -1;

    private InstructDocumentTransfer() {
        super(MessageType.Type.NOTHING, null);
        this.subtoPub = null;
        this.pubtoSub = null;
        this.itemName = null;
    }

    public InstructDocumentTransfer(ByteIdentifier procID, Subscription itemName,
            ForwardIdentifier pubtoSub, ForwardIdentifier subToPub, ForwardIdentifier pubtoRVP,
            ForwardIdentifier rvpToPub) {
        super(MessageType.Type.INSTRUCT_DOCUMENT_MESSAGE, rvpToPub);
        this.procID = procID;
        this.pubtoRVP = pubtoRVP;
        this.itemName = itemName;
        this.subtoPub = subToPub;
        this.pubtoSub = pubtoSub;
    }

    public ForwardIdentifier getSubtoPub() {
        return subtoPub;
    }
    
    public ByteIdentifier getProcID() {
        return procID;
    }

    public void setSubToPub(ForwardIdentifier subToPub) {
        this.subtoPub = subToPub;
    }

    public void setPubToSub(ForwardIdentifier pubToSub) {
        this.pubtoSub = pubToSub;
    }

    public ForwardIdentifier getPubtoSub() {
        return pubtoSub;
    }

    public void setPubtoRVP(ForwardIdentifier pubGWtoRVP) {
        this.pubtoRVP = pubGWtoRVP;
    }

    public ForwardIdentifier getPubtoRVP() {
        return pubtoRVP;
    }

    public Subscription getItemName() {
        return itemName;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + itemName.getSerializedLength() + (pubtoSub.getSerializedLength() * 3) + procID.getSerializedLength();
        }

        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);

        itemName.writeTo(buff);
        pubtoSub.writeTo(buff);
        pubtoRVP.writeTo(buff);
        subtoPub.writeTo(buff);
        procID.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        itemName = Subscription.parseByteBuffer(buff);
        pubtoSub = ForwardIdentifier.parseByteBuffer(buff);
        pubtoRVP = ForwardIdentifier.parseByteBuffer(buff);
        subtoPub = ForwardIdentifier.parseByteBuffer(buff);
        procID = ByteIdentifier.parseByteBuffer(buff);
    }

    public static InstructDocumentTransfer parseByteBuffer(ByteBuffer buff) {
        InstructDocumentTransfer rvpInstruct = new InstructDocumentTransfer();
        rvpInstruct.readBuffer(buff);
        return rvpInstruct;
    }
}
