package pubsub.experiments.document.subscriber;

import java.io.FileNotFoundException;
import java.util.List;
import org.apache.log4j.Logger;
import pubsub.distribution.ExponentialGenerator;
import pubsub.distribution.ZipfGenerator;
import pubsub.experiments.FileRead;

/**
 *
 * @author John Gasparis
 */
public class Requirer {

    private static final Logger logger = Logger.getLogger(Requirer.class);
    private static String fileName = "";
    public static String LOOPBACK_ADDR = "localhost";
    public static int LOOPBACK_PORT = 10000;
    private static long LIFETIME = 120000;
    private static int TOTAL_SUBSCRIBERS = Integer.MAX_VALUE;
    private static double SKEW = 0.7;
    private static double LAMBDA = 2.0;

    public static void main(String args[]) {
        if (args.length == 0) {
            usage();
        }

        readOptions(args);
        validateOptions();

        try {
            FileRead fileRead = new FileRead(fileName);
            fileRead.read();

            List<String> records = fileRead.getRecords();

            createSubscribers(records);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static void createSubscribers(List<String> records) {
        long sleep;
        long diff = 0;
        ZipfGenerator zipfGen = new ZipfGenerator(records.size(), SKEW);
        ExponentialGenerator expGen = new ExponentialGenerator(LAMBDA);
        Subscriber subscriber;
        String name;
        int next;

        for (int i = 0; i < TOTAL_SUBSCRIBERS; i++) {
            next = zipfGen.next();
            
            if (next < 0) {
                next = Math.abs(next);
                if (next > TOTAL_SUBSCRIBERS - 1) {
                    continue;
                }
            }
            
            name = records.get(next);
            subscriber = new Subscriber(logger, name, LIFETIME);

            subscriber.announce();
            
            sleep = (long) (expGen.next() * 1000) - diff;
            try {
                logger.debug("Let's sleep for " + sleep + " ms");
                Thread.sleep(sleep);
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            }

            diff = System.currentTimeMillis();
            diff = System.currentTimeMillis() - diff;
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
                    logger.error("invalid port: " + loopbackPortStr);
                }
            } else if ("-f".equals(option)) {
                fileName = args[++i];
            } else if ("-s".equals(option)) {
                String skewStr = args[++i];
                try {
                    double skew = Double.parseDouble(skewStr);
                    SKEW = skew;
                } catch (Exception e) {
                    logger.error("invalid skew: " + skewStr);
                }
            } else if ("-lmd".equals(option)) {
                String lamdaStr = args[++i];
                try {
                    double lamda = Double.parseDouble(lamdaStr);
                    LAMBDA = lamda;
                } catch (Exception e) {
                    logger.error("invalid lamda: " + lamdaStr);
                }
            } else if ("-tsub".equals(option)) {
                String totalSub = args[++i];
                try {
                    int tSub = Integer.parseInt(totalSub);
                    TOTAL_SUBSCRIBERS = tSub;
                } catch (Exception e) {
                    logger.error("invalid number of subscribers: " + totalSub);
                }
            } else if ("-lt".equals(option)) {
                String strLTime = args[++i];
                try {
                    long lifeTime = Long.parseLong(strLTime);
                    LIFETIME = lifeTime;
                } catch (Exception e) {
                    logger.error("invalid lifetime: " + strLTime);
                }
            }
        }
    }

    private static void validateOptions() {
        if (fileName.equals("")) {
            logger.error("publications not specified");
            usage();
        }
    }

    private static void usage() {
        logger.error("Usage: -f <subscriptions> [-l <loopback_address>] [-p <port>] [-s <skew>] [-lmd <lamda>] [-tsub <totalSubscribers>] [-lt <lifetime (ms)>]");
        System.exit(-1);
    }
}
