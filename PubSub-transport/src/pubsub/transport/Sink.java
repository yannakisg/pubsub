package pubsub.transport;


import java.io.IOException;
import pubsub.ForwardIdentifier;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.TimeOutLocRCClient;
import pubsub.rva.RVAUtil;
import pubsub.tmc.TMCInfo;
import pubsub.tmc.graph.GatewayNode;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public abstract class Sink extends StoppableThread {
    
    public static String LOOPBACK_ADDR = "localhost";
    public static int LOCAL_PORT = 10000;
    
    protected LocRCClient locRCClient;
    protected final ForwardIdentifier gwFID;
    protected static final PubSubID RVA_SID = RVAUtil.RVA_SID;
    protected static final PubSubID RVA_RID = RVAUtil.RVA_RID;


    public Sink() {
        locRCClient = LocRCClientFactory.createNewClient("DocumentSource");

        TimeOutLocRCClient timeoutLocRC = LocRCClientFactory.createTimeOutClient("DocumentSourceTimeout");

        GatewayNode gw = TMCInfo.getDefaultGateway(timeoutLocRC);

        if (gw == null) {
            gwFID = null;
        } else {
            gwFID = new ForwardIdentifier(gw.getLidORVlid(gw.getLID()), (short) 1);
        }

        timeoutLocRC.close();        
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
                    ex.printStackTrace(System.err);
                }
            }
        }
        try {
            locRCClient.close();
        } catch (IOException ex) {
        }
    }

    protected abstract void processPublication(Publication pub);
}