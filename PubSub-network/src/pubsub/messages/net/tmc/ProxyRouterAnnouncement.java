package pubsub.messages.net.tmc;

import java.nio.ByteBuffer;
import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.Link;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class ProxyRouterAnnouncement extends TMCNetMessage {

    private Link proxyRouterLink;
    private short ttlToProxy;

    public ProxyRouterAnnouncement(ByteIdentifier routerID, Link proxyRouterLink, short ttlToProxy, ForwardIdentifier fid) {
        super(MessageType.Type.NET_LSA_PROXY_ROUTER, fid, routerID);
        this.proxyRouterLink = proxyRouterLink;
        this.ttlToProxy = ttlToProxy;
    }

    private ProxyRouterAnnouncement() {
        this.proxyRouterLink = null;
    }

    @Override
    public int getSerializedLength() {
        return super.getSerializedLength()
                + (proxyRouterLink != null ? proxyRouterLink.getSerializedLength() : 0) + Util.SIZEOF_SHORT;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        buff.putShort(ttlToProxy);
        if (proxyRouterLink != null) {
            proxyRouterLink.writeTo(buff);
        }
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        ttlToProxy = buff.getShort();
        
        if (buff.hasRemaining()) {
            proxyRouterLink = Link.parseByteBuffer(buff);
        } else {
            proxyRouterLink = null;
        }
    }
    
    public void incrementTTL() {
        this.ttlToProxy++;
    }
    
    public short getTTL() {
        return this.ttlToProxy;
    }

    public static ProxyRouterAnnouncement parseByteBuffer(ByteBuffer buff) {
        ProxyRouterAnnouncement announc = new ProxyRouterAnnouncement();
        announc.readBuffer(buff);
        return announc;
    }

    public Link getProxyRouterLink() {
        return this.proxyRouterLink;
    }

    public void setProxyRouterLink(Link link) {
        this.proxyRouterLink = link;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProxyRouterAnnouncement other = (ProxyRouterAnnouncement) obj;
        if (this.proxyRouterLink != other.proxyRouterLink && (this.proxyRouterLink == null || !this.proxyRouterLink.equals(other.proxyRouterLink))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.proxyRouterLink != null ? this.proxyRouterLink.hashCode() : 0);
        hash = 23 * hash + +super.hashCode();
        return hash;
    }
}
