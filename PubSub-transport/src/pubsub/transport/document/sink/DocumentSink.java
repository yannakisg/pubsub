package pubsub.transport.document.sink;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ByteIdentifier;
import pubsub.rva.RVS;
import pubsub.transport.api.document.DocumentSinkListener;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.messages.Message;
import pubsub.messages.MessageType;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.net.transport.ControlACKMessage;
import pubsub.messages.net.transport.ControlMessage;
import pubsub.messages.net.transport.DataMessage;
import pubsub.transport.Sink;
import pubsub.transport.document.protocols.SelectiveRepeatSink;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class DocumentSink extends Sink {

    private final Logger logger = Logger.getLogger(DocumentSink.class);
    private SelectiveRepeatSink selectiveRepeatSink;
    private DocumentSinkListener documentSinkListener;
    private Subscription subscription;
    private File file;
    private Timer timer;
    private String name;
    private boolean isClosed = false;
    private long time;
    private static int count = 0;
    private static final long DELAY = 10000;
    private static final long PERIOD = 15000;
    private FileAppender appender;
    private long lifeTime;
    private ByteIdentifier procID;

    public DocumentSink(File file) {
        super();
        this.file = file;
        this.timer = new Timer("DocumentSink");

        this.name = "DocumentSink" + count;
        ByteBuffer buffer = ByteBuffer.allocate(Util.SIZEOF_INT + Util.SIZEOF_INT);
        buffer.putInt(Util.getPID());
        buffer.putInt(Util.getRandomInteger());
        
        this.procID = new ByteIdentifier(Util.sha256toBytes(buffer.array()));
        setName(name);
        try {
            appender = new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "sink" + count + ".log", false);
            count++;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void announceSubscription(Subscription sub, long lifeTime) {
        logger.debug("Announce Subscription => " + sub.toString());
        this.subscription = sub;

        this.lifeTime = lifeTime;
        RVS.subscribe(sub, RVAAnnouncement.RVPAction.DEFAULT, lifeTime, procID, locRCClient);
        locRCClient.subscribe(sub);

        timer.scheduleAtFixedRate(new AnnounceSubscriptionTask(), DELAY, PERIOD);
    }

    public void setListener(DocumentSinkListener listener) {
        this.documentSinkListener = listener;
    }

    @Override
    protected void processPublication(Publication pub) {
        MessageType.Type msgType = Message.getMessageType(pub.getByteAt(0));

        logger.debug("Received " + msgType);

        if (msgType == MessageType.Type.CONTROL_MESSAGE) {
            processControlMessage(pub.wrapData());
        } else {
            processDataMessage(pub.wrapData());
        }
    }

    private void processControlMessage(ByteBuffer buffer) {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;

        time = System.currentTimeMillis();

        ControlMessage message = ControlMessage.parseByteBuffer(buffer);
        sendControlAckMessage(message);

        try {
            selectiveRepeatSink = new SelectiveRepeatSink(name, appender.getFile(), file.getName(), subscription.getScopeId(),
                    subscription.getRendezvousId(), message, locRCClient, file, documentSinkListener);

            selectiveRepeatSink.requestChunkMessages(0);

            locRCClient.unsubscribe(subscription);

            this.subscription = Subscription.createSubToImmutableData(subscription.getScopeId(), subscription.getRendezvousId());
            locRCClient.subscribe(subscription);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void processDataMessage(ByteBuffer buffer) {
        DataMessage message = DataMessage.parseByteBuffer(buffer);
        if (selectiveRepeatSink.deliverDataChunk(message)) {
            logger.debug("File downloaded...");
            logger.debug("Total Time : " + (System.currentTimeMillis() - time) + " ms");
        }
    }

    private void sendControlAckMessage(ControlMessage ctrlMsg) {
        ControlACKMessage msg = new ControlACKMessage(ctrlMsg.getReverseSID(), ctrlMsg.getReverseRID(),
                ctrlMsg.getID(), ctrlMsg.getSubToPub());
        msg.publishMutableData(locRCClient, msg.toBytes());
    }

    private class AnnounceSubscriptionTask extends TimerTask {

        @Override
        public void run() {
            RVS.subscribe(subscription, RVAAnnouncement.RVPAction.DEFAULT, lifeTime, procID, locRCClient);
        }
    }

    public void close() throws IOException {
        if (!isClosed) {
            isClosed = true;

            if (timer != null) {
                timer.cancel();
            }

            if (selectiveRepeatSink != null) {
                selectiveRepeatSink.close();
            }

            this.shutDown();
            this.interrupt();
        }
    }
}
