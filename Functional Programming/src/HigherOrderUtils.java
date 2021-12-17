import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HigherOrderUtils {

    static interface NamedBiFunction<T,U,R> extends BiFunction<T,U,R> {
        String name();
    }

    public static NamedBiFunction<Double,Double,Double> add = new NamedBiFunction<Double,Double,Double>() {
        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            return aDouble + aDouble2;
        }

        @Override
        public String name() {
            return "add";
        }
    };

    public static NamedBiFunction<Double,Double,Double> subtract = new NamedBiFunction<Double,Double,Double>() {
        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            return aDouble - aDouble2;
        }

        @Override
        public String name() {
            return "diff";
        }
    };

    public static NamedBiFunction<Double,Double,Double> multiply = new NamedBiFunction<Double,Double,Double>() {
        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            return aDouble * aDouble2;
        }

        @Override
        public String name() {
            return "mult";
        }
    };

    public static NamedBiFunction<Double,Double,Double> divide = new NamedBiFunction<Double,Double,Double>() {
        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            if (aDouble2 == 0){
                throw new java.lang.ArithmeticException("Divide by zero error");
            }
            return aDouble / aDouble2;
        }

        @Override
        public String name() {
            return "div";
        }
    };

    /**
     * Applies a given list of bifunctions -- functions that take two arguments of a certain type
     * and produce a single instance of that type -- to a list of arguments of that type. The
     * functions are applied in an iterative manner, and the result of each function is stored in
     * the list in an iterative manner as well, to be used by the next bifunction in the next
     * iteration. For example, given
     * List<Double> args = Arrays.asList(1d, 1d, 3d, 0d, 4d), and
     * List<NamedBiFunction<Double, Double, Double>> bfs = [add, multiply, add, divide],

     * <code>zip(args, bfs)</code> will proceed iteratively as follows:
     * - index 0: the result of add(1,1) is stored in args[1] to yield args = [1,2,3,0,4]
     * - index 1: the result of multiply(2,3) is stored in args[2] to yield args = [1,2,6,0,4]
     * - index 2: the result of add(6,0) is stored in args[3] to yield args = [1,2,6,6,4]
     * - index 3: the result of divide(6,4) is stored in args[4] to yield args = [1,2,6,6,1.5]
     *
     * @param args: the arguments over which <code>bifunctions</code> will be applied.
     * @param bifunctions: the list of bifunctions that will be applied on <code>args</code>.
     * @param <T>: the type parameter of the arguments (e.g., Integer, Double)
     * @return the item in the last index of <code>args</code>, which has the final
     * result of all the bifunctions being applied in sequence.
     */
    public static <T> T zip(List<T> args, List<NamedBiFunction<T, T, T>> bifunctions){
        if (args.size() == 0){
            return null;
        }
        List<T> temp = args;
        for (int i = 0 ; i + 1 < temp.size() ; i++){
            T first = temp.get(i);
            T second = temp.get(i+1);
            temp.set(i+1,bifunctions.get(i).apply(first,second));
        }
        return temp.get(temp.size() - 1);
    }

    static class FunctionComposition<T,U,R>  {
        BiFunction<Function<T,U>,Function<U,R>,Function<T,R>> composition = Function::andThen;
    }

    public static void main(String[] args) {
        List<Double> num = Arrays.asList(1d, 1d, 3d, 1d, 4d);
        List<NamedBiFunction<Double, Double, Double>> bfs = Arrays.asList(add,multiply,subtract,divide);
        System.out.println(zip(num,bfs));
    }

}
