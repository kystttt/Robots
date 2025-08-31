package log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Кольцевой буфер дальше допишу
 *
 * @param <T> Тип содержимого буфера
 */
public class CircularBuffer<T> implements Iterable<T> {
    private final int capacity;
    private T[] elements;
    private int start = 0;
    private int end = 0;
    private int size = 0;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Создает буффер заданной вместимости
     *
     * @param capacity емкость буфера
     */
    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        elements = (T[]) new Object[capacity];
    }


    /**
     * Добавляет элемент в буффер, перезаписывает более старые данные в случае, если размер буфера превышает вместимость
     */
    public void add(T element) {
        lock.writeLock().lock();
        try {
            if (size < capacity) {
                elements[end] = element;
                size++;
                end = (end + 1) % capacity;
            } else {
                elements[end] = element;
                end = (end + 1) % capacity;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Возвращает массив с со значениями из буфера в заданном диапазоне
     */
    public List<T> getRange(int startIdx, int endIdx){
        lock.readLock().lock();
        try{
            if (endIdx < startIdx || startIdx < 0 || endIdx > size){
                throw new IndexOutOfBoundsException("Invalid range");
            }
            List<T> list = new ArrayList<T>(endIdx - startIdx);
            for (int i = startIdx; i < endIdx; i++) {
                int index = (start + i) % capacity;
                list.add(elements[index]);
            }
            return list;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return возвращает размер буфера
     */
    int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                int idxNow = (start + idx++) % capacity;
                return elements[idxNow];
            }
        };
    }
}

