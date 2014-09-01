package pubsub.hashing;

/**
 *
 * @author John Gasparis
 */
public class ShiftAddXorHash implements HashFunction {

    @Override
    public int hash(byte[] data) {
        int length = data.length;
        int h = 0;
        int i;

        for (i = 0; i < length; i++) {
            h ^= (h << 5) + (h >> 2) + (data[i] & 0xFF);
        }

        return h;
    }
}
