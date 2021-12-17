package hw3.datastructures;

import hw3.products.Laptop;
import java.util.LinkedList;

/**
 * This class implements the {@link Set} interface. It offers constant time performance (on average) for the basic
 * operations <code>add</code>, <code>remove</code>, <code>containt</code>, and <code>size</code>, under the simple
 * uniform hashing assumption (i.e., the hash function distributes elements uniformly across the slots in the backing
 * table).
 * There are two constructors given to you. You can modify them, or add new constructors. However, the signature of
 * these two constructors must not be changed. That is, the user must be able to create an instance of this class by
 * invoking <code>new ChainedHashSet()</code> and <code>new ChainedHashSet(int k)</code>.
 *
 * @author Raymond Lui
 * @param <E> the type of elements stored in this set
 */
public class ChainedHashSet<E> implements Set<E> {

    /**
     * Once an instance is created, this table size cannot change
     */
    private final int tablesize;
    private int size;
    private LinkedList<Laptop>[] lap;

    // DO NOT MODIFY THIS METHOD
    public final int tablesize() { return this.tablesize; }

    // DO NOT MODIFY THIS METHOD
    public final double loadfactor() { return size() / (double) tablesize; }

    /**
     * Constructs a hashtable with a default size of 10 and has a linklist in each index
     */
    public ChainedHashSet(){
        this(10);
        size = 0;
        lap = new LinkedList[tablesize];
        for (int i = 0 ; i < tablesize ; i++){
            lap[i] = new LinkedList<Laptop>();
        }
    }

    /**
     * Constructs a hashtable with the tablesize of a given tablesize and has a linklist in each index
     * @param tablesize the tablesize for the hashtable
     */
    public ChainedHashSet(int tablesize) {
        this.tablesize = tablesize;
        size = 0;
        lap = new LinkedList[tablesize];
        for (int i = 0 ; i < tablesize ; i++){
            lap[i] = new LinkedList<Laptop>();
        }
    }

    /**
     * This method returns the size of the hashtable
     * @return the amount of elements in the hashtable
     */
    @Override public int size() {
        return size;
    }

    /**
     * This method checks if the hashtable is empty
     * @return true if the hashtable is empty and false if the hashtable isn't empty
     */
    @Override public boolean isEmpty() {
        if (size == 0)
            return true;
        return false;
    }

    /**
     * This method checks if the hashtable contains a given element
     * @param element the element whose presence in this hashtable is to be tested
     * @return true if the hashtable contains the element and false if the hashtable doesn't contain the element
     * @throws NullPointerException if element is equal to null
     */
    @Override public boolean contains(E element) {
        if (element == null)
            throw new NullPointerException();
        int hashFunction = element.hashCode() % tablesize;
            for (int i = 0; i < lap[Math.abs(hashFunction)].size() ; i++){
                if (((Laptop)lap[Math.abs(hashFunction)].get(i)).equals((Laptop)element))
                    return true;
            }
        return false;
    }

    /**
     * This method adds an element into the hashtable
     * @param e the element to be added to this hashtable
     * @return true if successfully added to the hashtable and false if it isn't successfully added to the hashtable.
     * @throws NullPointerException if e is equal to null
     */
    @Override public boolean add(E e) {
        if (e == null)
            throw new NullPointerException();
        if (size == tablesize)
            return false;
        int hashFunction = e.hashCode() % tablesize;
        lap[Math.abs(hashFunction)].addFirst((Laptop)e);
        size++;
        return true;
    }

    /**
     * This method removes an element into the hashtable
     * @param e the element to be removed from this hashtable, if present
     * @return true if successfully removed from the hashtable and false if it isn't successfully removed from the hashtable
     * @throws NullPointerException if element is equal to null
     */
    @Override public boolean remove(E e) {
        if (e == null)
            throw new NullPointerException();
        if (isEmpty())
            return false;
        int hashFunction = e.hashCode() % tablesize;
        if (lap[Math.abs(hashFunction)].remove((Laptop) e)) {
            size--;
            return true;
        }
        return false;
    }

    /**
     * This method returns a string showing the entire hash table structure of this set. The format must be as follows:
     * Suppose a table has four slots, with three elements 'a', 'b', 'c', hashed to the first slot and 'z' hashed to the
     * third slot. Printing out the returned string should show the following:
     *
     * 1 || a -> b -> c
     * 2 ||
     * 3 || z
     *
     * Note that the elements 'a', 'b', 'c', and 'z' must also be human-readable.
     *
     * @return a string representation of the entire set, showing the underlying hash table structure
     */
    @Override
    public String toString() {
        String s = "";
        int count = 0;
        for (LinkedList linkedList : lap ){
            s += ""+count;
            count++;
            String line = "";
            for (int i = 0; i < linkedList.size() ; i++){
                line += (linkedList.get(i).toString()+" -> ");
            }
            if (!(line.isEmpty())){
                line = " "+line.substring(0,(line.length())-3)+"\n";
                s += line;
            }
            else {
                s += "\n";
            }
        }
        return s;
    }

}
