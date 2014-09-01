package pubsub.localrendezvous.singlejvm;

import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCManager;

public class LocalRCManager implements LocRCManager {

	@Override
	public LocRCClient createNewClient(String host, int port, String name) {
		return new LocalLocRCClient(name);
	}

	@Override
	public LocRCClient createNewClient(String name) {
		return new LocalLocRCClient(name);
	}
}
