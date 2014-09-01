package pubsub.bloomfilter;

import java.util.Arrays;

/**
 *
 * @author tsilo
 */
public class CountingBloomFilter {

    private long[] data;

    public CountingBloomFilter(long[] buf) {
        this.data = buf;
    }

    public CountingBloomFilter(int length, long val) {
        this.data = new long[length];
        Arrays.fill(this.data, val);
    }

    public CountingBloomFilter(int length) {
        this(length, 0);
    }

    public static CountingBloomFilter fromBloomFilter(BloomFilter bf) {
        long[] cbfData = new long[bf.length() * 8];
        Arrays.fill(cbfData, 0);

        byte[] bfData = bf.getBytes();
        for (int i = bfData.length - 1; i >= 0; i--) {
            byte val = bfData[i];
            int pos = (i + 1) * 8;
            for (int j = 7; j >= 0; j--) {
                int bit = val & 0x1;
                cbfData[pos + j - 8] = bit;
                val = (byte) (val >>> 1);
            }
        }
        CountingBloomFilter cbf = new CountingBloomFilter(cbfData);
        return cbf;
    }

    public void add(BloomFilter bf) {
        CountingBloomFilter tmp = fromBloomFilter(bf);
        if (this.data.length != tmp.data.length) {
            throw new IllegalArgumentException("bloom filters have different size");
        }

        for (int i = 0; i < this.data.length; i++) {
            this.data[i] += tmp.data[i];
        }
    }

    public void remove(BloomFilter bf) {
        CountingBloomFilter tmp = fromBloomFilter(bf);
        if (this.data.length != tmp.data.length) {
            throw new IllegalArgumentException("bloom filters have different size");
        }

        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = Math.max(this.data[i] - tmp.data[i], 0);
        }
    }

    public BloomFilter toBloomFilter() {
        byte[] bf = new byte[this.data.length];
        for (int i = 0; i < data.length; i++) {
            bf[i] = (byte) (data[i] == 0 ? 0 : 1);
        }
        return new BloomFilter(bf);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < this.data.length; i++) {
            builder.append(this.data[i]);
            if (i != this.data.length - 1) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public static void main(String[] args) {
        String one = "00101010";
        String two = "00010110";
        BloomFilter bf = BloomFilter.fromBinaryRepresentation(one);
        System.out.println(bf);
        BloomFilter bf2 = BloomFilter.fromBinaryRepresentation(two);
        System.out.println(bf2);

        CountingBloomFilter cbf = CountingBloomFilter.fromBloomFilter(bf);
        cbf.add(bf2);
        System.out.println(cbf);
    }
}
