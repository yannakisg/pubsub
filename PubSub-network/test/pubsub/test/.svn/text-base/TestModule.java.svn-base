package pubsub.test;

import java.io.IOException;

import pubsub.localrendezvous.LocRCFactory;
import pubsub.localrendezvous.LocalRComponent;
import pubsub.module.PubSubModule;
import pubsub.module.PubSubModuleManager;
import pubsub.module.ScopeRendezvousIdTable;
import pubsub.module.SimplePubSubModuleImpl;

public class TestModule {
	public static void main(String[] args) throws IOException {
		 PubSubModule module = new SimplePubSubModuleImpl(new ScopeRendezvousIdTable());
	        PubSubModuleManager.setModule(module);
	        LocalRComponent locRC = LocRCFactory.createLocalRendezvousComponent();
	        locRC.startAll();
	}
	
}
