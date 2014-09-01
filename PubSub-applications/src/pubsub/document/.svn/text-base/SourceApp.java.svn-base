package pubsub.document;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import pubsub.PubSubID;
import pubsub.transport.FileInfo;
import pubsub.transport.Source;
import pubsub.transport.api.document.DocumentSourceAPI;

/**
 *
 * @author John Gasparis
 */
public class SourceApp extends DocumentSourceAPI {

    private static PubSubID SID = PubSubID.fromHexString("AAAA");
    private static PubSubID RID = PubSubID.fromHexString("AAAB");
    private static String fileName;
    private static int chunkSize = 4000;
    private static String LOOPBACK_ADDR = "localhost";
    private static int LOOPBACK_PORT = 10000;
    private static long LIFETIME = 120000;

    public static void main(String args[]) throws IOException, NoSuchAlgorithmException {
        readOptions(args);
        Source.configureLoopbackAddr(LOOPBACK_ADDR);
        Source.configureLoopbackPort(LOOPBACK_PORT);

        SourceApp source = new SourceApp();

        fileName = "/home/gaspar/Desktop/1.mp3";

        File file = new File(fileName);
        FileInfo fileInfo = new FileInfo(file, chunkSize, SID, RID);
        source.announcePublication(SID, RID, LIFETIME, fileInfo);
        source.startAll();
    }

    private static void readOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-c".equals(option)) {
                try {
                    chunkSize = Integer.parseInt(args[++i]);
                    if (chunkSize < 0) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.out.println("invalid chunksize option " + args[i]);
                    System.exit(1);
                }
            } else if ("-l".equals(option)) {
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
