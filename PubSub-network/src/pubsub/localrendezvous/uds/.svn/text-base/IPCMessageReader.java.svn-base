package pubsub.localrendezvous.uds;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import pubsub.localrendezvous.LocRCIPCMessage;
import pubsub.localrendezvous.LocRCIPCMessage.MessageType;
import pubsub.util.StoppableThread;

/**
 *
 * @author John Gasparis
 */
public class IPCMessageReader extends StoppableThread {

    private static final Logger logger = Logger.getLogger(IPCMessageReader.class);
    private InputStream inputStream;
    private IPCMessageWriter writer;
    private MessageProcessor msgProc;
    private byte[] headerBuff;
    private byte[] dataBuffer;
    private boolean closed = false;

    public IPCMessageReader(MessageProcessor msgProc, InputStream inputStream, String name) {
        this.inputStream = inputStream;
        this.headerBuff = new byte[LocRCIPCMessage.HEADER_LENGTH];
        this.dataBuffer = null;
        this.msgProc = msgProc;
        this.setName(name + "/IPCMessageReader");
    }

    public void setIPCMessageWriter(IPCMessageWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        int nRead;
        ByteBuffer byteBufHeader;
        MessageType msgType;
        LocRCIPCMessage message;
        int length;

        while (!isShutDown()) {
            try {
                nRead = inputStream.read(headerBuff);

                if (nRead < 0) {
                    logger.error("InputStream read : " + nRead);
                    continue;
                }

                byteBufHeader = ByteBuffer.wrap(headerBuff);

                msgType = MessageType.findByType(byteBufHeader.get());
                length = byteBufHeader.getInt();

                dataBuffer = new byte[length];

                nRead = inputStream.read(dataBuffer);

                if (nRead < 0) {
                    logger.error("InputStream read : " + nRead);
                    continue;
                }

                if (nRead != length) {
                    logger.error("What am I doing now ?????");
                }

                message = LocRCIPCMessage.parseBuffer(ByteBuffer.wrap(dataBuffer), msgType);
                msgProc.addToQueue(message, writer);
            } catch (IOException ex) { 
                if (!isShutDown()) {
                    close();
                }
            }
        }
    }

    public void close() {
        if (!closed) {
            closed = true;
            super.shutDown();

            try {
                inputStream.close();
            } catch (IOException ex) {
            }

            this.interrupt();

            writer.close();
        }
    }
}
