package pubsub.util;

import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author John Gasparis
 */
public class ThreadSafeSortedList<E> {

    private final SortedList<E> sortedList;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
    private final Lock readLock = rwl.readLock();
    private final Lock writeLock = rwl.writeLock();

    public ThreadSafeSortedList(Comparator<E> comparator) {
        sortedList = new SortedList<E>(comparator);
    }

    public boolean add(E element) {
        writeLock.lock();
        try {
            return sortedList.add(element);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean addSole(E element) {
        writeLock.lock();
        try {
            if (containsElement(element)) {
                return false;
            } else {
                return sortedList.add(element);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public boolean containsElement(E element) {
        readLock.lock();
        try {
            return sortedList.containsElement(element);
        } finally {
            readLock.unlock();
        }
    }

    public int getIndex(E element) {
        readLock.lock();
        try {
            return sortedList.getIndex(element);
        } finally {
            readLock.lock();
        }
    }

    public E get(int index) {
        readLock.lock();
        try {
            return sortedList.get(index);
        } finally {
            readLock.unlock();
        }
    }

    public void remove(E element) {
        writeLock.lock();
        try {
            int index = sortedList.getIndex(element);
            if (index >= 0 && index < sortedList.size()) {
                sortedList.remove(index);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void remove(int index) {
        writeLock.lock();
        try {
            sortedList.remove(index);
        } finally {
            writeLock.unlock();
        }
    }

    public int getSize() {
        readLock.lock();
        try {
            return sortedList.size();
        } finally {
            readLock.unlock();
        }
    }

    public boolean isEmpty() {
        readLock.lock();
        try {
            return sortedList.isEmpty();
        } finally {
            readLock.unlock();
        }
    }
}
