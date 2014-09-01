package pubsub.tmc.rvp;

import pubsub.Publication;
import pubsub.tmc.Atomic;
import pubsub.tmc.TMCComponent;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.graph.MyRVPNode;

/**
 *
 * @author John Gasparis
 */
public class TMCRVP extends TMCComponent {

    private MyRVPNode myNode;   
    public TMCRVP() {
        this.setName(this.getClass().getSimpleName());        
    }

    @Override
    public void run() {
        Publication pub;

        myNode = new MyRVPNode(TMCUtil.getNodeID());
        subscriber = new TMCRVPSubscriber(myNode, locRCClient);
        subscribeFor();

        Atomic.increase();
        while (!isShutDown()) {
            try {
                pub = locRCClient.receiveNext();

                processPublication(pub);
            } catch (InterruptedException ex) {
                if (!isShutDown()) {
                    logger.debug(ex.getMessage(), ex);
                }
            }
        }
    }
}
