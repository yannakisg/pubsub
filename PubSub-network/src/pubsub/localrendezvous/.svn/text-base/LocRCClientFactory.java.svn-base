package pubsub.localrendezvous;

import pubsub.localrendezvous.TimeOutLocRCClient.Mode;
import pubsub.localrendezvous.nio.TCPLocRCManager;

/**
 *
 * @author tsilo
 * @author John Gasparis
 */
public class LocRCClientFactory {		
	private static LocRCManager manager = new TCPLocRCManager();
	
	public static void configureManager(LocRCManager mng){
		manager = mng;
	}       

    public static LocRCClient createNewClient(String host, int port, String name) {
    	return manager.createNewClient(host, port, name);        
    }

    public static LocRCClient createNewClient(String name) {
        return manager.createNewClient(name);                
    }

    public static TimeOutLocRCClient createTimeOutClient(String name) {
        return wrapMode(manager.createNewClient(name), Mode.DEEP);
    }

    public static TimeOutLocRCClient createTimeOutClient() {
        return createTimeOutClient("");
    }

    public static TimeOutLocRCClient createTimeOutClient(String host, int port, String name) {
        return wrapMode(manager.createNewClient(host, port, name), Mode.DEEP);
    }

    public static TimeOutLocRCClient wrap(LocRCClient locRCClient) {
        return wrapMode(locRCClient, Mode.SHALLOW);
    }

    private static TimeOutLocRCClient wrapMode(LocRCClient locRCClient, Mode m) {
        return new TimeOutLocRCClient(locRCClient, m);
    }
}
