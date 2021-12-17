package cse214hw1;


import java.util.NoSuchElementException;

public class ArrayDeque<T> implements Deque<T>{
    private T[] array;
    private int capacity;
    private int size;
    private int head;
    private int tail;

    public ArrayDeque(){
        this.array = (T[]) new Object [5];
    }
    public ArrayDeque(int capacity){
        this.capacity = capacity;
        this.array = (T[]) new Object [capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    @Override
    public void addFirst(T t) {
        if (size == 0){
            array[head] = t;
            size++;
        }
        else {
            if (size == capacity){
                reSize();
            }
            head -= 1;
            if (head < 0) {
                head = array.length - 1;
            }
            array[head] = t;
            size++;
        }

    }
    @Override
    public void addLast(T t) {
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
    public T removeFirst()  {
       if (size == 0){
           throw new NoSuchElementException("Deque is empty");
        }
        T hold = array[head];
        array[head] = null;
        head = head+1 % array.length;
        size--;
        return hold;
    }

    @Override
    public T removeLast() {
        if (size == 0){
            throw new NoSuchElementException("Deque is empty");
        }
        T hold = array[tail];
        array[tail] = null;
        tail = tail-1 ;
        if (tail < 0) {
            tail = array.length - 1;
        }
        size--;
        return hold;
    }

    private void reSize(){
        int newSize = array.length * 2;
        T[] temp= (T[]) new Object[newSize];
        for (int i = 0; i < array.length ; i++  ){
            temp[i] = array[(head+i) % array.length];
        }
        array = temp;
        head = 0;
        tail = ((array.length )/2) - 1;
        capacity = newSize;
    }
    public static <T> ArrayDeque<T> of(T a,T b,T c){
        ArrayDeque<T> temp= new ArrayDeque(5);
        temp.addFirst(a);
        temp.addLast(b);
        temp.addLast(c);
        return temp;
    }

}