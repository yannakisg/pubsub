package pubsub.tmc;

import java.io.IOException;
import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.forwarding.FwdComponent;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public abstract class TMCComponent extends StoppableThread {

    protected static final Logger logger = Logger.getLogger(TMCComponent.class);
    protected LocRCClient locRCClient = LocRCClientFactory.createNewClient("");
    protected Subscription subLinkStCon = Subscription.createSubToMutableData(TMCUtil.TMC_SID, TMCUtil.TMC_RID);
    protected TMCSubscriber subscriber;

    protected void subscribeFor() {

        Subscription subLinkEst = Subscription.createSubToMutableData(FwdComponent.FWD_SID, FwdComponent.FWD_LINK_ESTABLISHMENT);
        Subscription subLinkDown = Subscription.createSubToMutableData(FwdComponent.FWD_SID, FwdComponent.FWD_LINK_DOWN);

        locRCClient.subscribe(subLinkEst);
        locRCClient.subscribe(subLinkStCon);
        locRCClient.subscribe(subLinkDown);
    }

    protected void processPublication(Publication pub) {
        PubSubID sid, rid;

        sid = pub.getScopeId();
        rid = pub.getRendezvousId();

        if (sid.equals(FwdComponent.FWD_SID)) {
            if (rid.equals(FwdComponent.FWD_LINK_ESTABLISHMENT)) {
           //     logger.debug("Received a FWD_LINK_ESTABLISHMENT publication");
                subscriber.processEstablishedLink(pub);
            } else if (rid.equals(FwdComponent.FWD_LINK_DOWN)) {
              //  logger.debug("Received a FWD_LINK_DOWN publication");
                subscriber.processLinkDown(pub);
            } else {
                logger.debug("Unknown Rid");
            }
        } else if (sid.equals(TMCUtil.TMC_SID) && rid.equals(TMCUtil.TMC_RID)) {
          //  logger.debug("Received a TMC publication");
            try {
                subscriber.processTMCPublication(pub);
            } catch (IOException ex) {
                logger.debug("IOException", ex);
            }
        } else {
            logger.debug("Unknown publication");
        }
    }
}
