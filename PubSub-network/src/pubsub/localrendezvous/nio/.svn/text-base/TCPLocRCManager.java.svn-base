package pubsub.localrendezvous.nio;

import java.io.IOException;

import org.apache.log4j.Logger;

import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCFactory;
import pubsub.localrendezvous.LocRCManager;
import pubsub.localrendezvous.uds.UDSLocRCClientImpl;

public class TCPLocRCManager implements LocRCManager {

    private static final Logger logger = Logger.getLogger(TCPLocRCManager.class);

    @Override
    public LocRCClient createNewClient(String host, int port, String name) {
        try {

            if (LocRCFactory.TYPE == LocRCFactory.LocRCType.UDS) {
                return new UDSLocRCClientImpl(LocRCFactory.FILENAME, name);
            } else {
                return new NIOLocRCClientImpl(host, port, name);
            }
        } catch (IOException e) {
            logger.trace(e, e);
            throw new RuntimeException("cannot create LocRC client", e);
        }
    }

    @Override
    public LocRCClient createNewClient(String name) {
        return createNewClient(LocRCFactory.HOST, LocRCFactory.PORT, name);
    }
}
