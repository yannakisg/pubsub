package pubsub.tmc.topology.steinertree;

import java.nio.ByteBuffer;
import java.util.Arrays;
import pubsub.ByteIdentifier;

/**
 *
 * @author John Gasparis
 */
public class Edge {

    private double weight;
    private ByteIdentifier source;
    private ByteIdentifier target;
    private String toString = "";

    public Edge(ByteIdentifier source, ByteIdentifier target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    @Override
    public String toString() {
        toString = source.toString() + " -- " + target.toString() + " [" + weight + "]";
        return toString;
    }

    public void swapEndpoints() {
        ByteIdentifier temp = source;
        source = target;
        target = temp;
    }
    

    public ByteIdentifier getSource() {
        return this.source;
    }

    public ByteIdentifier getTarget() {
        return this.target;
    }

    public double getWeight() {
        return this.weight;
    }

    public void debug() {
        System.out.println("\t" + toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
            return false;
        }
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        if (this.target != other.target && (this.target == null || !this.target.equals(other.target))) {
            return false;
        }
        if (this.source != other.target && (this.source == null || !this.source.equals(other.target))) {
            return false;
        }
        if (this.target != other.source && (this.target == null || !this.target.equals(other.source))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
        ByteBuffer buffer = ByteBuffer.allocate(source.getId().length + target.getId().length);
        buffer.put(source.getId()).put(target.getId());
        
        hash = 79 * hash + Arrays.hashCode(buffer.array());
        return hash;
    }
}
