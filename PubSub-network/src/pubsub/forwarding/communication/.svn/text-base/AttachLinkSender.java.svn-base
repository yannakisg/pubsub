package pubsub.forwarding.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

/**
 * 
 * @author John Gasparis
 */
public class AttachLinkSender {

    private Socket socket;
    private DataInputStream dain;
    private DataOutputStream daout;
    private boolean closed = false;

    public AttachLinkSender(String host, int port) throws UnknownHostException,
            IOException {
        System.out.printf("connecting to %s, %d\n", host, port);
        this.socket = SocketFactory.getDefault().createSocket(host, port);
        this.socket.setTcpNoDelay(true);
        dain = new DataInputStream(socket.getInputStream());
        daout = new DataOutputStream(socket.getOutputStream());
    }

    public int createNewLink() throws IOException {
        daout.writeByte(AttachLinkServer.CREATE_LINK);
        return dain.readInt();
    }

    public int createNewLinkExplicit(int linkPort) throws IOException {
        daout.writeByte(AttachLinkServer.CREATE_LINK_EXPLICIT);
        daout.writeInt(linkPort);
        return dain.readInt();
    }

    public boolean attach(int localPort, int remotePort, byte[] remoteAddress, double weight) throws IOException {
        AttachLinkMessage msg = new AttachLinkMessage(localPort, remotePort, remoteAddress, weight, false);
        OutputStream out = socket.getOutputStream();
        System.out.println("sending attach link message to " + socket.getRemoteSocketAddress());
        out.write(AttachLinkServer.ATTACH_LINK);
        out.write(msg.toBytes());
        System.out.println("reading return result");
        int res = socket.getInputStream().read();

        System.out.printf("result: %d\n", res);
        return res == 0;
    }

    public void close() throws IOException {
        if (!closed) {
            closed = true;
            this.socket.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
