package pubsub.forwarding.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

public abstract class BaseCommunicationLink implements CommunicationLink {
    private static final Logger logger = Logger.getLogger(BaseCommunicationLink.class);
    private final DatagramSocket receiverSocket;
    private final DatagramSocket senderSocket;
    private AtomicLong packetRX = new AtomicLong(0);
    private AtomicLong byteRX = new AtomicLong(0);
    private static AtomicLong sentPackets = new AtomicLong(0);
    private static AtomicLong receivedPackets = new AtomicLong(0);

    public BaseCommunicationLink(DatagramSocket receiverSocket, int bufferSize) throws SocketException {
        this.receiverSocket = receiverSocket;
        this.senderSocket = new DatagramSocket();
    }

    @Override
    public long getPacketRX() {
        return this.packetRX.get();
    }

    @Override
    public void increamentPacketRX() {
        this.packetRX.incrementAndGet();
    }

    @Override
    public long getByteRX() {
        return this.byteRX.get();
    }

    @Override
    public void increamentByteRX(int value) {
        this.byteRX.addAndGet(value);
    }

    protected void receive(DatagramPacket p) throws IOException {
        this.receiverSocket.receive(p);
       // logger.debug("Received[UDPLink] => " + receivedPackets.incrementAndGet());
    }

    protected void send(DatagramPacket packet) throws IOException {        
        this.senderSocket.send(packet);
       // logger.debug("Sent[UDPLink] => " + sentPackets.incrementAndGet());
    }

    @Override
    public boolean isDown() {
        return receiverSocket.isClosed();
    }

    @Override
    public String toString() {
        return "address: " + this.receiverSocket.getLocalAddress().toString()
                + "/" + this.receiverSocket.getLocalPort();
    }
}
