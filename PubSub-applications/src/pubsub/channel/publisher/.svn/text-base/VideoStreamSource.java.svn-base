package pubsub.channel.publisher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import org.apache.log4j.Logger;
import pubsub.PubSubID;

/**
 *
 * @author John Gasparis
 */
public class VideoStreamSource {

   /* private static final Logger logger = Logger.getLogger(VideoStreamSource.class);
    private static String FILENAME = "";
    private static long LIFETIME = 60000;
    private static PubSubID SID;
    private static PubSubID RID;
    private static int PORT;

    public static void main(String args[]) {
        if (args.length == 0) {
            usage();
        }
        try {
            readOptions(args);
        } catch (UnsupportedEncodingException ex) {
            logger.error(ex.getMessage(), ex);
        }
        validateOptions();


        StreamSource videoStream = new StreamSource(SID, RID, PORT, FILENAME, LIFETIME);

        videoStream.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
        }
    }

    private static void readOptions(String[] args) throws UnsupportedEncodingException {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if (option.equals("-f")) {
                FILENAME = args[++i];
            } else if (option.equals("-s")) {
                SID = PubSubID.fromHexString(String.format("%x", new BigInteger(args[++i].getBytes("UTF-8"))));
            } else if (option.equals("-r")) {
                RID = PubSubID.fromHexString(String.format("%x", new BigInteger(args[++i].getBytes("UTF-8"))));
            } else if (option.equals("-p")) {
                String portStr = args[++i];
                try {
                    int port = Integer.parseInt(portStr);
                    PORT = port;
                } catch (Exception e) {
                    logger.error("invalid port: " + portStr);
                }
            } else if (option.equals("-l")) {
                String lTimeStr = args[++i];
                try {
                    long lifeTime = Long.parseLong(lTimeStr);
                    LIFETIME = lifeTime;
                } catch (Exception e) {
                    logger.error("invalid lifeTime: " + lTimeStr);
                }
            }
        }
    }

    private static void validateOptions() {
        if (!new File(FILENAME).exists()) {
            logger.error(FILENAME + " does not exist");
            System.exit(-1);
        } else if (PORT < 1 || PORT > 65535) {
            logger.error("Invalid port : " + PORT);
            System.exit(-1);
        } else if (LIFETIME < 0) {
            logger.error("Invalid lifeTime : " + LIFETIME);
            System.exit(-1);
        }
    }

    private static void usage() {
        logger.error("Usage: -f <videoFile> -s <sid> -r <rid> -p <port> [-l <lifeTime (ms)>]");
        System.exit(-1);
    }*/
}
