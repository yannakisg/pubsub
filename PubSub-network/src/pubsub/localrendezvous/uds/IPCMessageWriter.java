package pubsub.localrendezvous.uds;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Logger;
import pubsub.localrendezvous.LocRCIPCMessage;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public class IPCMessageWriter extends StoppableThread {

    private static final Logger logger = Logger.getLogger(IPCMessageWriter.class);
    private ProducerConsumerQueue<LocRCIPCMessage> queue;
    private boolean closed = false;
    private IPCMessageReader reader;
    private OutputStream outputStream;

    public IPCMessageWriter(OutputStream outputStream, String name) {
        this.queue = ProducerConsumerQueue.createNew();
        this.outputStream = outputStream;
        this.setName(name + "/IPCMessageWriter");
    }

    public void setIPCMessageReader(IPCMessageReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        LocRCIPCMessage mesg;

        while (!isShutDown()) {
            try {
                mesg = queue.getConsumer().take();

                outputStream.write(mesg.toBytes());
                outputStream.flush();
            } catch (InterruptedException ex) {
            } catch (IOException ex) {
                //logger.error(ex.getMessage(), ex);
                if (!isShutDown()) {
                    close();
                }
            } catch (Exception e) {                
            }
        }
    }

    public synchronized void addToQueue(LocRCIPCMessage mesg) {
        queue.getProduder().offer(mesg);
    }

    public void close() {
        if (!closed) {
            closed = true;
            super.shutDown();

            try {
                outputStream.close();
            } catch (IOException ex) {
            }

            this.interrupt();

            reader.close();
        }
    }
}
