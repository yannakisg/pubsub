package pubsub.transport.document.protocols;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClient;
import pubsub.messages.net.transport.ControlMessage;
import pubsub.messages.net.transport.DataMessage;
import pubsub.transport.document.sink.DocumentSinkPending;
import pubsub.transport.TransportUtil;
import pubsub.transport.api.document.DocumentSinkListener;
import pubsub.messages.net.transport.RequestChunkMessage;
import pubsub.util.Pair;
import pubsub.util.StoppableThread;
import pubsub.util.ThreadSafeSortedList;

/**
 *
 * @author John Gasparis
 */
public class SelectiveRepeatSink {

    private final Logger logger;
    private LocRCClient locRCClient;
    private ControlMessage controlMessage;
    private Pair<PubSubID, PubSubID> sidRid;
    private ForwardIdentifier reverseGWFID;
    private DocumentSinkPending pendingTable;
    private long totalTimeouts = 0;
    private long ldr = -1; /* Last data received */

    private long lrs = 0; /* Last request sent */

    private long estimatedRTT = 0;
    private long deviation = 0;
    private AtomicLong timeout = new AtomicLong(1000);
    private int maxWindow = 1;//Integer.MAX_VALUE;
    private int currentWindow = 1;
    private final ThreadSafeSortedList<RequestChunkMessage> requests;
    private Map<Integer, Long> pendingTimeouts;
    private BitSet bitSet;
    private final RequestChunkMessage msg = new RequestChunkMessage(null, null, null, null, null, null, -1);
    private Retransmitter retransmitter = null;
    private String prefixName;
    
    private long duplicates = 0;
    private long timeouts = 0;
    private long start = 0;
    private long outOfOrder = 0;
    
    private static int count = 0;

