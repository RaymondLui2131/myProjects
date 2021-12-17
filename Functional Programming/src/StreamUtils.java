import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class StreamUtils {
    public static void main(String[] args) {
        Collection<String> bob = new ArrayList<>();
        bob.add("");
        bob.add("Cool");
        bob.add("two");
        bob.add("5353");
        bob.add("Folsadsdsa");
        bob.add("Lolsadsdsa");
        bob.add("gjhuh");
        bob.add("Molsadsdsa");
        bob.add("53532");
        bob.add("Dolsadsdsa");
        bob.add("D5353s");
        Person p1 = new Person("bob",5);
        Person p2 = new Person("jeff",611);
        Person p3 = new Person("jason",5);
        Person p4 = new Person("james",52);
        Person p5 = new Person("kobe",222);
        Collection<Person> peopleCollection = new ArrayList<>();
        peopleCollection.add(p1);
        peopleCollection.add(p2);
        peopleCollection.add(p3);
        peopleCollection.add(p4);
        peopleCollection.add(p5);
        HashMap<String,Person> one = new HashMap<>();
        one.put("Amogus",p1);
        one.put("kekw",p2);
        one.put("omegual",p3);
        one.put("kkkk",p4);
        one.put("xDDD",p5);
        System.out.println(capitalized(bob));
        System.out.println(longest(bob,false));
        System.out.println(least(peopleCollection,false).getName());
        System.out.println(flatten(one));
    }

    /**
     * @param strings: the input collection of <code>String</code>s.
     * @return a collection of those <code>String</code>s in the input collection

    that start with a capital letter.

     */
    public static Collection<String> capitalized(Collection<String> strings){
        return strings.stream()
                      .filter(i -> i.length() >= 1? (Character.isUpperCase(i.charAt(0))) : false)
                      .collect(Collectors.toList());

    }

    /**
     * Find and return the longest <code>String</code> in a given collection of <code>String</code>s.
     *
     * @param strings: the given collection of <code>String</code>s.
     * @param from_start: a <code>boolean</code> flag that decides how ties are broken.
    If <code>true</code>, then the element encountered earlier in
     * the iteration is returned, otherwise the later element is returned.
     * @return the longest <code>String</code> in the given collection,
     * where ties are broken based on <code>from_start</code>.
     */
    public static String longest(Collection<String> strings, boolean from_start){
        return strings.stream()
                      .reduce((a,b) -> a.length() > b.length()? a : a.length() == b.length()? from_start? a : b : b)
                      .map(Object::toString)
                      .orElse("");

    }

    /**
     * Find and return the least element from a collection of given elements that are comparable.
     *
     * @param items:      the given collection of elements
     * @param from_start: a <code>boolean</code> flag that decides how ties are broken.
     *                    If <code>true</code>, the element encountered earlier in the
     *                    iteration is returned, otherwise the later element is returned.
     * @param <T>:        the type parameter of the collection (i.e., the items are all of type T).
     * @return the least element in <code>items</code>, where ties are
     * broken based on <code>from_start</code>.
     */
    public static <T extends Comparable<T>> T least(Collection<T> items, boolean from_start) {
        return items.stream()
                    .reduce((a,b) -> a.compareTo(b) == 0? (from_start? a : b ): a.compareTo(b) > 0? b : a)
                    .orElse(null);
    }

    /**
     * Flattens a map to a stream of <code>String</code>s, where each element in the list
     * is formatted as "key -> value" (i.e., each key-value pair is converted to a string
     * with this format).
     *
     * @param aMap the specified input map.
     * @param <K> the type parameter of keys in <code>aMap</code>.
     * @param <V> the type parameter of values in <code>aMap</code>.
     * @return the flattened list representation of <code>aMap</code>.
     */
    public static <K, V> List<String> flatten(Map<K, V> aMap){
         return aMap.keySet()
                    .stream()
                    .map(a -> a.toString() + " -> " + aMap.get(a).toString())
                    .collect(Collectors.toList());

    }

}
