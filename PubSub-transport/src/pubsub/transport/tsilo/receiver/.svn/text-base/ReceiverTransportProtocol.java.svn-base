package pubsub.transport.tsilo.receiver;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import pubsub.ContentType;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.forwarding.FwdUtils;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.messages.net.transport.ControlMessage;
import pubsub.messages.net.transport.RequestChunkMessage;
import pubsub.transport.tsilo.receiver.FlagSet.Flags;
import pubsub.util.Consumer;
import pubsub.util.Pair;
import pubsub.util.ProducerConsumerQueue;

public class ReceiverTransportProtocol {

    private Logger logger = Logger.getLogger(ReceiverTransportProtocol.class);
    /*
     * what I subscribe to
     */
    private final Subscription originalDocName;
    // request messages
    private final PubSubID feedBackRid;
    private final PubSubID feedBackScope;
    private final ForwardIdentifier pubToSub;
    private final ForwardIdentifier subToPub;
    //segments details
    private final int totalChunks;
    private final int firstChunkNum;
    private final int lastChunkNUm;
    //communication with locRC
    private final TimeOutLocRCClient locRCCLient;
    private final Consumer<Publication> incomingDataQueue;
    private final ProducerConsumerQueue<Pair<Publication, Long>> chunkQueue = ProducerConsumerQueue.createNew();
    private final FlagSet flagSet;
    //what happens after a chunk arrives
    private Set<ChunkArrivalHandler> arrivalHandlers = new HashSet<ChunkArrivalHandler>();
    private AtomicInteger pending = new AtomicInteger(0);
    private int lowThres, highThres;
    private int currentWindow;
    private int maxWindow = Integer.MAX_VALUE;
    private boolean started = false;
    private boolean closed = false;
    // timers
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ReceivingThread recThread;
    private ProcessingThread procThread;
    // timeout related
    private long rttEst = 3000;
    private long rttDeviation = 250;
    //statistics
    private volatile long sentRequests = 0;
    private volatile long receivedChunks = 0;
    private volatile long receivedChunksTotal = 0;
    private volatile long timeouts = 0;
    //callbacks
    Set<TransportCallBack> observerSet = new HashSet<TransportCallBack>();
    //other
    private String namePrefix;

    public ReceiverTransportProtocol(Subscription orgnlDocName,
            TimeOutLocRCClient locRCClient, ControlMessage initMesg) {
        this.originalDocName = orgnlDocName;
        this.locRCCLient = locRCClient;

        this.feedBackScope = initMesg.getReverseSID();
        this.feedBackRid = initMesg.getReverseRID();

        this.pubToSub = initMesg.getPubToSub();
        this.subToPub = initMesg.getSubToPub();

        /* compute number of chunks*/
        int chunks = (int) (initMesg.getTotalChunks() / initMesg.getChunkSize());
        if (initMesg.getTotalChunks() % initMesg.getChunkSize() != 0) {
            chunks++;
        }

        this.totalChunks = chunks;

        this.firstChunkNum = 0;
        this.lastChunkNUm = firstChunkNum + totalChunks - 1;

        this.incomingDataQueue = this.locRCCLient.subscribeNonBlock(orgnlDocName);

        this.flagSet = new FlagSet(firstChunkNum, totalChunks);
        this.lowThres = firstChunkNum;
        this.highThres = lowThres;

        this.currentWindow = Math.min(1, maxWindow);

        namePrefix = this.getClass().getName();
    }

    public void start() {
        if (started) {
            return;
        }

        started = true;

        procThread = new ProcessingThread(this.chunkQueue.getConsumer(), this);
        procThread.setName(namePrefix);
        procThread.start();

        recThread = new ReceivingThread(this.incomingDataQueue, this.chunkQueue.getProduder());
        recThread.setNamePrefix(this.namePrefix);
        recThread.start();

        this.lowThres = this.firstChunkNum;
        transmitNextRequests();
    }

    private void transmitNextRequests() {
        while (pending.intValue() < currentWindow && highThres < lastChunkNUm) {
            sendRequest(highThres);
            scheduleTimer(highThres);
            Flags status = this.getStatusForChunk(highThres);
            if (status == Flags.INIT) {
                pending.incrementAndGet();
                this.flagSet.set(highThres, Flags.PENDING);
            } else if (status == Flags.PENDING) {
                this.flagSet.set(highThres, Flags.RE_REQUESTED);
            }
            highThres++;
        }
    }

