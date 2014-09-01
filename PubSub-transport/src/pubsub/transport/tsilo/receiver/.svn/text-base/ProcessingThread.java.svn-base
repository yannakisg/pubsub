package pubsub.transport.tsilo.receiver;

import org.apache.log4j.Logger;

import pubsub.Publication;
import pubsub.util.Consumer;
import pubsub.util.Pair;
import pubsub.util.StoppableThread;

public class ProcessingThread extends StoppableThread {

    private final static Logger logger = Logger.getLogger(ProcessingThread.class);
    private final Consumer<Pair<Publication, Long>> queue;
    private final ReceiverTransportProtocol recvProto;

    public ProcessingThread(Consumer<Pair<Publication, Long>> queue,
            ReceiverTransportProtocol recvProto) {
        this.queue = queue;
        this.recvProto = recvProto;
    }

    @Override
    public void run() {
        while (!isShutDown()) {
            Pair<Publication, Long> pair;
            try {
                pair = queue.take();
                this.recvProto.chunkArrived(pair.getFirst(), pair.getSecond().longValue());
            } catch (InterruptedException e) {
                if (!isShutDown()) {
                    logger.debug(e.getMessage(), e);
                }
            }
        }
    }
}
