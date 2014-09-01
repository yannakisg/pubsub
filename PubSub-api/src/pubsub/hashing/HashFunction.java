package pubsub.hashing;

/**
 *
 * @author John Gasparis
 */
public interface HashFunction {

    public int hash(byte[] data);
}
