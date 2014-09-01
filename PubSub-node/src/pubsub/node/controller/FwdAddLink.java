package pubsub.node.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import pubsub.forwarding.communication.AttachLinkSender;

/**
 * 
 * @author tsilo
 * @author John Gasparis
 */
public class FwdAddLink {

	private final String host1, host2;
	private final int port1, port2;
	private final InetAddress addr1;
	private final InetAddress addr2;
        private final double weight;

	public FwdAddLink(String host1, int port1, String host2, int port2, double weight)
			throws UnknownHostException {
		this.host1 = host1;
		this.port1 = port1;

		this.host2 = host2;
		this.port2 = port2;

		this.addr1 = InetAddress.getByName(host1);
		this.addr2 = InetAddress.getByName(host2);
                
                this.weight = weight;
	}

	public void connect() throws UnknownHostException, IOException {
		AttachLinkSender sender1 = new AttachLinkSender(host1, port1);
		AttachLinkSender sender2 = new AttachLinkSender(host2, port2);

		int link1 = sender1.createNewLink();
		System.out.printf("link1 port: %d\n", link1);

		int link2 = sender2.createNewLink();
		System.out.printf("link2 port: %d\n", link2);

		sender1.attach(link1, link2, addr2.getAddress(), weight);
		sender2.attach(link2, link1, addr1.getAddress(), weight);
	}
	
	public void connectExplicit(int linkPort1, int linkPort2) throws UnknownHostException, IOException{
		AttachLinkSender sender1 = new AttachLinkSender(host1, port1);
		AttachLinkSender sender2 = new AttachLinkSender(host2, port2);
		
		int link1port = sender1.createNewLinkExplicit(linkPort1);
		System.out.printf("link1 port: %d\n", link1port);

		int link2port = sender2.createNewLinkExplicit(linkPort2);
		System.out.printf("link2 port: %d\n", link2port);

		sender1.attach(link1port, link2port, addr2.getAddress(), weight);
		sender2.attach(link2port, link1port, addr1.getAddress(), weight);
	}

	public static void connect(String host1, int port1, String host2, int port2, double weight)
			throws IOException {
		FwdAddLink fwdAddLink = new FwdAddLink(host1, port1, host2, port2, weight);
		fwdAddLink.connect();
	}

	public static void main(String[] args) throws IOException {
		int attachPort = 10001;

		String router1 = "192.168.1.103";//"131.130.69.162";
		String router2 = "192.168.1.102";
		String router3 = "192.168.1.106";
                
                String host2 = "192.168.1.100";
                String host3 = "192.168.1.101";

		String rvp = "192.168.1.104";

		FwdAddLink addLink = null;
		
		// router1 - router2
		System.out.println("router1 - router2");
		//addLink = new FwdAddLink(router1, attachPort, router2, attachPort);
		addLink.connectExplicit(65000, 65000);
		goOn();
                
		// router1 - router3
		System.out.println("router1 - router3");
		//addLink = new FwdAddLink(router1, attachPort, router3, attachPort);
		addLink.connectExplicit(65001, 65001);
		goOn();
		
		// host2 - router2
		System.out.println("host2 - router2");
		//addLink = new FwdAddLink(router2, attachPort, host2, attachPort);
		addLink.connectExplicit(65002, 65002);
		goOn();
		
		// host3 - router3
		System.out.println("host3 - router3");
		//addLink = new FwdAddLink(router3, attachPort, host3, attachPort);
		addLink.connectExplicit(65003, 65003);
		goOn();
		
		// router1 - rvp
		System.out.println("router1 - rvp");
		//addLink = new FwdAddLink(router1, attachPort, rvp, attachPort);
		addLink.connectExplicit(65004, 65004);
		goOn();

	}

	private static void goOn() throws IOException {
		System.out.println("Press enter to continue");
		System.in.read();

	}
}
