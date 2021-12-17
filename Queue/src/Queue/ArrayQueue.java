package cse214hw1;

import java.util.NoSuchElementException;

public class ArrayQueue<T> implements Queue<T> {
    private int head;
    private int tail;
    private T[] array;
    private int capacity;
    private int size;

    public ArrayQueue() {
        this.array = (T[]) new Object[5];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
        this.capacity = 5;
    }

    @Override
    public void add(T t) {
        if (size == 0){
            array[tail] = t;
            size++;
        }
        else {
            if (size == capacity){
                reSize();
            }
            tail += 1;
            array[(tail) % capacity] = t;
            size++;
        }
    }

    @Override
    public T remove() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        T hold = array[head];
        array[head] = null;
        head = head + 1 % array.length;
        size--;
        return hold;
    }

    @Override
    public T peek() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        T hold = array[head];
        return hold;
    }

    private void reSize() {
        int newSize = array.length * 2;
        T[] temp = (T[]) new Object[newSize];
        for (int i = 0; i < array.length; i++) {
            temp[i] = array[(head + i) % array.length];
        }
        array = temp;
        head = 0;
        tail = ((array.length) / 2) - 1;
        capacity = newSize;
    }
}
