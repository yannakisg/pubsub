package pubsub.util;

/**
 *
 * @author tsilo
 */
public class Pair<X, Y> {

    private final X first;
    private final Y second;

    public Pair(X first, Y second) {
        Util.checkNull(first);
        Util.checkNull(second);

        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<X, Y> other = (Pair<X, Y>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 37 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }

    public X getFirst() {
        return first;
    }

    public Y getSecond() {
        return second;
    }
}
