package pubsub.hashing;

/**
 *
 * @author John Gasparis
 */
public class OneAtATimeHash implements HashFunction {

    @Override
    public int hash(byte[] data) {
        int length = data.length;
        int h = 0;
        int i;

        for (i = 0; i < length; i++) {
            h += (data[i] & 0xFF);
            h += (h << 10);
            h ^= (h >> 6);
        }

        h += (h << 3);
        h ^= (h >> 11);
        h += (h << 15);

        return h;
    }
}
