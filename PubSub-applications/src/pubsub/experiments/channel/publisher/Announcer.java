package pubsub.experiments.channel.publisher;

import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import pubsub.experiments.channel.publisher.XMLChannelPublisherParser.XMLChannelNode;
import pubsub.util.XMLParser.XMLElement;

/**
 *
 * @author John Gasparis
 */
public class Announcer {

    private static final Logger logger = Logger.getLogger(Announcer.class);
    private static String FILENAME = "";
    private static long LIFETIME = Long.MAX_VALUE;
    private static int CHUNK_SIZE = 0;

    public static void main(String args[]) {
        if (args.length == 0) {
            usage();
        }

        readOptions(args);
        validateOptions();

        readAndPublish();
        
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static void readAndPublish() {
        XMLChannelPublisherParser parser = new XMLChannelPublisherParser(FILENAME);
        parser.parseXMLFile("channel");
        
        List<XMLElement> elements = parser.getNodeList();
        XMLChannelPublisherParser.XMLChannelNode chNode;
        
        for (XMLElement element : elements) {
            chNode = (XMLChannelNode) element;
            new Publisher(chNode.getChannelName(), chNode.getChannelType(), chNode.getBitRate(), LIFETIME, CHUNK_SIZE).announce();                  
        }
    }

    private static void readOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-f".equals(option)) {
                FILENAME = args[++i];
            } else if ("-lt".equals(option)) {
                String strLTime = args[++i];
                try {
                    long lifeTime = Long.parseLong(strLTime);
                    LIFETIME = lifeTime;
                } catch (Exception e) {
                    logger.error("invalid lifetime: " + strLTime);
                }
            } else if ("-c".equals(option)) {
                String chSize = args[++i];
                try {
                    int chunkSize = Integer.parseInt(chSize);
                   CHUNK_SIZE = chunkSize;
                } catch (Exception e) {
                    logger.error("invalid chunk Size: " + chSize);
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
        
        if (CHUNK_SIZE <= 0) {
            logger.error("Invalid chunk size");
            System.exit(-1);
        }
    }

    private static void usage() {
        logger.error("Usage: -f <publications> -c <chunkSize> [-lt <lifetime (ms)>]");
        System.exit(-1);
    }
}
