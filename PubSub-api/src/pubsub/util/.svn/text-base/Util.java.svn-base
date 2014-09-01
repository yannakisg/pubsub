package pubsub.util;

import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import pubsub.PubSubID;

/**
 * 
 * @author John Gasparis
 */
public class Util {

    private static final int TWO_EXPONENT;
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    /*
     * Throw a NullpointerException if o is null
     */
    public static void checkNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    public static void checkIllegal(Object o, String fieldName) {
        if (o == null) {
            throw new IllegalArgumentException("field " + fieldName
                    + " not initialized");
        }

    }

    public static <T> void printArray(T[] array) {
        for (T t : array) {
            System.out.print(t + " ");
        }
        System.out.println();
    }

    public static void printArray(byte[] array) {
        for (byte b : array) {
            System.out.print(b + " ");
        }
        System.out.println();
    }

    static {
        int bits = Byte.SIZE;
        int i, j;
        for (i = 1, j = 0; i != bits; i = i << 1, j++) {
        }


        TWO_EXPONENT = j;
    }
    public static final int SIZEOF_BYTE = bitsToByteSize(Byte.SIZE);
    public static final int SIZEOF_SHORT = bitsToByteSize(Short.SIZE);
    public static final int SIZEOF_INT = bitsToByteSize(Integer.SIZE);
    public static final int SIZEOF_FLOAT = bitsToByteSize(Float.SIZE);
    public static final int SIZEOF_DOUBLE = bitsToByteSize(Double.SIZE);
    public static final int SIZEOF_LONG = bitsToByteSize(Long.SIZE);
    private static long prevTime = System.currentTimeMillis();

    public static byte[] intToByteArray(int number) {
        return new byte[]{(byte) (number >> 24), (byte) (number >> 16),
                    (byte) (number >> 8), (byte) (number)};
    }

    public static int byteArrayToInt(byte[] array) {
        int num = 0;

        for (int i = 0; i < array.length; i++) {
            num = (num << 8) | (array[i] & 0xFF);
        }

        return num;
    }

    private static int bitsToByteSize(int num) {
        return (num >> TWO_EXPONENT);
    }

    public static byte[] sha256toBytes(byte[] data) {
        try {
            MessageDigest md = null;
            md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }

    }

    public static PubSubID sha256toPubSubId(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        byte[] hash = md.digest();

        byte[] id = new byte[32];
        Arrays.fill(id, (byte) 0);
        System.arraycopy(hash, 0, id, id.length - hash.length, hash.length);
        return new PubSubID(id);
    }

    public static int getRandomInteger() {
        seed();
        return RANDOM.nextInt();
    }

    public static double getRandomDouble() {
        seed();
        return RANDOM.nextDouble();
    }

    public static byte[] getRandomBytes(int size) {
        seed();
        byte[] bytes = new byte[size];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    private static void seed() {
        long curTime = System.currentTimeMillis();
        if (curTime - prevTime > 0) {
            RANDOM.setSeed(curTime);
        }
        prevTime = curTime;
    }
    
    public static int getPID() {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        pid = pid.substring(0, pid.indexOf("@"));
        return Integer.parseInt(pid);
    }
}
