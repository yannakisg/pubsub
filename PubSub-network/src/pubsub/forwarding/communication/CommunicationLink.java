package pubsub.forwarding.communication;


import java.net.DatagramPacket;
import pubsub.util.Pair;

public interface CommunicationLink {

    public DatagramPacket receive() throws InterruptedException;

    public boolean transmit(Pair<byte[], Integer> pair);
    
    public void transmitDirectly(byte[] data, int length);

    public boolean isDown();

    public long getPacketRX();

    public void increamentPacketRX();

    public long getByteRX();

    public void increamentByteRX(int length);
}
