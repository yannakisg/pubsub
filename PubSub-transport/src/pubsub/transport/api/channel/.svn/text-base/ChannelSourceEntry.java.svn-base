package pubsub.transport.api.channel;

/**
 *
 * @author John Gasparis
 */
public class ChannelSourceEntry {

    protected static enum Status {

        PENDING_INSTRUCT,
        RECEIVED
    }
    private Status status = Status.PENDING_INSTRUCT;
    private int id = -1;

    public void setID(int id) {
        this.status = Status.RECEIVED;
        this.id = id;
    }

    public boolean received() {
        return status == Status.RECEIVED;
    }

    public int getID() {
        return this.id;
    }
}
