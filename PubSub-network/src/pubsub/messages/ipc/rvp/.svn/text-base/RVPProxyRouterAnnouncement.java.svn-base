package pubsub.messages.ipc.rvp;

import java.nio.ByteBuffer;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.Link;

/**
 *
 * @author John Gasparis
 */
public class RVPProxyRouterAnnouncement extends RVPIPCMessage {

    private Link proxyRouterLink;

    private RVPProxyRouterAnnouncement() {
        this(null);
    }

    public RVPProxyRouterAnnouncement(Link proxyRouterLink) {
        super(MessageType.Type.IPC_LSA_PROXY_ROUTER);

        this.proxyRouterLink = proxyRouterLink;
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength()
                + (proxyRouterLink != null ? proxyRouterLink.getSerializedLength() : 0);
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        if (proxyRouterLink != null) {
            proxyRouterLink.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);

        if (buff.hasRemaining()) {
            proxyRouterLink = Link.parseByteBuffer(buff);
        } else {
            proxyRouterLink = null;
        }
    }

    public Link getProxyRouterLink() {
        return this.proxyRouterLink;
    }

    public void setProxyRouterLink(Link link) {
        this.proxyRouterLink = link;
    }

    public static RVPProxyRouterAnnouncement parseByteArray(byte[] data) {
        RVPProxyRouterAnnouncement announc = new RVPProxyRouterAnnouncement();
        announc.fromBytes(data);

        return announc;
    }
    
    public static RVPProxyRouterAnnouncement parseByteBuffer(ByteBuffer buffer) {
        RVPProxyRouterAnnouncement announc = new RVPProxyRouterAnnouncement();
        announc.readBuffer(buffer);

        return announc;
    }
}
