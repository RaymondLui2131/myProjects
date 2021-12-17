import java.util.ArrayList;
import java.util.List;

/**
 * An unmodifiable point in the standard two-dimensional Euclidean space. The coordinates of such a point is given by
 * exactly two doubles specifying its <code>x</code> and <code>y</code> values.
 */
public class TwoDPoint implements Point, Comparable<TwoDPoint> {
    private double one;
    private double two;

    public TwoDPoint(double x, double y) {
        this.one = x;
        this.two = y;
    }

    /**
     * @return the coordinates of this point as a <code>double[]</code>.
     */
    @Override
    public double[] coordinates() {
        double arr[] = new double[] {this.one,this.two};
        return arr;
    }

    /**
     * Returns a list of <code>TwoDPoint</code>s based on the specified array of doubles. A valid argument must always
     * be an even number of doubles so that every pair can be used to form a single <code>TwoDPoint</code> to be added
     * to the returned list of points.
     *
     * @param coordinates the specified array of doubles.
     * @return a list of two-dimensional point objects.
     * @throws IllegalArgumentException if the input array has an odd number of doubles.
     */
    public static List<TwoDPoint> ofDoubles(double... coordinates) throws IllegalArgumentException {
        List<TwoDPoint> arr = new ArrayList<TwoDPoint>();
        if (coordinates.length % 2 == 0){
            for (int i = 0; i < coordinates.length - 1 ; i++){
                TwoDPoint point = new TwoDPoint(coordinates[i],coordinates[i+1]);
                arr.add(point);
            }
        }
        else {
            throw new IllegalArgumentException("Valid argument must be an even number");
        }
        return arr;
    }

    @Override
    public int compareTo(TwoDPoint o) {
        TwoDPoint point = new TwoDPoint(this.one,this.two);
        if (distanceFromOrigin(o) > distanceFromOrigin(point)){
            return 1;
        }
        else if (distanceFromOrigin(o) == distanceFromOrigin(point)){
            return 0;
        }
        else {
            return -1;
        }
    }

    private double distanceFromOrigin(TwoDPoint p){
        return Math.sqrt(Math.pow(p.coordinates()[0] - 0,2) + Math.pow(p.coordinates()[1] - 0,2));
    }
}
