package pubsub.hashing;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author John Gasparis
 */
public class HashFunctions {

    private static final List<HashFunction> lHashFuncs = new ArrayList<HashFunction>();

    static {
        lHashFuncs.add(new MurmurHash());
        lHashFuncs.add(new ShiftAddXorHash());
        lHashFuncs.add(new OneAtATimeHash());
        lHashFuncs.add(new JSWHash());
        lHashFuncs.add(new ELFHash());
        lHashFuncs.add(new ModifiedDJBHash());
    }

    public static List<HashFunction> getFunctions() {
        return lHashFuncs;
    }

    public static HashFunction getMurmurHashInstance() {
        return lHashFuncs.get(0);
    }

    public static HashFunction getShiftAddXorHashInstance() {
        return lHashFuncs.get(1);
    }

    public static HashFunction getOneAtATimeHashInstance() {
        return lHashFuncs.get(2);
    }

    public static HashFunction getJSWHashInstance() {
        return lHashFuncs.get(3);
    }

    public static HashFunction getELFHashInstance() {
        return lHashFuncs.get(4);
    }

    public static HashFunction getModifiedDJBHashInstance() {
        return lHashFuncs.get(5);
    }
}
