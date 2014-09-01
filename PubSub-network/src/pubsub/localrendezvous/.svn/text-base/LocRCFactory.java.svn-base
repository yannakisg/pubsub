package pubsub.localrendezvous;

import java.io.IOException;

import pubsub.localrendezvous.nio.NIOLocalRendezvousComponent;
import pubsub.localrendezvous.uds.UDSLocalRendezvousComponent;

/**
 * 
 * @author John Gasparis
 */
public class LocRCFactory {

    public enum LocRCType {

        NIO,
        UDS
    }
    public static LocRCType TYPE = LocRCType.UDS;
    public static String FILENAME = "/tmp/pubsub.sock";
    public static String HOST = "localhost";
    public static int PORT = 10000;

    public static void configureHostAddr(String hostname) {
        HOST = hostname;
    }

    public static void configureHostPort(int port) {
        PORT = port;
    }

    public static void configureLocRCType(LocRCType type) {
        TYPE = type;
    }

    public static void configureFileName(String fileName) {
        FILENAME = fileName;
    }

    public static LocalRComponent createLocalRendezvousComponent() throws IOException {
        if (TYPE == LocRCType.UDS) {
            return new UDSLocalRendezvousComponent(FILENAME);
        } else {
            return new NIOLocalRendezvousComponent(HOST, PORT);
        }
    }
}
