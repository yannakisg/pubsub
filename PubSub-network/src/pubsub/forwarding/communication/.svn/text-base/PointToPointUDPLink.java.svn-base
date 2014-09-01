package pubsub.forwarding.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.forwarding.FwdComponent;
import pubsub.util.Pair;
import pubsub.util.Producer;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

public class PointToPointUDPLink extends BaseCommunicationLink {

    public static int MTU = 5000;

    public void configureMTU(int length) {
        MTU = length;
    }
    private static final Logger logger = Logger.getLogger(PointToPointUDPLink.class);
    private ProducerConsumerQueue<Pair<byte[], Integer>> bufferOut = ProducerConsumerQueue.createNew();
    private ProducerConsumerQueue<DatagramPacket> bufferIn = ProducerConsumerQueue.createNew();
    private InetAddress neighborAddr;
    private int dstPort;
    private SenderThread senderThread;
    private ReceiverThread receiverThread;
    private String strRep = null;
    private DatagramPacket packet = null;


    public PointToPointUDPLink(DatagramSocket socket, InetAddress neighbor,
            int port, String linkName) throws SocketException {
        super(socket, MTU);

        this.neighborAddr = neighbor;
        this.dstPort = port;

        senderThread = new SenderThread();
        senderThread.setNamePrefix(linkName);

        receiverThread = new ReceiverThread(this);
        receiverThread.setNamePrefix(linkName);
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "outLink.log", false));
        } catch (IOException ex) {
        }
    }

    @Override
    public DatagramPacket receive() throws InterruptedException {
        return bufferIn.getConsumer().take();
    }

    @Override
    public synchronized boolean transmit(Pair<byte[], Integer> pair) {
        return bufferOut.getProduder().offer(pair);
    }
    
    @Override
    public void transmitDirectly(byte[] data, int length) {
        try {
            //logger.debug("Sending to: " + neighborAddr + " " + length + " bytes");
            FwdComponent.addSentBytes(length);
            if (packet == null) {
                packet = new DatagramPacket(data, length);
                packet.setAddress(neighborAddr);
                packet.setPort(dstPort);
            } else {
                packet.setData(data, 0, length);
            }
            
            send(packet);
        } catch (Exception e) {
            logger.debug("Exception while sending to " + neighborAddr + " [" + e.getMessage() + "]");
        }
    }

    private void send(Pair<byte[], Integer> pair) {
        transmitDirectly(pair.getFirst(), pair.getSecond());
    }

    public void operate() {
        senderThread.start();
        receiverThread.start();
    }

    @Override
    public String toString() {
        if (strRep == null) {
            strRep = this.getClass().getName();
            strRep += ", endpoint: " + this.neighborAddr.getHostAddress() + "/"
                    + this.dstPort;
        }
        return strRep;
    }

    private class ReceiverThread extends StoppableThread {

        private PointToPointUDPLink link = null;

        public ReceiverThread(PointToPointUDPLink link) {
            this.link = link;
        }

        @Override
        public void run() {
            DatagramPacket p;
            Producer<DatagramPacket> producer = bufferIn.getProduder();

            while (!isShutDown()) {
                try {
                    p = new DatagramPacket(new byte[MTU], MTU);
                    link.receive(p);

                    if (!p.getAddress().equals(neighborAddr) || p.getLength() > MTU) {
                        logger.debug("Invalid Packet.");
                    } else {
                        //logger.debug("Size[" +dstPort+ "]: " + bufferIn.getSize());
                        if (!producer.offer(p)) {                            
                            logger.debug("Discard packet");
                        }
                    }
                } catch (IOException ex) {
                    if (!isDown()) {
                        logger.error(ex.getMessage(), ex);
                        continue;
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private class SenderThread extends StoppableThread {

        @Override
        public void run() {
            Pair<byte[], Integer> pair;
            while (!isShutDown()) {
                try {
                    pair = bufferOut.getConsumer().take();
                    send(pair);
                } catch (InterruptedException e) {
                    if (!isShutDown()) {
                        logger.debug(e.getMessage());
                    } else {
                        break;
                    }
                } 
            }
        }
    }
}
