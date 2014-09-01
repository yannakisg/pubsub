package pubsub;

import static pubsub.util.Util.checkNull;

/**
 *
 * @author John Gasparis
 * @author tsilo
 */
public abstract class BasePubSubPacket extends BaseSerializableStruct {

    protected ContentType cType;
    protected PubSubID scopeId, rendezvousId;
    private Integer hashCode = null;

    public PubSubID getScopeId() {
        return scopeId;
    }

    public void setScopeId(PubSubID scopeId) {
        checkNull(scopeId);
        this.scopeId = scopeId;
    }

    public PubSubID getRendezvousId() {
        return rendezvousId;
    }

    public void setRendezvousId(PubSubID rendezvousId) {
        checkNull(rendezvousId);
        this.rendezvousId = rendezvousId;
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((rendezvousId == null) ? 0 : rendezvousId.hashCode());
            result = prime * result + ((scopeId == null) ? 0 : scopeId.hashCode());
            hashCode = result * prime + cType.byteValue();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BasePubSubPacket other = (BasePubSubPacket) obj;
        if (rendezvousId == null) {
            if (other.rendezvousId != null) {
                return false;
            }
        } else if (!rendezvousId.equals(other.rendezvousId)) {
            return false;
        }
        if (cType != other.getContentType()) {
            return false;
        }

        if (scopeId == null) {
            if (other.scopeId != null) {
                return false;
            }
        } else if (!scopeId.equals(other.scopeId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getScopeId().toString() + "/" + getRendezvousId().toString();
    }

    public ContentType getContentType() {
        return this.cType;
    }

    public void setContentType(ContentType type) {
        this.cType = type;
    }
}
