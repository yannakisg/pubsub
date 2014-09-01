package pubsub.tmc;

import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.Publication;
import pubsub.bloomfilter.BloomFilter;
import pubsub.util.SortedList;

/**
 *
 * @author John Gasparis
 */
public abstract class TMCSubscriber {

    protected static Logger logger = Logger.getLogger(TMCSubscriber.class);
    protected HelloMessagesCache hellomsgCache = new HelloMessagesCache();
    
    public TMCSubscriber() {
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "tmcSub.log", false));
        } catch (IOException ex) {
        }
    }
    
    protected abstract void processEstablishedLink(Publication pub);
    
    protected abstract void processLinkDown(Publication pub);

    protected abstract void processTMCPublication(Publication pub) throws IOException;

    protected class HelloMessagesCache {

        private SortedList<BloomFilter> list;

        public HelloMessagesCache() {
            list = new SortedList<BloomFilter>(new Comparator<BloomFilter>() {

                @Override
                public int compare(BloomFilter o1, BloomFilter o2) {
                    byte[] bytesO1 = o1.getBytes();
                    byte[] bytesO2 = o2.getBytes();

                    for (int i = 0; i < bytesO1.length; i++) {
                        if (bytesO1[i] > bytesO2[i]) {
                            return 1;
                        } else if (bytesO1[i] < bytesO2[i]) {
                            return -1;
                        }
                    }
                    return 0;
                }
            });
        }

        public void add(BloomFilter vlid) {
            list.add(vlid);
        }

        public boolean contains(BloomFilter vlid) {
            return list.containsElement(vlid);
        }
    }
}
