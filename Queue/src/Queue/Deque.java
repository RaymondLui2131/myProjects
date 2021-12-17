package cse214hw1;

public interface Deque<T> {
    void addFirst(T t);
    void addLast(T t);
    T removeFirst();
    T removeLast();
}
