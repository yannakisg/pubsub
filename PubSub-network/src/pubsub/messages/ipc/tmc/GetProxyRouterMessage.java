package pubsub.messages.ipc.tmc;

import java.nio.ByteBuffer;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.Link;

/**
 *
 * @author John Gasparis
 */
public class GetProxyRouterMessage extends TMCIPCMessage {

    private Link prRouterLink;

    public GetProxyRouterMessage(Link proxyRouterLink) {
        super(MessageType.Type.GET_PROXY_ROUTER);
        this.prRouterLink = proxyRouterLink;
    }

    private GetProxyRouterMessage() {
        this(null);
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength() + (prRouterLink != null ? prRouterLink.getSerializedLength() : 0);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        if (prRouterLink != null) {
            prRouterLink.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        if (buff.hasRemaining()) {
            prRouterLink = Link.parseByteBuffer(buff);
        }
    }

    public Link getProxyRouterLink() {
        return prRouterLink;
    }

    public static GetProxyRouterMessage parseByteBuffer(ByteBuffer buff) {
        GetProxyRouterMessage msg = new GetProxyRouterMessage();
        msg.readBuffer(buff);
        return msg;
    }
}
