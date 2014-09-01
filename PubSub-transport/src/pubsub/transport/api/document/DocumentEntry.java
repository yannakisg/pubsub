package pubsub.transport.api.document;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.Comparator;
import org.apache.log4j.Logger;
import pubsub.messages.net.transport.DataMessage;
import pubsub.util.SortedList;

/**
 *
 * @author John Gasparis
 */
public class DocumentEntry {

    protected static enum Status {

        PENDING_CONTROL,
        PENDING_CHUNKS,
        RECEIVED
    }
    private static final Logger logger = Logger.getLogger(DocumentEntry.class);
    private Status status;
    private BitSet receivedChunks;
    private SortedList<DataMessage> sortedList;
    private static final int DEFAULT_SIZE = 256;
    private int totalReceivedChunks;
    private RandomAccessFile raf;
    private int chunkSize;
    private long totalSize;

    public DocumentEntry(File file, int chunkSize) throws IOException {
        this.sortedList = new SortedList<DataMessage>(DEFAULT_SIZE, new Comparator<DataMessage>() {

            @Override
            public int compare(DataMessage o1, DataMessage o2) {
                if (o1.getChunkNum() > o2.getChunkNum()) {
                    return 1;
                } else if (o2.getChunkNum() > o1.getChunkNum()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        raf = new RandomAccessFile(file, "rw");
        status = Status.PENDING_CHUNKS;
        this.receivedChunks = new BitSet();
        this.totalReceivedChunks = 0;
        this.chunkSize = chunkSize;
        this.totalSize = 0;
    }

    public void addData(DataMessage message) {
        if (sortedList.containsElement(message) || receivedChunks.get(message.getChunkNum())) {
            return;
        }
        if (sortedList.size() == DEFAULT_SIZE) {
            try {
                writeData();
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        
        totalSize += message.getData().length;
        totalReceivedChunks++;
        sortedList.add(message);
        receivedChunks.set(message.getChunkNum());
    }

    public int getTotalReceivedChunks() {
        return this.totalReceivedChunks;
    }

    public void documentReceived() {
        status = Status.RECEIVED;
    }

    public boolean hasReceived(int chunkNum) {
        return receivedChunks.get(chunkNum);
    }

    public Status getStatus() {
        return this.status;
    }
    
    public long getTotalSize() {
        return this.totalSize;
    }

    private void writeData() throws IOException {
        long position;
        for (DataMessage msg : sortedList) {
            position = msg.getChunkNum() * chunkSize;
            raf.seek(position);
            raf.write(msg.getData());
        }

        sortedList.clear();
    }
}
