package pubsub.channel.publisher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import pubsub.PubSubID;
import pubsub.transport.api.channel.ChannelSourceAPI;
import pubsub.transport.api.channel.ChannelSourceEntryEvent;
import pubsub.transport.api.channel.PacketInfoEntry;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public class StreamSource /*extends ChannelSourceAPI*/ {
/*
    private PubSubID sid;
    private PubSubID rid;
    private String fileName;
    private long lifeTime;
    private int port;
    private Worker worker;
    private static final Logger logger = Logger.getLogger(StreamSource.class);

    public StreamSource(PubSubID sid, PubSubID rid, int port, String fileName, long lifeTime) {
        this.sid = sid;
        this.rid = rid;
        this.port = port;
        this.fileName = fileName;
        this.lifeTime = lifeTime;

        this.worker = new Worker();
        
        logger.debug("SID: " + sid);
        logger.debug("RID: " + rid);
    }

    public void start() {
        this.announcePublication(sid, rid, lifeTime);
        this.addListener(this);
        this.startAll();
        this.worker.start();
    }

    @Override
    public void rvpInstructReceived(ChannelSourceEntryEvent event) {
        logger.debug("rvpInstructReceived");

        worker.setID(event.entry().getID());

        runVLC();
    }

    private void runVLC() {
        String command = "vlc -vvv " + fileName + " --sout udp:127.0.0.1:" + port + " --ttl 10";

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private class Worker extends StoppableThread {

        private int id;

        public void setID(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            this.setNamePrefix("StreamSource/Worker");
            try {
                DatagramSocket socket = new DatagramSocket(port);
                DatagramPacket dgrmPacket = new DatagramPacket(new byte[1500], 1500);
                PacketInfoEntry entry;
                while (!isShutDown()) {
                    socket.receive(dgrmPacket);
                    entry = new PacketInfoEntry(sid, rid, new ChannelPacketInfo(id, Arrays.copyOfRange(dgrmPacket.getData(), 0, dgrmPacket.getLength())));
                    StreamSource.this.enqueue(entry);
                }
            } catch (SocketException ex) {
                logger.error(ex.getMessage(), ex);
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }
        }
    }*/
}
