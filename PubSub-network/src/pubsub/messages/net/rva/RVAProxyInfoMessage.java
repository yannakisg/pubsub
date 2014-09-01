package pubsub.messages.net.rva;

import java.nio.ByteBuffer;
import pubsub.ForwardIdentifier;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.Link;

/**
 *
 * @author John Gasparis
 */
public class RVAProxyInfoMessage extends BaseRVANetMessage {

    private Link link;
    private static int LENGTH = -1;

    public RVAProxyInfoMessage(Link link, ForwardIdentifier fid) {
        super(MessageType.Type.RVA_PROXY_INFO_MESSAGE, fid);
        this.link = link;
    }

    private RVAProxyInfoMessage() {
        this(null, null);
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return this.link;
    }

    @Override
    public int getSerializedLength() {
        if (LENGTH == -1) {
            LENGTH = super.getSerializedLength() + this.link.getSerializedLength();
        }
        return LENGTH;
    }

    @Override
    public void writeTo(ByteBuffer buff) {
        super.writeTo(buff);
        this.link.writeTo(buff);
    }

    @Override
    public void readBuffer(ByteBuffer buff) {
        super.readBuffer(buff);
        this.link = Link.parseByteBuffer(buff);
    }

    public static RVAProxyInfoMessage parseByteBuffer(ByteBuffer buff) {
        RVAProxyInfoMessage msg = new RVAProxyInfoMessage();
        msg.readBuffer(buff);
        return msg;
    }
}
