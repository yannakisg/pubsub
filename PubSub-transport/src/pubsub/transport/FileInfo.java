package pubsub.transport;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import pubsub.PubSubID;
import pubsub.messages.net.transport.DataMessage;

/**
 *
 * @author John Gasparis
 */
public class FileInfo {

    private RandomAccessFile raf;
    private long fileSize;
    private int dataChunkSize;
    private int totalChunks;
    private PubSubID sid;
    private PubSubID rid;
    private static final int DEFAULT_SIZE = 256;
    private int lowestChunkNum = Integer.MAX_VALUE;
    private Map<Integer, DataMessage> dataCache;

    public FileInfo(File file, int chunkSize, PubSubID sid, PubSubID rid) throws IOException {
        this.raf = new RandomAccessFile(file, "r");
        this.dataChunkSize = chunkSize;
        this.fileSize = raf.length();
        this.totalChunks = (int) (fileSize / chunkSize);

        if (raf.length() % chunkSize != 0) {
            totalChunks++;
        }

        this.dataCache = new HashMap<Integer, DataMessage>(DEFAULT_SIZE, (float) 1.0);
        this.sid = sid;
        this.rid = rid;
    }

    public DataMessage getNextDataMessage(int chunkNum, long timestamp) throws IOException {
        DataMessage message = dataCache.get(chunkNum);
        if (message != null) {
            return message;
        } else {

            long position = chunkNum * dataChunkSize;

            raf.seek(position);
            int frameLen = (int) Math.min(fileSize - position, dataChunkSize);
            byte[] frame = new byte[frameLen];
            raf.read(frame);

            message = new DataMessage(sid, rid, chunkNum, frame, timestamp);

            if (dataCache.size() == DEFAULT_SIZE) {
                dataCache.remove(lowestChunkNum);
            }

            dataCache.put(chunkNum, message);

            if (chunkNum < lowestChunkNum) {
                lowestChunkNum = chunkNum;
            }

            return message;
        }
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getDataChunkSize() {
        return this.dataChunkSize;
    }

    public void close() throws IOException {
        this.raf.close();
    }
}
