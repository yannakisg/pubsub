package pubsub.transport.document.source;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import pubsub.ACKHandler;
import pubsub.ByteIdentifier;
import pubsub.ContentType;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.net.rva.InstructDocumentTransfer;
import pubsub.messages.net.transport.ControlACKMessage;
import pubsub.messages.net.transport.ControlMessage;
import pubsub.messages.net.transport.DataMessage;
import pubsub.transport.Source;
import pubsub.transport.FileInfo;
import pubsub.messages.net.transport.RequestChunkMessage;
import pubsub.rva.RVS;
import pubsub.transport.TransportUtil;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class DocumentSource extends Source {

    private FileInfo fileInfo;
    private boolean isClosed = false;
    private static int count = 0;
    private ACKHandler handler;
    private ByteIdentifier procID;
    
    public DocumentSource() {
        super();
        this.setName(DocumentSource.class.getSimpleName() + count);
        count++;
        this.handler = new ACKHandler();
        this.handler.setDaemon(false);
        ByteBuffer buffer = ByteBuffer.allocate(Util.SIZEOF_INT + Util.SIZEOF_INT);
        buffer.putInt(Util.getPID());
        buffer.putInt(Util.getRandomInteger());
        
        this.procID = new ByteIdentifier(Util.sha256toBytes(buffer.array()));
    }

    public void announcePublication(PubSubID sid, PubSubID rid, long lifeTime, FileInfo info) {
        loggerSource.debug("announcePublication");
        this.fileInfo = info;

        Subscription notification = createNotification(sid, rid);
        RVS.publish(sid, rid, ContentType.DOCUMENT, notification, RVAAnnouncement.RVPAction.DEFAULT, lifeTime, procID, locRCClient);
        locRCClient.subscribe(notification);
    }

    @Override
    protected void processPublication(Publication pub) {
        MessageType.Type msgType = Message.getMessageType(pub.getByteAt(0));

        loggerSource.debug("Received " + msgType + " Message");

        if (msgType == MessageType.Type.INSTRUCT_DOCUMENT_MESSAGE) {
            processRVPInstructMessage(pub.wrapData());
        } else if (msgType == MessageType.Type.REQUEST_MESSAGE) {
            processRequestMessage(pub.wrapData());
        } else if (msgType == MessageType.Type.CTRL_ACK_MESSAGE) {
            processCtrlAckMessage(pub.wrapData());
        }
    }

    private void processRVPInstructMessage(ByteBuffer buffer) {
        InstructDocumentTransfer rvpInstruct = InstructDocumentTransfer.parseByteBuffer(buffer);

        Subscription itemName = rvpInstruct.getItemName();
        ControlMessage ctrlMessage;


        addFid(rvpInstruct.getPubtoSub(), rvpInstruct.getProcID());

        ctrlMessage = createControlMessage(itemName, rvpInstruct.getPubtoSub(), rvpInstruct.getSubtoPub());
        deliverControlMessage(ctrlMessage);

        locRCClient.subscribe(Subscription.createRequestToImmutableData(ctrlMessage.getReverseSID(), ctrlMessage.getReverseRID()));
    }

    private void processRequestMessage(ByteBuffer buffer) {
        try {
            int chunkNum = RequestChunkMessage.getChunkNum(buffer);
            long timestamp = RequestChunkMessage.getTimeStamp(buffer);
            loggerSource.debug("REQUEST MESSAGE => " + chunkNum);
            deliverDataChunk(chunkNum, timestamp);
        } catch (IOException ex) {
            loggerSource.error(ex.getMessage(), ex);
        }
    }

    private void processCtrlAckMessage(ByteBuffer buffer) {
        ControlACKMessage message = ControlACKMessage.parseByteBuffer(buffer);
        handler.removeEntry(message.getAckID());
    }

    private ControlMessage createControlMessage(Subscription itemName, ForwardIdentifier pubToSub, ForwardIdentifier subToPub) {
        MessageDigest md = null;
        ControlMessage ctrlMessage;

        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(itemName.getScopeId().getId());
            md.update(md.digest());
            PubSubID hSid = new PubSubID(md.digest());

            md.update(itemName.getRendezvousId().getId());
            md.update(md.digest());
            PubSubID hRid = new PubSubID(md.digest());

            ctrlMessage = new ControlMessage(itemName.getScopeId(), itemName.getRendezvousId(), pubToSub,
                    hSid, hRid, pubToSub, subToPub, fileInfo.getDataChunkSize(), fileInfo.getTotalChunks());

            return ctrlMessage;
        } catch (NoSuchAlgorithmException ex) {
        }

        return null;
    }

    private void deliverControlMessage(ControlMessage ctrlMessage) {
        TransportUtil.sendControlMessage(ctrlMessage, locRCClient);
        handler.addEntry(ctrlMessage);
    }

    private void deliverDataChunk(int chunkNum, long timestamp) throws IOException {
        DataMessage dataMessage = fileInfo.getNextDataMessage(chunkNum, timestamp);
        byte[] bMessage = dataMessage.toBytes();

        synchronized (getFids()) {
            Iterator<ForwardIdentifier> iter = getFids().iterator();
            ForwardIdentifier fid;
            while (iter.hasNext()) {
                fid = iter.next();
                dataMessage.setFID(fid);
                dataMessage.publishImmutableData(locRCClient, bMessage);
            }
        }
    }

    public void close() throws IOException {
        if (!isClosed) {
            isClosed = true;

            fileInfo.close();

            this.handler.shutDown();
            this.handler.interrupt();

            this.shutDown();
            this.interrupt();
        }
    }
}
