package pubsub.localrendezvous;

public interface LocRCManager {

	LocRCClient createNewClient(String host, int port, String name);

	LocRCClient createNewClient(String name);

}
