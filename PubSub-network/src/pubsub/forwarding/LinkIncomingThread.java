package pubsub.forwarding;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pubsub.ContentType;
import pubsub.PubSubID;
import pubsub.Publication;
import pubsub.forwarding.communication.CommunicationLink;
import pubsub.util.FwdConfiguration;
import pubsub.util.StoppableThread;
import pubsub.util.Util;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class LinkIncomingThread extends StoppableThread {

    private static Logger logger = Logger.getLogger(LinkIncomingThread.class);
    private FwdComponent fwdC;
    private CommunicationLink link;
    private static int countRcvdChannelPackets = 0;

    public LinkIncomingThread(CommunicationLink communicationLink,
            FwdComponent fwdComponent) {
        this.link = communicationLink;
        this.fwdC = fwdComponent;
        try {
            logger.addAppender(new FileAppender(new PatternLayout("%d [%t] %-5p %c - %m%n"), "linkInc.log", false));
        } catch (IOException ex) {
        }
    }

    @Override
    public void run() {
        DatagramPacket packet;
        KeepAliveMessage kam;
        ContentType cType;
        ByteBuffer buffer;
        byte[] data;
        short ttl;
        double flip;
        int sidPos = Util.SIZEOF_BYTE;
        int ridPos = Util.SIZEOF_BYTE + PubSubID.ID_LENGTH;
        int keepAliveLength = FwdConfiguration.ZFILTER_LENGTH + Util.SIZEOF_DOUBLE;
        int dataLen;
        Publication pub;
        PubSubID sid, rid;
        FwdStruct fp;
        int sentDataLen;

        while (!isShutDown()) {
            try {
                packet = this.link.receive();
                data = packet.getData();
                buffer = ByteBuffer.wrap(data);
                sentDataLen = packet.getLength();
                buffer.limit(sentDataLen);

                //logger.debug("Receiving from: " + packet.getAddress() + " " + packet.getLength() + " bytes");

                dataLen = packet.getLength() - FwdConfiguration.PUBLICATION_HEADER_LENGTH;

                if (!equalsID(data, sidPos, FwdComponent.FWD_SID)) {
                    logger.debug("Unknown SID");
                    continue;
                }


                if (equalsID(data, ridPos, FwdComponent.FWD_KEEP_ALIVE) && dataLen == keepAliveLength) {
                    logger.debug("It is a keep alive message");
                    buffer.position(FwdConfiguration.PUBLICATION_HEADER_LENGTH);
                    kam = KeepAliveMessage.parseByteBuffer(buffer);
                    fwdC.handleKeepAlive(kam, link);
                    continue;
                }

                if (!equalsID(data, ridPos, FwdComponent.FWD_RID)) {
                    logger.debug("Unknown RID");
                    continue;
                }


                ttl = getTTL(buffer);

                ttl--;
                if (ttl < 0) {
                    logger.debug("TTL expired, discarding packet");
                    continue;
                } else {
                    setTTL(buffer, ttl);
                }
                try {
                    cType = getContentType(data);

                    if (cType == ContentType.REQUEST_IMMUTABLE_DATA) {
                        buffer.position(FwdConfiguration.NESTEDPUBDATA_INIT_POS);
                        fwdC.handleRequestMsg(buffer, data, sentDataLen, link);
                        continue;
                    } else if (cType == ContentType.IMMUTABLE_DATA) {
                        flip = Util.getRandomDouble();
                        if (flip < CachingElement.CACHE_PROBABILITY) {
                            buffer.position(FwdConfiguration.NESTEDPUB_INIT_POS);
                            fwdC.store(buffer);
                        }
                    }/* else if (cType == ContentType.CHANNEL) {
                    logger.debug("Received [FwdComponent] => " + countRcvdChannelPackets);
                    countRcvdChannelPackets++;
                    }*/
                    
                    fwdC.forward(buffer, data, sentDataLen, link);
                } catch (IllegalArgumentException ex) {
                    logger.debug(ex.getMessage());
                }
            } catch (InterruptedException ex) {
                if (!link.isDown()) {
                    logger.debug(ex.getMessage(), ex);
                    continue;
                } else {
                    break;
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                logger.debug(e.getMessage());
            }
        }
    }

    private boolean equalsID(byte[] byteArray, int initPos, PubSubID id) {
        int length = (PubSubID.ID_LENGTH + initPos);
        if (byteArray.length < length) {
            return false;
        }

        byte[] idByteArray = id.getId();
        int i, j;

        for (i = initPos, j = 0; i < length; i++, j++) {
            if (byteArray[i] != idByteArray[j]) {
                return false;
            }
        }

        return true;
    }

    private ContentType getContentType(byte[] byteArray) throws IllegalArgumentException {
        return ContentType.getType(byteArray[FwdConfiguration.PUBLICATION_HEADER_LENGTH + FwdConfiguration.FID_LENGTH]);
    }

    private short getTTL(ByteBuffer buffer) {
        return buffer.getShort(FwdConfiguration.PUBLICATION_HEADER_LENGTH);
    }

    private void setTTL(ByteBuffer buffer, short ttl) {
        buffer.putShort(FwdConfiguration.PUBLICATION_HEADER_LENGTH, ttl);
    }
    /*
     * pub = Publication.parseByteBuffer(buffer);
    
    sid = pub.getScopeId();
    rid = pub.getRendezvousId();
    dataLen = pub.getDataLength();
    
    if (!sid.equals(FwdComponent.FWD_SID)) {
    logger.debug("Unknown SID");
    continue;
    }
    
    if (rid.equals(FwdComponent.FWD_KEEP_ALIVE) && dataLen == keepAliveLength) {
    // logger.debug("It is a keep alive message");
    kam = KeepAliveMessage.parseByteArray(pub.getDataArray());
    fwdC.handleKeepAlive(kam, link);
    continue;
    }
    
    if (!rid.equals(FwdComponent.FWD_RID)) {
    logger.debug("Unknown RID");
    continue;
    }
    
    try {
    fp = FwdStruct.parseByteBuffer(pub.wrapData());
    // logger.debug("TTL: " + fp.getTTL());
    /* short newTTL = fp.decreamentAndGet();
    
    if (newTTL < 0) {
    logger.debug("TTL expired, discarding packet");
    continue;
    } else {
    FwdStruct.setTTL(pub.wrapData(), newTTL);
    //FwdStruct.setTTL(pub.wrapData(), newTTL);
    }
    
    fwdC.handleIncoming(fp, pub, link);
    } catch (IllegalArgumentException ex) {
    logger.debug(ex.getMessage());
    }
     */
}
