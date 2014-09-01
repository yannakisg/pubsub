package pubsub.channel.subscriber;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.transport.api.channel.ChannelSinkAPI;
import pubsub.util.ProducerConsumerQueue;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public class StreamSink /*extends ChannelSinkAPI*/ {
/*
    private PubSubID sid;
    private PubSubID rid;
    private int port;
    private long lifeTime;
    private Worker worker;
    private ProducerConsumerQueue<ChannelPacketInfo> prodCons;
    private static final Logger logger = Logger.getLogger(StreamSink.class);

    public StreamSink(PubSubID sid, PubSubID rid, int port, long lifeTime) throws SocketException, UnknownHostException {
        this.sid = sid;
        this.rid = rid;
        this.port = port;
        this.lifeTime = lifeTime;

        this.worker = new Worker();
        this.prodCons = new ProducerConsumerQueue<ChannelPacketInfo>();

        logger.debug("SID: " + sid);
        logger.debug("RID: " + rid);
    }

    public void start() {
        this.announceSubscription(sid, rid, prodCons, lifeTime);
        this.startAll();
        this.worker.start();
    }

    private class Worker extends StoppableThread {

        private DatagramSocket socket;
        private InetAddress address;

        public Worker() throws SocketException, UnknownHostException {
            socket = new DatagramSocket();
            address = InetAddress.getByName("127.0.0.1");
            
            this.setNamePrefix("StreamSink/Worker");
        }

        private void runVLC() {
            String command = "vlc udp://@:" + port;

            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        @Override
        public void run() {
            ChannelPacketInfo packetInfo;
            DatagramPacket packet;
            boolean received = false;
            int count = 0;

            while (!isShutDown()) {
                try {
                    packetInfo = prodCons.getConsumer().take();

                    if (!received) {
                        runVLC();
                        received = true;
                    }
                    
                    logger.debug("Received Packet [Worker] => " + count);
                    count++;
                    
                    packet = new DatagramPacket(packetInfo.getData(), packetInfo.getData().length);
                    packet.setAddress(address);
                    packet.setPort(port);

                    socket.send(packet);
                } catch (InterruptedException ex) {
                    logger.error(ex.getMessage(), ex);
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }*/
}