    public SelectiveRepeatSink(String prefixName, String appenderName, String fileName, PubSubID sid, PubSubID rid, ControlMessage message, LocRCClient locRCClient, File file, DocumentSinkListener listener) throws IOException {        
        logger = Logger.getLogger("SelectiveRepeatSink" + count++);
        appenderName = appenderName.substring(0, appenderName.lastIndexOf(".log"));
        logger.addAppender(new FileAppender(new SimpleLayout(), appenderName + "_selectiveRepeat_" + fileName + ".log", false));
        this.prefixName = prefixName;
        this.controlMessage = message;
        this.reverseGWFID = message.getSubToPub();
        this.locRCClient = locRCClient;
        this.sidRid = new Pair<PubSubID, PubSubID>(sid, rid);

        logger.debug("TotalChunks: " + message.getTotalChunks());

        this.requests = new ThreadSafeSortedList<RequestChunkMessage>(new Comparator<RequestChunkMessage>() {

            @Override
            public int compare(RequestChunkMessage o1, RequestChunkMessage o2) {
                int cNum1 = o1.getChunkNum();
                int cNum2 = o2.getChunkNum();

                if (cNum1 > cNum2) {
                    return 1;
                } else if (cNum2 > cNum1) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        requests.addSole(new RequestChunkMessage(controlMessage.getReverseSID(),
                controlMessage.getReverseRID(), reverseGWFID,
                sidRid.getFirst(), sidRid.getSecond(), controlMessage.getPubToSub(), 0));


        this.pendingTable = new DocumentSinkPending();
        pendingTable.setEntry(file, controlMessage.getChunkSize());
        pendingTable.setListener(listener);

        this.pendingTimeouts = new HashMap<Integer, Long>();
        this.bitSet = new BitSet();
    }

    public void requestChunkMessages(int initPos) {
        RequestChunkMessage message;
        long tm;
        
        if (start == 0) {
            start = System.currentTimeMillis();
        }

        tm = timeout.get();
        //logger.debug("Request " + requests.getSize() + " Messages");
        for (int i = initPos; i < requests.getSize(); i++) {
            message = requests.get(i);
            pendingTimeouts.put(message.getChunkNum(), tm);
            
            logger.debug("Transmit Request: " + message.getChunkNum() + " | " + System.currentTimeMillis());
            TransportUtil.sendRequestChunkMessage((int) controlMessage.getTotalChunks(), message, locRCClient);
        }


        if (retransmitter == null) {
            retransmitter = new Retransmitter();
            retransmitter.start();
        }
    }

    public boolean deliverDataChunk(DataMessage message) {
        logger.debug("Received: " + message.getChunkNum() + " | " + System.currentTimeMillis() + " | " + message.getHopCount());

        if (message.getChunkNum() <= ldr) {
            logger.debug("Duplicate: " + message.getChunkNum());
            duplicates++;
            return false;
        } else if (message.getChunkNum() > lrs) {
            logger.debug("Out of Order: " + message.getChunkNum());
            outOfOrder++;
            return false;
        }

        updateWindow();

        if (!bitSet.get(message.getChunkNum())) {
            updateRTT(message.getTimeStamp());
        }


        ldr = requests.get(0).getChunkNum() - 1;
        lrs = requests.get(requests.getSize() - 1).getChunkNum();

        msg.setChunkNum(message.getChunkNum());
        requests.remove(msg);

        int receivedChunks = pendingTable.addData(message);
        //logger.debug("RECEIVED => " + receivedChunks);

        if (receivedChunks == controlMessage.getTotalChunks()) {
            long totalTime = System.currentTimeMillis() - start;
            logger.debug("Size: " + pendingTable.getTotalSize() + " bytes");
            logger.debug("Total Time: " + totalTime + " ms");
            logger.debug("Timeouts: " + timeouts);
            logger.debug("Duplicates: " + duplicates);
            logger.debug("Out of Order: " + outOfOrder);
            
            pendingTable.documentReceived();
            locRCClient.unsubscribe(Subscription.createSubToImmutableData(sidRid.getFirst(), sidRid.getSecond()));
            return true;
        }

        update();
        return false;
    }

    private void updateWindow() {
        currentWindow = maxWindow;
        /*if (totalTimeouts == 0) {
        currentWindow = (currentWindow << 1);
        } else {
        currentWindow = Math.min(currentWindow + 1, maxWindow);
        }*/
    }

    private void updateRTT(long timestamp) {
        long sampleRTT = System.currentTimeMillis() - timestamp;
        long newTimeout;

        sampleRTT -= (estimatedRTT >> 3);
        estimatedRTT += sampleRTT;

        if (sampleRTT < 0) {
            sampleRTT = -sampleRTT;
        }

        sampleRTT -= (deviation >> 3);
        deviation += sampleRTT;

        newTimeout = Math.max((estimatedRTT >> 3) + (deviation >> 1), 1000);

        //timeout.set(newTimeout);
    }

    private void update() {
        if (requests.getSize() >= currentWindow) {
            return;
        }

        int length = Math.abs(requests.getSize() - currentWindow);
        int chunkNum;

        for (int i = 0; i < length; i++) {
            chunkNum = (int) (lrs + i + 1);
            if (!pendingTable.hasReceived(chunkNum)) {
                requests.addSole(new RequestChunkMessage(controlMessage.getReverseSID(),
                        controlMessage.getReverseRID(), reverseGWFID,
                        sidRid.getFirst(), sidRid.getSecond(), controlMessage.getPubToSub(), chunkNum));
            } else {
                length++;
            }
        }


        if (requests.getSize() - length < 0) {
            return;
        }

        requestChunkMessages(requests.getSize() - length);

        lrs += length;

    }

    public void close() {
        retransmitter.shutDown();
        retransmitter.interrupt();
    }

    private class Retransmitter extends StoppableThread {

        public Retransmitter() {
            setName(prefixName + "/Retransmitter");
        }

        @Override
        public void run() {
            int i;
            Long tempTimeout;
            long tm;
            RequestChunkMessage message;

            while (!isShutDown()) {
                //logger.debug("Retransmitter is running");                

                try {
                    Thread.sleep(timeout.get());
                } catch (InterruptedException ex) {
                }

                tm = timeout.get();
                for (i = 0; i < requests.getSize(); i++) {
                    message = requests.get(i);
                    tempTimeout = pendingTimeouts.get(message.getChunkNum());
                    if (tempTimeout == null) {
                        continue;
                    }

                    tempTimeout -= tm;

                    if (tempTimeout <= 0) {
                        if (totalTimeouts == 0) {
                            totalTimeouts++;
                        }
                        bitSet.set(message.getChunkNum());
                        
                        timeouts++;
                        logger.debug("Retransmit Request: " + message.getChunkNum() + " | " + System.currentTimeMillis());
                        TransportUtil.sendRequestChunkMessage((int) controlMessage.getTotalChunks(), message, locRCClient);
                        pendingTimeouts.put(i, tm);
                    } else {
                        pendingTimeouts.put(i, tempTimeout);
                    }
                }
            }
        }
    }
}
