package pubsub.transport;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.bloomfilter.BloomFilter;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public abstract class Source extends StoppableThread {

    protected static final Logger loggerSource = Logger.getLogger(Source.class);
    private final Map<ForwardIdentifier, Set<ByteIdentifier>> fidMap;
    private static String LOOPBACK_ADDR = "localhost";
    private AtomicInteger totalSubs = new AtomicInteger(0);

    public static void configureLoopbackAddr(String str) {
        loggerSource.debug("loopback set to " + str);
        LOOPBACK_ADDR = str;
    }
    private static int LOCAL_PORT = 10000;

    public static void configureLoopbackPort(int val) {
        loggerSource.debug("local port set to " + val);
        LOCAL_PORT = val;
    }
    protected LocRCClient locRCClient;
    private AtomicBoolean updatedFidList = new AtomicBoolean(false);
    private ForwardIdentifier finalFid;

    public Source() {
        loggerSource.debug("local rc located at " + LOOPBACK_ADDR + "/" + LOCAL_PORT);
        locRCClient = LocRCClientFactory.createNewClient(LOOPBACK_ADDR, LOCAL_PORT, "Source");
        fidMap = Collections.synchronizedMap(new HashMap<ForwardIdentifier, Set<ByteIdentifier>>());
        finalFid = new ForwardIdentifier(BloomFilter.createZero(), (short) 0);
    }

    @Override
    public void run() {
        Publication pub;

        while (!isShutDown()) {
            try {
                pub = locRCClient.receiveNext();

                processPublication(pub);
            } catch (InterruptedException ex) {
                if (!isShutDown()) {
                    loggerSource.error(ex.getMessage(), ex);
                }
            }
        }
        try {
            locRCClient.close();
        } catch (IOException ex) {
            loggerSource.error(ex.getMessage(), ex);
        }
    }

    protected boolean isEmpty() {
        return fidMap.isEmpty();
    }
    
    protected Map<ForwardIdentifier, Set<ByteIdentifier>> getMap() {
        return fidMap;
    }

    protected Set<ForwardIdentifier> getFids() {
        return fidMap.keySet();
    }

    protected int addFid(ForwardIdentifier fid, ByteIdentifier procID) {
        //loggerSource.debug("Added FID");
        Set<ByteIdentifier> set = getSet(fid);

        updatedFidList.getAndSet(true);
        if (set.add(procID)) {
            return totalSubs.incrementAndGet();
        } else {
            return totalSubs.get();
        }
    }

    protected abstract void processPublication(Publication pub);

    protected Subscription createNotification(PubSubID sid, PubSubID rid) {

        MessageDigest md = null;
        Subscription notification;

        try {
            md = MessageDigest.getInstance("SHA-256");

            md.update(sid.getId());
            PubSubID hSid = new PubSubID(md.digest());

            md.update(rid.getId());
            PubSubID hRid = new PubSubID(md.digest());
            notification = Subscription.createSubToMutableData(hSid, hRid);

            return notification;
        } catch (NoSuchAlgorithmException ex) {
        }

        return null;
    }

    protected int removeFid(ForwardIdentifier fid, ByteIdentifier procID) {
        Set<ByteIdentifier> set = getSet(fid);

        if (set.isEmpty()) {
            return totalSubs.get();
        }
        
        int value = totalSubs.get();
        if (set.remove(procID)) {
            value = totalSubs.decrementAndGet();
        }

        if (set.isEmpty()) {
            fidMap.remove(fid);
        }
        
        updatedFidList.getAndSet(true);
        return value;
    }

    protected ForwardIdentifier getORedFid() {
        if (updatedFidList.get()) {
            BloomFilter lid = BloomFilter.createZero();
            short maxTTL = 0;
            updatedFidList.getAndSet(false);

            Set<ForwardIdentifier> keySet = fidMap.keySet();
            synchronized (fidMap) {
                Iterator<ForwardIdentifier> iter = keySet.iterator();
                ForwardIdentifier fid;
                while (iter.hasNext()) {
                    fid = iter.next();
                    lid.or(fid.getBloomFilter());
                    maxTTL = maxTTL < fid.getTTL() ? fid.getTTL() : maxTTL;
                }
            }
            //loggerSource.debug("LID: " + lid + " TTL: " + maxTTL);
            finalFid.setBloomFilter(lid);
            finalFid.setTTL(maxTTL);
        }

        return finalFid;
    }

    protected void setFinalFid(ForwardIdentifier fid) {
        this.finalFid = fid;
    }

    protected ForwardIdentifier getFinalFid() {
        return this.finalFid;
    }

    private Set<ByteIdentifier> getSet(ForwardIdentifier fid) {
        Set<ByteIdentifier> set = fidMap.get(fid);

        if (set == null) {
            set = Collections.synchronizedSet(new HashSet<ByteIdentifier>());
            fidMap.put(fid, set);
        }

        return set;
    }
}
