package pubsub.forwarding;

import java.util.Timer;
import java.util.TimerTask;
import pubsub.forwarding.communication.CommunicationLink;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.util.Pair;
import pubsub.util.StoppableThread;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class KeepAliveThread extends StoppableThread {

    private Logger logger = Logger.getLogger(KeepAliveThread.class);
    private AtomicInteger noResp = new AtomicInteger(0);
    private final CommunicationLink link;
    private final FwdComponent fwdC;
    private boolean connected = false;
    private Timer timer;
    private Pair<byte[], Integer> pOut;

    public KeepAliveThread(CommunicationLink cl, FwdComponent f, double weight) {
        this.link = cl;
        this.fwdC = f;
        KeepAliveMessage k = new KeepAliveMessage(fwdC.getVLID(), weight);
        byte[] data = Publication.createMutableData(FwdComponent.FWD_SID, FwdComponent.FWD_KEEP_ALIVE, k.toBytes()).toBytes();
        pOut = new Pair<byte[], Integer>(data, data.length);

        timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 6000, 5000);
    }

    @Override
    public void run() {        
        while (!isShutDown()) {
            try {
                logger.debug("transmit keep alive");
                link.transmit(pOut);

                Thread.sleep(30000);

               /* if (noResp.get() > 3 && connected) {
                    connected = false;
                    logger.debug("Link is down");
                    fwdC.publishLinkDown(this.link);
                } else {*/
                    noResp.getAndIncrement();
                //}
            } catch (InterruptedException e) {
                if (isShutDown()) {
                    break;
                } else {
                    logger.error(e.getMessage(), e);
                }
            }

        }
        logger.debug("shutting down");
    }

    public void inform(KeepAliveMessage mesg) {
        if (!connected) {
            timer.cancel();
            connected = true;
            fwdC.publishLinkEstablishment(link, mesg.getVLID(), mesg.getWeight());
            link.transmit(pOut);
        }

        noResp.getAndSet(0);
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            link.transmit(pOut);
        }
    }
}
