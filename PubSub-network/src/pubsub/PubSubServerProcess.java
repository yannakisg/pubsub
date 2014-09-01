package pubsub;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import pubsub.localrendezvous.TimeOutLocRCClient;


import pubsub.util.Consumer;
import pubsub.util.StoppableThread;

/**
 *
 * @author tsilo
 */
public class PubSubServerProcess extends StoppableThread {

    private static Logger logger = Logger.getLogger(PubSubServerProcess.class);
    private final TimeOutLocRCClient lClient;
    private final Subscription serverSub;
    private final RequestHandler requestHandler;
    private volatile long period = 1;

    public PubSubServerProcess(TimeOutLocRCClient lClient, Subscription sub, RequestHandler rh) {
        this.lClient = lClient;
        this.serverSub = sub;
        this.requestHandler = rh;
    }

    @Override
    public void run() {
        Consumer<Publication> requestBuffer = this.lClient.subscribeNonBlock(serverSub);
        while (!isShutDown()) {
            try {
                Publication request = requestBuffer.poll(getPeriod(), TimeUnit.SECONDS);
                if (request == null) {
                    continue;
                }

                Publication encapsulated = Publication.parseByteBuffer(request.wrapData());

                byte[] requestData = encapsulated.getDataArray();
                byte[] responseData = requestHandler.handleRequest(requestData);
                encapsulated.setData(responseData);
                this.lClient.publish(encapsulated);
            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.debug("closing gracefully");
        this.lClient.unsubscribe(serverSub);
    }

    public synchronized void setPeriod(long val) {
        if (val < 0) {
            throw new IllegalArgumentException("invalid value for timeout: " + val);
        }
        this.period = val;
    }

    public synchronized long getPeriod() {
        return period;
    }
}
