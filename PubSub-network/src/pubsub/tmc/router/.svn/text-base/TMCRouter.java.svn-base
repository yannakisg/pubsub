package pubsub.tmc.router;

import pubsub.Publication;
import pubsub.tmc.Atomic;
import pubsub.tmc.TMCComponent;
import pubsub.tmc.TMCUtil;
import pubsub.tmc.graph.MyRouterNode;

/**
 *
 * @author John Gasparis
 */
public class TMCRouter extends TMCComponent {
    private MyRouterNode myNode;    

    public TMCRouter() {
        this.setName(this.getClass().getSimpleName()); 
    }

    @Override
    public void run() {
        myNode = new MyRouterNode(TMCUtil.getNodeID());
        subscriber = new TMCRouterSubscriber(myNode, locRCClient);

        Publication pub;

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
