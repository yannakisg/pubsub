package pubsub.hashing;

import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class JSWHash implements HashFunction {

    private int[] table = new int[256];

    private void initializeTable(int[] table, int seed) {
        for (int i = 0; i < table.length; i++) {
            table[i] = Util.getRandomInteger();
        }
    }

    @Override
    public int hash(byte[] data) {
        int length = data.length;
        int h = 16777551;
        int i;

        initializeTable(table, length);

        for (i = 0; i < length; i++) {
            h = (h << 1 | h >> 31) ^ table[(data[i] & 0xFF)];
        }

        return h;
    }
}
