package pubsub.transport.channel.source;

import java.util.Arrays;

/**
 *
 * @author John Gasparis
 */
public class DataProducer {

    private int chunkSize;
    private long totalChunks;
    private int lastChunkSize;
    private int produced;
    private boolean producedLastChunk;

    public DataProducer(long bitRate, int chunkSize) {
        this.chunkSize = chunkSize;

        long byteRate = (bitRate >> 3);
        int rem;

        totalChunks = (byteRate / chunkSize);
        if ((rem = (int) (byteRate % chunkSize)) != 0) {
            lastChunkSize = rem;
            totalChunks++;
        } else {
            lastChunkSize = chunkSize;
        }

        this.produced = 0;
        this.producedLastChunk = false;
    }

    public byte[] produce() {
        produced++;
        byte[] data;

        if (produced == totalChunks) {
            produced = 0;
            data = new byte[lastChunkSize];
            producedLastChunk = true;
        } else {
            data = new byte[chunkSize];
            producedLastChunk = false;
        }
        Arrays.fill(data, (byte) 0);
        return data;
    }
    
    public boolean producedLastChunk() {
        return producedLastChunk;
    }
}
