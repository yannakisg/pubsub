package pubsub.tmc.host;

import pubsub.Publication;
import pubsub.tmc.Atomic;
import pubsub.tmc.TMCComponent;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.graph.MyHostNode;

/**
 *
 * @author John Gasparis
 */
public class TMCHost extends TMCComponent {

    private MyHostNode myNode;    

    public TMCHost() {
        this.setName(this.getClass().getSimpleName());        
    }

    @Override
    public void run() {
        Publication pub;
        myNode = new MyHostNode(TMCUtil.getNodeID());
        subscriber = new TMCHostSubscriber(myNode, locRCClient);
        subscribeFor();

        Atomic.increase();
        while (!isShutDown()) {
            try {

                pub = locRCClient.receiveNext();

                processPublication(pub);
            } catch (InterruptedException ex) {
                if (!isShutDown()) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
