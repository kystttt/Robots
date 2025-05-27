package log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Класс CircularBuffer представляет собой циклический (кольцевой) буфер фиксированного размера,
 * который позволяет эффективно хранить ограниченное количество элементов с заменой самых старых при переполнении.
 * @param <T> тип элементов, хранящихся в буфере
 */
public class CircularBuffer<T> implements Iterable<T> {
    private final Object[] buffer;
    private int start = 0;
    private int end = 0;
    private int size = 0;
    private final int capacity;

    /**
     * Создает новый циклический буфер заданной емкости.
     * @param capacity максимальное количество элементов, которое буфер может содержать
     */
    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    /**
     * Добавляет элемент в буфер.
     * Если буфер не заполнен, элемент добавляется в конец.
     * Если буфер заполнен, самый старый элемент перезаписывается новым.
     * @param item элемент для добавления
     */
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

    /**
     * Возвращает элемент по индексу, считая от самого старого (start).
     *
     * @param index индекс элемента (от 0 до size-1)
     * @return элемент по заданному индексу
     * @throws IndexOutOfBoundsException если индекс вне допустимого диапазона
     */
    public synchronized T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (T) buffer[(start + index) % capacity];
    }

    /**
     * Возвращает текущее количество элементов в буфере.
     * @return количество элементов в буфере
     */
    public synchronized int size() {
        return size;
    }

    /**
     * Возвращает подмножество элементов буфера в виде Iterable,
     * начиная с индекса startFrom и максимум count элементов.
     * @param startFrom индекс первого элемента для выборки (от 0 до size-1)
     * @param count максимальное количество элементов для выборки
     * @return Iterable с элементами, или пустой список, если startFrom вне диапазона
     */
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

    /**
     * Возвращает итератор для обхода всех элементов буфера от самого старого к новому.
     * @return итератор по элементам буфера
     */
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
