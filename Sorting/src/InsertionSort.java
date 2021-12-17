

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the InsertionSort class takes in a list of elements and sorts it by insertionSort
 * and has a method to show each step
 * @author Raymond Lui
 */
public class InsertionSort<E extends Comparable<E>> implements Sorter<E> {

    private List<E> elements;
    private Order   order;
    private List<String> show;

    public InsertionSort(List<E> elements, Order order) {
        this.elements = elements;
        this.order    = order;
        show = new ArrayList<String>();
    }

    /**
     * This method gets the list
     * @return the list in the insertionSort class elements
     */
    @Override
    public List<E> getList() {
        return elements;
    }

    /**
     * This method sorts the elements list by insertionSort
     */
    @Override
    public void sort() {
        int num;
        if (order.equals(Order.INCREASING))
            num = 1;
        else
            num = -1;
        show = new ArrayList<String>();
        int i,j = 0;
        E item = null;
        for (i = 1; i <= elements.size()-1; i++) {
            item = elements.get(i);
            j = i;
            show.add(makeString(item));
            while (j > 0 && (elements.get(j - 1).compareTo(item)) == num ) {
                elements.set(j, elements.get(j - 1));
                j--;
            }
            elements.set(j,item);
        }
        show.add(makeStr());
    }


    /**
     * The method displays the original input (unsorted) list, and then, each step is shown in a new line. For example,
     * if the original list to be sorted in increasing order is [6, 5, 3, 1, 8, 7, 2, 4], calling this method should
     * return the following string (exactly in this format):
     * <p>
     * 5 :: [6, 5, 3, 1, 8, 7, 2, 4]
     * 3 :: [5, 6, 3, 1, 8, 7, 2, 4]
     * 1 :: [3, 5, 6, 1, 8, 7, 2, 4]
     * 8 :: [1, 3, 5, 6, 8, 7, 2, 4]
     * 7 :: [1, 3, 5, 6, 8, 7, 2, 4]
     * 2 :: [1, 3, 5, 6, 7, 8, 2, 4]
     * 4 :: [1, 2, 3, 5, 6, 7, 8, 4]
     * [1, 2, 3, 4, 5, 6, 7, 8]
     * <p>
     * At each step, the element being inspected for insertion is at the start, and the list in its current state is
     * then placed after ::, two colon symbols. Notice the repeated list when the element being inspected for insertion
     * is 8, and the list does not change at all. Such repetitions must be included in the returned string.
     *
     * @return the string representation of the step-wise transformation of the input list, as done with insertion sort
     */
    @Override
    public String show() {
        String str = "";
        for (int i = 0; i < show.size(); i++){
            str += show.get(i) + "\n";
        }
        return str;
    }

    /**
     * This methods makes a String for the show method
     * @param item the item to be swapped
     * @return the string representation of steps of insertionSort
     */
    private String makeString(E item){
        return item + " :: " + makeStr();
    }

    /**
     * This method makes most of the String for the show method
     * @return part of the string representation of steps of insertionSort
     */
    private String makeStr(){
        String str = "[";
        for (int i = 0; i < elements.size(); i++) {
            str += elements.get(i) + "," + " ";
        }
        if (elements.size() != 0)
            str = str.substring(0,str.length()-2);
        str += "]";
        return str;
    }

    /**
     * Just an example showing how your code will be used. The same list of elements used to explain the show() method
     * is being used in this example. Notice that this main method actually doesn't care about the type of algorithm
     * used by the sorter. For example, you could have a BubbleSort, MergeSort, or QuickSort implemenation instead, and
     * someone using your code would not need to change anything!
     *
     * This is a type of "abstraction" in code. We are ending this semester on this note, because you will learn a lot
     * more about abstraction in CSE 216.
     */
    public static void main(String... args) {
        Sorter<Integer> intsorter = new InsertionSort<>(Arrays.asList(6, 5, 3, 1, 8, 7, 2, 4), Order.DECREASING);
        intsorter.sort();
        System.out.println(intsorter.show());
        // NOTE: the list shown at the end of the string printed after calling show() MUST be identical to the result
        // calling getList() after calling sort(). That is, the backing list must actually be changed as is shown by the
        // result of the show() method.
    }
//    public static void main(String... args) {
//        ArrayList<Integer> bob = new ArrayList<Integer>();
//        bob.add(6);
//        bob.add(5);
//        bob.add(3);
//        bob.add(1);
//        bob.add(8);
//        bob.add(7);
//        bob.add(2);
//        bob.add(4);
//        InsertionSort bobby = new InsertionSort(bob,Order.DECREASING);
//        bobby.sort();
//        System.out.println(bobby.show());
//        System.out.println(bobby.getList());
//    }
}
