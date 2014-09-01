package pubsub.document;


import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.transport.Sink;
import pubsub.transport.api.document.DocumentEntryEvent;
import pubsub.transport.api.document.DocumentSinkAPI;

/**
 *
 * @author John Gasparis
 */
public class SinkApp extends DocumentSinkAPI {

    private static SinkApp sink;
    private static PubSubID SID = PubSubID.fromHexString("AAAA");
    private static PubSubID RID = PubSubID.fromHexString("AAAB");
    private static final Logger logger = Logger.getLogger(SinkApp.class);
    private static String LOOPBACK_ADDR = "localhost";
    private static int LOOPBACK_PORT = 10000;
    private static long LIFETIME = 120000;
    private static String fileName;

    public SinkApp(File file) {
        super(file);
    }

    @Override
    public void documentReceived(DocumentEntryEvent event) {
        logger.debug("Document was received");
        try {
            sink.stopAll();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        System.exit(0);
    }

    public static void main(String args[]) {
        readOptions(args);
        Sink.LOOPBACK_ADDR = LOOPBACK_ADDR;
        Sink.LOCAL_PORT = LOOPBACK_PORT;

        fileName = "/home/gaspar/Desktop/out.mp3";

        sink = new SinkApp(new File(fileName));

        sink.announceSubscription(SID, RID, LIFETIME);

        sink.setListener(sink);
        sink.startAll();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
        }
    }

    private static void readOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-l".equals(option)) {
                String loopbackAddr = args[++i];
                LOOPBACK_ADDR = loopbackAddr;
            } else if ("-p".equals(option)) {
                String loopbackPortStr = args[++i];
                try {
                    int port = Integer.parseInt(loopbackPortStr);
                    LOOPBACK_PORT = port;
                } catch (Exception e) {
                    System.out.println("invalid port: " + loopbackPortStr);
                }
            } else if ("-f".equals(option)) {
                fileName = args[++i];
            }
        }
    }
}
