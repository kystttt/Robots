package log;

import java.util.ArrayList;
import java.util.Iterator;

public class CircularBuffer<T> implements Iterable<T> {
    private final Object[] buffer;
    private int start = 0;
    private int end = 0;
    private int size = 0;
    private final int capacity;

    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public synchronized void add(T item) {
        if (size < capacity) {
            buffer[end] = item;
            end = (end + 1) % capacity;
            size++;
        } else {
            buffer[end] = item;
            start = (start + 1) % capacity;
            end = (end + 1) % capacity;
        }
    }

    public synchronized T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (T) buffer[(start + index) % capacity];
    }

    public synchronized int size() {
        return size;
    }

    public synchronized Iterable<T> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= size) {
            return new ArrayList<>();
        }
        ArrayList<T> entries = new ArrayList<>();
        for (int i = startFrom; i < startFrom + count && i < size; i++) {
            entries.add(get(i));
        }
        return entries;
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return new Iterator<T>() {
            private int current = start;
            private int count = 0;

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public T next() {
                T item = (T) buffer[current];
                current = (current + 1) % capacity;
                count++;
                return item;
            }
        };
    }
}