    void sendRequest(int i) {
        RequestChunkMessage req = new RequestChunkMessage(this.feedBackScope,
                this.feedBackRid, this.subToPub, this.originalDocName.getScopeId(), this.originalDocName.getRendezvousId(),
                this.pubToSub, i);

        req.publish(locRCCLient, ContentType.REQUEST_IMMUTABLE_DATA, req.toBytes());

        /*
        Publication publication = Publication.createPublication(feedBackScope,
        feedBackRid,
        ContentType.REQUEST_IMMUTABLE_DATA,
        req.toBytes());
        FwdUtils.publishToFwd(subToPub, publication, locRCCLient);*/
        this.flagSet.setDepartureTime(i, System.currentTimeMillis());
        this.sentRequests++;
    }

    void scheduleTimer(int i) {
        long now = System.currentTimeMillis();
        long delay = now + computeTimeout();
        scheduledExecutorService.schedule(new RequestTimer(i, this), delay,
                TimeUnit.MILLISECONDS);
    }

    FlagSet.Flags getStatusForChunk(int chunkNum) {
        return this.flagSet.getStatus(chunkNum);
    }

    private synchronized long computeTimeout() {
        return rttEst + 4 * rttDeviation;
    }

    synchronized void timeout() {
        timeouts++;
        setRTTEst(2 * rttEst);
        setWindow(Math.min(1, currentWindow / 2));
    }

    synchronized private void updateRTT(long sampleRTT) {
        if (this.receivedChunks == 1) {//first chunk received
            setRTTEst(sampleRTT);
            setRTTDev(sampleRTT / 8);
        } else {
            long diff = sampleRTT - rttEst;
            setRTTEst(rttEst + diff / 8);
            setRTTDev(rttDeviation + (Math.abs(diff) - rttDeviation) / 8);
        }
    }

    private synchronized void setRTTEst(long val) {
        rttEst = val;
        logger.debug("SET RTT_EST " + rttEst);
    }

    private synchronized void setRTTDev(long val) {
        rttDeviation = val;
        logger.debug("SET RTT_EST_DEV " + rttDeviation);
    }

    private void increamentWindow() {
        if (this.timeouts == 0) {//still in slow start
            setWindow(currentWindow * 2);
        } else {
            setWindow(Math.min(currentWindow + 1, maxWindow));
        }
    }

    private synchronized void setWindow(int val) {
        currentWindow = val;
        logger.debug("SET WINDOW " + currentWindow);
    }

    public void chunkArrived(Publication publication, long arrivalTime) {
        this.receivedChunksTotal++;
        ByteBuffer buffer = publication.wrapData();
        int chunkNum = buffer.getInt();
        Flags status = this.flagSet.getStatus(chunkNum);
        if (status == Flags.ARRIVED) {// duplicate
            return;
        }

        if (status == Flags.PENDING) {
            this.receivedChunks++;
            long observedRTT = arrivalTime
                    - this.flagSet.getDepartureTime(chunkNum);
            updateRTT(observedRTT);
            increamentWindow();
            pending.decrementAndGet();
        }

        if (status == Flags.RE_REQUESTED) {
            this.receivedChunks++;
            pending.decrementAndGet();
        }

        this.flagSet.set(chunkNum, Flags.ARRIVED);
        this.lowThres = this.flagSet.advanceLowThres();



        if (receivedChunks == totalChunks) {//we re done
            this.close();
            notifyObservers();
        }
    }

    public void setNamePrefix(String name) {
        this.namePrefix = name + "/" + this.namePrefix;
    }

    public boolean registerCallBack(TransportCallBack observer) {
        return this.observerSet.add(observer);
    }

    private void notifyObservers() {
        for (TransportCallBack observer : this.observerSet) {
            observer.notifyTransferCompleted();
        }
    }

    public boolean registerChunkArrivalHandler(ChunkArrivalHandler handler) {
        return this.arrivalHandlers.add(handler);
    }

    public void close() {
        if (!closed) {
            closed = true;
            this.locRCCLient.unsubscribe(originalDocName);

            this.recThread.shutDown();
            this.recThread.interrupt();

            this.procThread.shutDown();
            this.procThread.interrupt();
        }
    }

    public void setLoggerAppender(Appender appender) {
        this.logger.removeAllAppenders();
        this.logger.addAppender(appender);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
