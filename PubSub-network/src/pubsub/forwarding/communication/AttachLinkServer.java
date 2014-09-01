package pubsub.forwarding.communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pubsub.forwarding.FwdComponent;
import pubsub.util.StoppableThread;
import pubsub.util.Util;

/**
 *
 * @author John Gasparis
 */
public class AttachLinkServer extends StoppableThread {

    private static final Logger logger = Logger.getLogger(AttachLinkServer.class);
    public static final byte CREATE_LINK = 0;
    public static final byte ATTACH_LINK = 1;
    public static final byte CREATE_LINK_EXPLICIT = 2;
    private static int DEF_PORT = 10001;

    public static void configureListeningPort(int port) {
        DEF_PORT = port;
    }
    private ServerSocket serverSocket;
    private final Map<Integer, DatagramSocket> udpSockets = new HashMap<Integer, DatagramSocket>();
    private int linkIndex = 0;
    private final FwdComponent fwd;

    public AttachLinkServer(FwdComponent fwd) throws IOException {
        this.serverSocket = new ServerSocket(DEF_PORT);
        this.fwd = fwd;
        logger.debug("Listening on " + DEF_PORT);
    }

    @Override
    public void run() {
        while (!isShutDown()) {
            try {
                Socket socket = serverSocket.accept();
                Thread th = new Thread(new RequestProccessor(socket, this));
                th.setName(this.getClass().getName() + "/" + socket.getRemoteSocketAddress().toString());
                th.start();
            } catch (IOException e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

    public void createNewLink(Socket socket) throws IOException {
        int port = 0;
        try {
            DatagramSocket udp = new DatagramSocket();
            port = udp.getLocalPort();
            logger.debug("Create new UDP socket, port: " + port);
            this.udpSockets.put(udp.getLocalPort(), udp);
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
            port = 0;
        }

        ByteBuffer buffer = ByteBuffer.allocate(Util.SIZEOF_INT);
        buffer.putInt(port);
        socket.getOutputStream().write(buffer.array());
    }

    public void createNewLinkExplicit(Socket socket) throws IOException {
        int port = 0;
        try {
            DataInputStream dain = new DataInputStream(socket.getInputStream());
            port = dain.readInt();
            logger.debug("Create new UDP socket, port: " + port);
            DatagramSocket udp = new DatagramSocket(port);
            this.udpSockets.put(udp.getLocalPort(), udp);
        } catch (SocketException e) {
            port = 0;
        }

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(port);
        socket.getOutputStream().write(buffer.array());
    }

    public void attachLink(Socket socket) throws IOException {
        logger.debug("Received attach link message");
        byte[] buffer = new byte[AttachLinkMessage.getLength()];
        AttachLinkMessage msg;
        logger.debug("reading from socket");
        socket.getInputStream().read(buffer);

        msg = AttachLinkMessage.createNew();
        logger.debug("parsing message");
        msg.fromBytes(buffer);

        DatagramSocket datagramSocket = this.udpSockets.get(msg.getLocalPort());
        InetAddress addr = InetAddress.getByAddress(msg.getAddress());

        int remotePort = msg.getRemotePort();
        int next = linkIndex++;
        String linkName = "Link-" + next;

        logger.debug("creating new UDP link - weight[" + msg.getWeight() + "]");
        PointToPointUDPLink communicationLink = new PointToPointUDPLink(datagramSocket, addr, remotePort, linkName);
        communicationLink.operate();
        this.fwd.attach(communicationLink, msg.getWeight());

        logger.debug("respond back");
        socket.getOutputStream().write(0);
    }
}
