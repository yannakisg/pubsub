package pubsub.store;

import pubsub.ByteIdentifier;
import pubsub.ForwardIdentifier;
import pubsub.messages.RVAAnnouncement;
import pubsub.messages.RVAAnnouncement.RVPAction;

/**
 *
 * @author John Gasparis
 */
public class NodeEntry {

    private static NodeEntry instance;
    private ByteIdentifier procID;
    private ByteIdentifier gwID;
    private ByteIdentifier hostID;
    private ForwardIdentifier gwToHost;
    private ForwardIdentifier hostToGW;
    private RVAAnnouncement.RVPAction rvpAction;
    private long lifeTime;

    public NodeEntry(ByteIdentifier procID, RVAAnnouncement.RVPAction rvpAction, ByteIdentifier gwID, ByteIdentifier hostID, ForwardIdentifier gwToHost, ForwardIdentifier hostToGW, long lifeTime) {
        this.procID = procID;
        this.rvpAction = rvpAction;
        this.gwID = gwID;
        this.hostID = hostID;
        this.gwToHost = gwToHost;
        this.lifeTime = lifeTime;
        this.hostToGW = hostToGW;
    }
    
    public ByteIdentifier getProcID() {
        return this.procID;
    }

    public RVPAction getRvpAction() {
        return rvpAction;
    }

    public void setRvpAction(RVPAction rvpAction) {
        this.rvpAction = rvpAction;
    }       

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public ByteIdentifier getGwID() {
        return gwID;
    }

    public void setGwID(ByteIdentifier gwID) {
        this.gwID = gwID;
    }

    public ForwardIdentifier getGwToHost() {
        return gwToHost;
    }

    public void setGwToHost(ForwardIdentifier gwToHost) {
        this.gwToHost = gwToHost;
    }

    public ByteIdentifier getHostID() {
        return hostID;
    }

    public void setHostID(ByteIdentifier hostID) {
        this.hostID = hostID;
    }

    public ForwardIdentifier getHostToGW() {
        return hostToGW;
    }

    public void setHostToGW(ForwardIdentifier hostToGW) {
        this.hostToGW = hostToGW;
    }

    public static NodeEntry getInstance(ByteIdentifier gwID, ByteIdentifier hostID) {
        if (instance == null) {
            instance = new NodeEntry(null, RVAAnnouncement.RVPAction.DEFAULT, gwID, hostID, null, null, -1);
        } else {
            instance.gwID = gwID;
            instance.hostID = hostID;
        }

        return instance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeEntry other = (NodeEntry) obj;
        if (this.gwID != other.gwID && (this.gwID == null || !this.gwID.equals(other.gwID))) {
            return false;
        }
        if (this.hostID != other.hostID && (this.hostID == null || !this.hostID.equals(other.hostID))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.hostID != null ? this.hostID.hashCode() : 0);
        return hash;
    }
}
