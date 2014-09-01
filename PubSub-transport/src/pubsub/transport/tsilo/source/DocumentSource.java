package pubsub.transport.tsilo.source;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.cache.LRUPacketCache;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.rva.RVS;
import pubsub.util.Consumer;

public class DocumentSource {

    private final static Logger logger = Logger.getLogger(DocumentSource.class);
    private static String LOCALHOST_ADDR = "localhost";

    public static void configureLocalhostAddr(String str) {
        LOCALHOST_ADDR = str;
    }
    private static int PORT = 10000;

    public static void configureLocalhostPort(int port) {
        PORT = port;
    }
    private TimeOutLocRCClient locRcClient;
    private boolean closed = false;
    private RandomAccessFile raf;
    private Subscription docSub;
    private Consumer<Publication> requestQueue;
    private Consumer<Publication> notificationQueue;
    private FirstRequestThread frt;
    private RequestThread rqt;
    private int pieceSize;
    private int totalPieces;
    private long length;
    private LRUPacketCache<Integer, Publication> cache = new LRUPacketCache<Integer, Publication>(25000);

    public DocumentSource(Subscription sub, File file, int pieceSize) throws IOException {
        this.locRcClient = LocRCClientFactory.createTimeOutClient(LOCALHOST_ADDR, PORT, this.getClass().getName());
        this.docSub = sub;

        this.pieceSize = pieceSize;
        raf = new RandomAccessFile(file, "r");
        length = raf.length();
        totalPieces = (int) (this.length / pieceSize);
        requestQueue = this.locRcClient.subscribeNonBlock(docSub);
    }

    public void serve(PubSubID feedBackScope, PubSubID feedBackRid) {
        Subscription notification = Subscription.createSubToImmutableData(feedBackScope, feedBackRid);
        notificationQueue = this.locRcClient.subscribeNonBlock(notification);

        logger.debug("announcing publication to RENE");
        // RVS.publish(this.docSub, notification, this.locRcClient);

        frt = new FirstRequestThread(notificationQueue, this.docSub, notification, this.pieceSize, this.totalPieces, this.locRcClient);
        frt.setName(this.getClass().getName());
        frt.start();

        rqt = new RequestThread(requestQueue, this, this.locRcClient);
        rqt.setName(this.getClass().getName());
    }

    public synchronized Publication getChunk(int chunkNum) {
        Publication pub = this.cache.seek(chunkNum);
        if (pub == null) {
            try {
                pub = readFile(chunkNum);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            cache.write(chunkNum, pub);
        }
        return pub;
    }

    private synchronized Publication readFile(int chunkNum) throws IOException {
        long position = chunkNum * this.pieceSize;
        raf.seek(position);
        int frameLen = (int) Math.min(length - position, pieceSize);
        byte[] frame = new byte[frameLen];
        raf.read(frame);

        ByteBuffer buffer = ByteBuffer.allocate(4 + frame.length);
        buffer.putInt(chunkNum);
        buffer.put(frame);
        return Publication.createImmutableData(this.docSub.getScopeId(), this.docSub.getRendezvousId(), buffer.array());
    }

    public void close() throws IOException {
        if (!closed) {
            closed = true;
            raf.close();

            this.frt.shutDown();
            this.frt.interrupt();

            this.rqt.shutDown();
            this.rqt.interrupt();
        }
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
