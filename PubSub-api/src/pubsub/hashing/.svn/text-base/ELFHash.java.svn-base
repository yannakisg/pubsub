package pubsub.hashing;

/**
 *
 * @author John Gasparis
 */
public class ELFHash implements HashFunction {

    @Override
    public int hash(byte[] data) {
        int length = data.length;
        int h = 0;
        int g, i;

        for (i = 0; i < length; i++) {
            h = (h << 4) + (data[i] & 0xFF);
            g = h & 0xF0000000;

            if (g != 0) {
                h ^= g >> 24;
            }

            h &= ~g;
        }

        return h;
    }
}
