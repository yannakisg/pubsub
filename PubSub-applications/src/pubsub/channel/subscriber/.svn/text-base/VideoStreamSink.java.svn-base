 package pubsub.channel.subscriber;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;


import pubsub.PubSubID;

/**
 *
 * @author John Gasparis
 */
public class VideoStreamSink {
/*
    private static PubSubID SID;
    private static PubSubID RID;
    private static int PORT;
    private static long LIFETIME = 60000;
    private static final Logger logger = Logger.getLogger(VideoStreamSink.class);

    public static void main(String[] args) throws SocketException, UnknownHostException {
        if (args.length == 0) {
            usage();
        }
        try {
            readOptions(args);
        } catch (UnsupportedEncodingException ex) {
            logger.error(ex.getMessage(), ex);
        }
        validateOptions();

        StreamSink videoStream = new StreamSink(SID, RID, PORT, LIFETIME);

        videoStream.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
        }
    }

    private static void readOptions(String[] args) throws UnsupportedEncodingException {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if (option.equals("-s")) {
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
        if (PORT < 1 || PORT > 65535) {
            logger.error("Invalid port : " + PORT);
            System.exit(-1);
        } else if (LIFETIME < 0) {
            logger.error("Invalid lifeTime : " + LIFETIME);
            System.exit(-1);
        }
    }

    private static void usage() {
        logger.error("Usage: -s <sid> -r <rid> -p <port> [-l <lifeTime (ms)>]");
        System.exit(-1);
    }*/
}
