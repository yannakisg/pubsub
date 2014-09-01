package pubsub.transport;

/**
 *
 * @author John Gasparis
 */
public class StreamInfo {

    private int port;
    private int packetSize;

    public StreamInfo(int port, int packetSize) {
        this.port = port;
        this.packetSize = packetSize;
    }

    public int getPacketSize() {
        return this.packetSize;
    }

    public int getPort() {
        return this.port;
    }
}
