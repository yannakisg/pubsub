package pubsub.rva;

import java.io.IOException;
import pubsub.ByteIdentifier;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.tmc.TMCInfo;
import pubsub.util.StoppableThread;

/**
 * @author xvas
 * @author John Gasparis
 */
public class RVAComponentBase extends StoppableThread {
    /*
     * The local rendezvous component client for this RVA service
     */

    protected final LocRCClient locRCClient;
    protected final TimeOutLocRCClient timeOutLocRCClient;
    /*
     * The ID of the current node hosting this RVA
     */
    private ByteIdentifier myNodeID = null; // Identifies this host.
    

    /**
     * The default constructor
     */
    public RVAComponentBase() {
        locRCClient = LocRCClientFactory.createNewClient(this.getClass().getSimpleName());
        timeOutLocRCClient = LocRCClientFactory.createTimeOutClient(this.getClass().getSimpleName());
    }

    /**
     * @return the myNodeID
     */
    public ByteIdentifier getMyNodeID() {
        if (myNodeID == null) {
            myNodeID = TMCInfo.getMyNodeID(timeOutLocRCClient);
        }
        return myNodeID;
    }
    
    protected void closeLocRC() throws IOException {
        this.locRCClient.close();
        this.timeOutLocRCClient.close();
    }
}
