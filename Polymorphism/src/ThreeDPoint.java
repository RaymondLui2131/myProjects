/**
 * An unmodifiable point in the three-dimensional space. The coordinates are specified by exactly three doubles (its
 * <code>x</code>, <code>y</code>, and <code>z</code> values).
 */
public class ThreeDPoint implements Point {
    private double one;
    private double two;
    private double three;

    public ThreeDPoint(double x, double y, double z) {
        this.one = x;
        this.two = y;
        this.three = z;
    }

    /**
     * @return the (x,y,z) coordinates of this point as a <code>double[]</code>.
     */
    @Override
    public double[] coordinates() {
        double arr[] = new double[] {this.one,this.two,this.three};
        return arr;
    }
}
