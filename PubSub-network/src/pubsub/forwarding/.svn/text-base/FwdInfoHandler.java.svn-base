package pubsub.forwarding;

import java.nio.ByteBuffer;
import java.util.Arrays;

import pubsub.RequestHandler;

/**
 *
 * @author tsilo
 */
public class FwdInfoHandler implements RequestHandler {

    public static final byte[] REQUEST_VLID = {0};
    public static final byte[] REQUEST_MTU = {1};
    private final byte[] EMPTY_BUFFER = new byte[0];
    private final FwdComponent fwdComponent;
    private byte[] mtuBytes = null;

    public FwdInfoHandler(FwdComponent fwdComponent) {
        this.fwdComponent = fwdComponent;
    }

    @Override
    public byte[] handleRequest(byte[] requestData) {
        if (requestData == null || requestData.length == 0) {
            return EMPTY_BUFFER;
        } else if (Arrays.equals(requestData, REQUEST_VLID)) {
            return this.fwdComponent.getVLID().getBytes();
        } else if (Arrays.equals(requestData, REQUEST_MTU)) {
            return MTUtoBytes();
        }

        return EMPTY_BUFFER;
    }

    private byte[] MTUtoBytes() {
        if (mtuBytes == null) {
            int m = this.fwdComponent.getMTU();
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(m);
            mtuBytes = b.array();
        }
        return mtuBytes;
    }
}
