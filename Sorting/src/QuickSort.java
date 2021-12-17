

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the QuickSort class takes in a list of elements and sorts it by quickSort
 * and has a method to show each step
 * @author Raymond Lui
 */
public class QuickSort<E extends Comparable<E>> implements Sorter<E> {

    private List<E> elements;
    private Order   order;
    private List<String> output;

    public QuickSort(List<E> elements, Order order) {
        this.elements = elements;
        this.order    = order;
        output = new ArrayList<String>();
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
     * This method sorts the elements list by quickSort
     */
    @Override
    public void sort() {
        if (elements.size() == 0){
            return;
        }
        qS(0, elements.size() - 1);
    }

    /**
     * This is a helper method that takes in two indexes to sort the list
     * elements by quickSort
     * @param low the index to begin sorting
     * @param high the index to end sorting
     */
    public void qS(int low,int high){
        String str = "";
        int compare = 1;
        if (order.equals(Order.DECREASING)){
            compare = -1;
        }

        if (low > high) {
            return;
        }

        if (low == high){
            show2("" + elements.get(low));
            show2(" ");
            return;
        }

        if (high - low == 1){
            show2("" + elements.get(low));
            if (elements.get(low).compareTo(elements.get(high)) == compare){
                swap(low,high);
                show2(" ");
                qS(low,low);
            }
            else {
                show2(" ");
                qS(high,high);
            }
            return;
        }
        show2(""+elements.get(low));
        E pivot = this.elements.get(low);
        int i = low + 1;
        int j = high;
        while (i < j){
            if ((elements.get(i).compareTo(pivot) == compare && (elements.get(j).compareTo(pivot)) == -compare) || elements.get(i).compareTo(pivot) == 0 || elements.get(j).compareTo(pivot) == 0){
                swap(i,j);
                show2("" + elements.get(low));
                i++;
                j--;
            }
            else if ((elements.get(i).compareTo(pivot)) == -compare){
                i++;
            }
            else if (elements.get(j).compareTo(pivot) == compare){
                j--;
            }
        }
        boolean found = false;
        int pivotIndex = low;
        for (int a = low + 1 ; a <= high ; a++){
            if (pivot.compareTo(elements.get(a)) == -compare){
                swap(low,a-1);
                show2(" ");
                pivotIndex = a - 1;
                found = true;
                break;
            }
        }
        if (!found){
            pivotIndex = high;
            swap(low,high);
            show2(" ");
        }
        qS(low,pivotIndex - 1);
        qS(pivotIndex + 1,high);
    }

    /**
     * This method is a helper method that swaps the two
     * indexes in the parameter
     * @param first The index to be sorted
     * @param last The other index to be sorted
     */
    private void swap(int first,int last){
        E hold = elements.get(last);
        elements.set(last,elements.get(first));
        elements.set(first,hold);
    }
    /**
     * The method displays the original input (unsorted) list, and then, each step is shown in a new line. For example,
     * if the original list to be sorted in increasing order is [6, 4, 9, 5, 1, 8, 2, 7, 3], calling this method should
     * string as follows, exactly in the format shown:
     * <p>
     * 6 :: [6, 4, 9, 5, 1, 8, 2, 7, 3]
     * 6 :: [6, 4, 3, 5, 1, 8, 2, 7, 9]
     * 6 :: [6, 4, 3, 5, 1, 2, 8, 7, 9]
     *   :: [2, 4, 3, 5, 1, 6, 8, 7, 9]
     * <p>
     * Only the steps with the first pivot are shown above, and NOT the entire output. The last step with a specific
     * pivot does not show the pivot separately in front of the :: separator, to indicate that a different pivot will
     * be used in the next step. At each stage, you must use the first element as the pivot.
     *
     * @return the string representation of the step-wise transformation of the input list, as done with quick sort
     */
    public String show() {
        if (elements.size() == 0){
            return "[]";
        }
        String str = "";
        for (int i = 0 ; i < output.size() ; i++){
            str += output.get(i) + "\n";
        }
        return str;
    }

    /**
     * This is a helper method for the show method
     * @param s the pivot to shown in the show method
     */
    private void show2(String s){
        s += " :: [";
        if (elements.size() == 0){
            s += "]";
            output.add(s);
            return;
        }
        for (E element:elements){
            s +=  element + ", ";
        }
        s = s.substring(0,s.length()-2) + "]";
        output.add(s);
    }

    public static void main(String... args) {
        ArrayList<Integer> bob = new ArrayList<Integer>();
        bob.add(9);
        bob.add(3);
        bob.add(4);
        bob.add(5);
        bob.add(1);
        bob.add(2);
        bob.add(7);
        bob.add(8);
        bob.add(6);
        Sorter<Integer> quickSorter = new QuickSort<>(Arrays.asList(6,4,9,5,1,8,2,7,3), Order.DECREASING);
//        Sorter<Integer> quickSorter = new QuickSort<>(Arrays.asList(6,5,3,1,7,5,3,6,7,4,8,7,2,4,2,2,1,3,5), Order.INCREASING);
        quickSorter.sort();
        System.out.println(quickSorter.show());
        System.out.println(quickSorter.getList());
    }
}

