package pubsub.localrendezvous.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import pubsub.localrendezvous.LocRCIPCMessage;

import pubsub.localrendezvous.LocRCIPCMessage.MessageType;

/**
 *
 * @author tsilo
 */
public class IPCMessageReader {

    private static enum State {

        READING_HEADER, READING_DATA, COMPLETED;
    }
    private final SelectionKey key;
    private final ByteBuffer headerBuffer = ByteBuffer.allocate(LocRCIPCMessage.HEADER_LENGTH);
    private ByteBuffer mesgBuffer = null;
    private State state = State.READING_HEADER;
    private MessageType msgType = null;
    private LocRCIPCMessage ipcMesg = null;

    public IPCMessageReader(SelectionKey selKey) {
        this.key = selKey;
        reset();
    }

    public void reset() {
        headerBuffer.clear();
        state = State.READING_HEADER;

        mesgBuffer = null;
        msgType = null;
        ipcMesg = null;
    }

    public void read() throws IOException {
        if (state == State.READING_HEADER) {
            readHeader();
        }

        if (state == State.READING_DATA) {
            readData();
        }
    }

    private void readHeader() throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int read = channel.read(headerBuffer);

        if (read < 0) {
            cancelAndReset();
            throw new IOException("channel read " + read);
        }

        if (!headerBuffer.hasRemaining()) {
            this.headerBuffer.rewind();
            this.state = State.READING_DATA;
        }
    }

    private void readData() throws IOException {
        if (mesgBuffer == null) {
            msgType = MessageType.findByType(headerBuffer.get());
            int len = headerBuffer.getInt();
            mesgBuffer = ByteBuffer.allocateDirect(len);
        }

        SocketChannel channel = (SocketChannel) key.channel();
        int read = channel.read(mesgBuffer);
        if (read < 0) {
            cancelAndReset();
            throw new IOException("channel read " + read);
        }

        if (!mesgBuffer.hasRemaining()) {
            this.state = State.COMPLETED;
            this.mesgBuffer.rewind();
            this.ipcMesg = LocRCIPCMessage.parseBuffer(mesgBuffer, this.msgType);
        }
    }

    private void cancelAndReset() {
        this.key.cancel();
        reset();
    }

    public boolean completed() {
        return this.state == State.COMPLETED;
    }

    public LocRCIPCMessage getIPCMessage() {
        return this.ipcMesg;
    }
}
