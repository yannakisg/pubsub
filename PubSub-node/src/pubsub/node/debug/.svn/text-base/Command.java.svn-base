package pubsub.node.debug;

import javax.swing.SwingUtilities;
import pubsub.Publication;
import pubsub.Subscription;
import pubsub.localrendezvous.LocRCClient;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.messages.MessageType;
import pubsub.tmc.TMCUtil;
import pubsub.messages.ipc.DebugTopologyMessage;
import pubsub.messages.ipc.IPCTMCInterestMessage;

/**
 *
 * @author John Gasparis
 */
public class Command {

    protected static final String SHOW_TOPOLOGY_S = "show topology -s";
    protected static final String SHOW_TOPOLOGY_G = "show topology -g";
    protected static final String SHOW_HOSTS_G = "show hosts -g";
    protected static final String SHOW_HOSTS_S = "show hosts -s";
    protected static final String CLEAR = "clear";
    protected static final String HELP = "help";
    protected static final String QUIT = "exit";
    private LocRCClient locRCClient;
    private Subscription sub;
    private IPCTMCInterestMessage interestDT;
    private IPCTMCInterestMessage interestDH;

    public Command() {
        sub = Subscription.createSubToMutableData(TMCUtil.TMC_SID, TMCUtil.TMC_DEBUG_RID);
        interestDT = new IPCTMCInterestMessage(MessageType.Type.DEBUG_TOPOLOGY);
        interestDH = new IPCTMCInterestMessage(MessageType.Type.GET_HOSTS);
    }

    public void showRemoteTopologyG(String host, int port) {
        LocRCClient client = LocRCClientFactory.createNewClient(host, port, "Command");

        client.subscribe(sub);
        interestDT.publishMutableData(locRCClient, interestDT.toBytes());

        try {
            final Publication pub1 = client.receiveNext();

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    NodeViewer viewer = new NodeViewer(pub1.getDataArray());
                }
            });
            client.close();
        } catch (Exception ex) {
        }
    }

    public void showTopologyG() {
        showG(MessageType.Type.DEBUG_TOPOLOGY);
    }

    public String showTopologyS() {
        return showS(MessageType.Type.DEBUG_TOPOLOGY);
    }

    public void showHostsG() {
        showG(MessageType.Type.GET_HOSTS);
    }

    public String showHostsS() {
        return showS(MessageType.Type.GET_HOSTS);
    }

    private String showS(final MessageType.Type type) {
        locRCClient = LocRCClientFactory.createNewClient("");

        locRCClient.subscribe(sub);

        if (type == MessageType.Type.GET_HOSTS) {
            interestDH.publishMutableData(locRCClient, interestDH.toBytes());
        } else {
            interestDT.publishMutableData(locRCClient, interestDT.toBytes());
        }

        
        try {
            final Publication pub1 = locRCClient.receiveNext();

            DebugTopologyMessage message = DebugTopologyMessage.createNew(pub1.getDataArray());
            return message.toString();

        } catch (InterruptedException ex) {
            closeLocRCClient();
            return null;
        }

    }

    private void showG(final MessageType.Type type) {
        locRCClient = LocRCClientFactory.createNewClient("");
        locRCClient.subscribe(sub);

        if (type == MessageType.Type.GET_HOSTS) {
            interestDH.publishMutableData(locRCClient, interestDH.toBytes());
        } else {
            interestDT.publishMutableData(locRCClient, interestDT.toBytes());
        }

        try {
            final Publication pub1 = locRCClient.receiveNext();

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    NodeViewer viewer = new NodeViewer(pub1.getDataArray());
                }
            });
        } catch (InterruptedException ex) {
        }

        closeLocRCClient();
    }

    public void closeLocRCClient() {
        try {
            if (locRCClient != null) {
                this.locRCClient.close();
            }
        } catch (Exception ex) {
        }
    }
}
