package pubsub.forwarding.communication;

import java.nio.ByteBuffer;
import pubsub.BaseSerializableStruct;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class AttachLinkMessage extends BaseSerializableStruct {

    private static final int LENGTH = Util.SIZEOF_INT + Util.SIZEOF_INT + Util.SIZEOF_INT + Util.SIZEOF_BYTE + Util.SIZEOF_DOUBLE;
    private int localPort;
    private int remotePort;
    private byte[] address;
    private boolean isServer;
    private double weight;

    public AttachLinkMessage(int localPort, int remotePort, byte[] address, double weight, boolean isServer) {
        this.localPort = localPort;
        this.remotePort = remotePort;
        this.address = address;
        this.isServer = isServer;
        this.weight = weight;
    }

    private AttachLinkMessage() {
        this(-1, -1, new byte[4], -1, false);
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public byte[] getAddress() {
        return this.address;
    }
    
    public double getWeight() {
        return this.weight;
    }

    public boolean isServer() {
        return this.isServer;
    }

    @Override
    public int getSerializedLength() {
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        buff.putInt(localPort).putInt(remotePort).put(address).putDouble(weight).put(isServer ? (byte) 1 : (byte) 0);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        localPort = buff.getInt();
        remotePort = buff.getInt();

        if (address == null) {
            address = new byte[4];
        }

        buff.get(address);
        weight = buff.getDouble();

        isServer = (buff.get() == (byte) 1);
    }

    public static AttachLinkMessage createNew() {
        AttachLinkMessage msg = new AttachLinkMessage();
        return msg;
    }

    public static int getLength() {
        return LENGTH;
    }
}
