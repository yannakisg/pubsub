package pubsub.messages.ipc.tmc;

import java.nio.ByteBuffer;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.GatewayNode;

/**
 *
 * @author John Gasparis
 */
public class DefaultGWMessage extends TMCIPCMessage {

    private GatewayNode gw;

    public DefaultGWMessage(GatewayNode gw) {
        super(MessageType.Type.GET_DEFAULT_GW);
        this.gw = gw;
    }

    private DefaultGWMessage() {
        this(null);
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength() + (gw != null ? gw.getSerializedLength() : 0);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        if (gw != null) {
            gw.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        if (buff.hasRemaining()) {
            gw = GatewayNode.parseByteBuffer(buff);
        }
    }

    public GatewayNode getDefaultGW() {
        return gw;
    }

    public static DefaultGWMessage parseByteBuffer(ByteBuffer buff) {
        DefaultGWMessage msg = new DefaultGWMessage();
        msg.readBuffer(buff);
        return msg;
    }
}
