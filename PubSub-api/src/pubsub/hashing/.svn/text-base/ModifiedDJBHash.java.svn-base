package pubsub.hashing;

/**
 *
 * @author John Gasparis
 */
public class ModifiedDJBHash implements HashFunction {

    @Override
    public int hash(byte[] data) {
        int length = data.length;
        int h = 0;
        int i;

        for (i = 0; i < length; i++) {
            h = 33 * h ^ (Byte.valueOf(data[i]).hashCode());
        }

        return h;
    }
}
