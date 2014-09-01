package pubsub.experiments.channel.subscriber;

import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import pubsub.experiments.channel.subscriber.XMLChannelSubscriberParser.XMLChannelNode;
import pubsub.util.XMLParser.XMLElement;

/**
 *
 * @author John Gasparis
 */
public class Requirer {

    private static final Logger logger = Logger.getLogger(Requirer.class);
    private static String FILENAME;
    private static int LOG_COUNT = Integer.MIN_VALUE;

    public static void main(String args[]) {
        if (args.length == 0) {
            usage();
        }

        readOptions(args);
        validateOptions();
        
        readAndSubscribe();
        
        System.exit(0);
    }
    
    private static void readAndSubscribe() {
        XMLChannelSubscriberParser parser = new XMLChannelSubscriberParser(FILENAME);
        parser.parseXMLFile("channel");
        
        List<XMLElement> elements = parser.getNodeList();
        XMLChannelSubscriberParser.XMLChannelNode chNode;
        Subscriber subscriber;
        Subscriber prevSubscriber = null;
        
        for (XMLElement element : elements) {
            chNode = (XMLChannelNode) element;
            subscriber = new Subscriber(chNode.getChannelName(), chNode.getDuration(), LOG_COUNT);
            subscriber.announce();
            
            try {
                Thread.sleep(chNode.getDuration() * 1000);
            } catch (InterruptedException ex) {                
            }
            
            if (prevSubscriber != null) {
                prevSubscriber.close();
            }
            subscriber.unsubscribe();
            prevSubscriber = subscriber;            
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
        prevSubscriber.close();
    }

    private static void readOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-f".equals(option)) {
                FILENAME = args[++i];
            } else if ("-l".equals(option)) {
                String logName = args[++i];
                try {
                    int lName = Integer.parseInt(logName);
                   LOG_COUNT = lName;
                } catch (Exception e) {
                    logger.error("invalid chunk Size: " + logName);
                }
            }            
        }
    }

    private static void validateOptions() {
        File file = new File(FILENAME);
        if (!file.exists()) {
            logger.error("File does not exist");
            System.exit(-1);
        }
        if (LOG_COUNT == Integer.MIN_VALUE) {
            logger.error("Invalid logCount");
            System.exit(-1);
        }
    }

    private static void usage() {
        logger.error("Usage: -f <subscriptions> -l <logCount>");
        System.exit(-1);
    }
}
