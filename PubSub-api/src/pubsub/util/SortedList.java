package pubsub.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author John Gasparis
 */
public class SortedList<E> extends ArrayList<E> {

    private static final long serialVersionUID = -2437569920894405007L;
    private Comparator<E> comparator;

    public SortedList(Comparator<E> comparator) {
        super();
        this.comparator = comparator;
    }

    public SortedList(int initialCapacity, Comparator<E> comparator) {
        super(initialCapacity);
        this.comparator = comparator;
    }

    @Override
    public boolean add(E element) {
        if (isEmpty()) {
            super.add(0, element);
        } else {
            int index = Collections.binarySearch(this, element, comparator);
            if (index < 0) {
                index = ~index;
            }
            super.add(index, element);
        }

        return true;
    }

    public void removeElement(E element) {
        int index = getIndex(element);
        if (index >= 0 && index < size()) {
            remove(index);
        }
    }

    public boolean containsElement(E element) {
        if (isEmpty()) {
            return false;
        } else {
            int index = Collections.binarySearch(this, element, comparator);
            if (index < 0) {
                return false;
            }
        }
        return true;
    }

    public int getIndex(E element) {
        if (isEmpty()) {
            return -1;
        } else {
            return Collections.binarySearch(this, element, comparator);
        }
    }
}
