package pubsub.tmc;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import org.apache.log4j.Logger;
import pubsub.ByteIdentifier;
import pubsub.PubSubID;
import pubsub.invariants.WellKnownIds;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class TMCUtil {

    public static byte SHA1_LENGTH = 20;
    public static final ByteIdentifier UNKNOWN_ID = new ByteIdentifier(getUnknownNodeID());
    private static Logger logger = Logger.getLogger(TMCUtil.class);
    public static final PubSubID TMC_SID = PubSubID.fromHexString(WellKnownIds.TMC.TMC_SID);
    public static final PubSubID TMC_RID = PubSubID.fromHexString(WellKnownIds.TMC.TMC_RID);
    public static final PubSubID TMC_LOCAL_UTIL_RID = PubSubID.fromHexString(WellKnownIds.TMC.TMC_LOCAL_UTIL_RID);
    public static final PubSubID TMC_DEBUG_RID = PubSubID.fromHexString(WellKnownIds.TMC.TMC_DEBUG_RID);
    private static byte[] hashedMac = null;

    private static byte[] getNetworkInterface() {

        NetworkInterface iface;
        Enumeration<NetworkInterface> enumFace;

        try {
            iface = NetworkInterface.getByName("eth0");

            if (iface != null && iface.getHardwareAddress() != null) {
                return iface.getHardwareAddress();
            }

            enumFace = NetworkInterface.getNetworkInterfaces();
            while (enumFace.hasMoreElements()) {
                iface = enumFace.nextElement();

                if (iface != null && !iface.isLoopback() && iface.getHardwareAddress() != null) {
                    byte[] hardwareAddress = iface.getHardwareAddress();
                    return hardwareAddress;
                }
            }
        } catch (SocketException sexc) {
            return null;
        }
        return null;

    }

    private static byte[] hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md;

        md = MessageDigest.getInstance("SHA-1");

        md.update(data);
        return md.digest();
    }

    public static byte[] getNodeID() {
        byte[] mac;

        if (hashedMac == null) {
            try {
                mac = getNetworkInterface();

                hashedMac = hash(mac);

                debugByteArray(logger, hashedMac);
                debugByteArray(logger, UNKNOWN_ID.getId());
            } catch (NoSuchAlgorithmException ex) {

                return null;
            }
        }

        return hashedMac;
    }

    public static byte[] getRandomNodeID() {
        byte[] rBytes = Util.getRandomBytes(16);
        try {
            byte[] id = hash(rBytes);

            return id;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    public static byte[] getUnknownNodeID() {
        String str = "UNKNOWN";
        byte[] id;

        try {
            id = hash(str.getBytes());

            return id;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    public static void debugByteArray(Logger logger, byte[] array) {
        debugByteArray(logger, "", array);
    }

    public static void debugByteArray(Logger logger, String msg, byte[] array) {
        String str = msg + byteArrayToString(array);

        logger.debug(str);
    }

    public static String byteArrayToString(byte[] array) {
        String str = "";
        for (byte b : array) {
            str = str + String.format("%02X", b);
        }

        return str;
    }
}
