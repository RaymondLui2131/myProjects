import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quadrilateral implements TwoDShape, Positionable {

    List<TwoDPoint> vertices;

    public Quadrilateral(List<TwoDPoint> vertices) {
        this.vertices = vertices;
        try {
            if (!isMember(this.vertices)) {
                this.vertices = null;
                throw new IllegalArgumentException("Not enough points");
            }
            setPosition(this.vertices);
        }
        catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sets the position of this quadrilateral according to the first four elements in the specified list of points. The
     * quadrilateral is formed on the basis of these four points taken in a clockwise manner on the two-dimensional
     * x-y plane, starting with the point with the least x-value. If the input list has more than four elements, the
     * subsequent elements are ignored.
     *
     * @param points the specified list of points.
     */
    @Override
    public void setPosition(List<? extends Point> points) {
        if (isMember(points)) {
            TwoDPoint p1 = new TwoDPoint(points.get(0).coordinates()[0],points.get(0).coordinates()[1]);
            TwoDPoint p2 = new TwoDPoint(points.get(1).coordinates()[0],points.get(1).coordinates()[1]);
            TwoDPoint p3 = new TwoDPoint(points.get(2).coordinates()[0],points.get(2).coordinates()[1]);
            TwoDPoint p4 = new TwoDPoint(points.get(3).coordinates()[0],points.get(3).coordinates()[1]);
            List<TwoDPoint> list = new ArrayList<>();
            list.add(p1);list.add(p2);list.add(p3);list.add(p4);
            List<TwoDPoint> fourElem = list;
            List<TwoDPoint> temp = fourElem;
            List<TwoDPoint> sorted = new ArrayList<>();
            TwoDPoint centroid = findCentroid(fourElem.get(0), fourElem.get(1), fourElem.get(2), fourElem.get(3));
            TwoDPoint vectorOfP1 = vector(fourElem.get(0), centroid);
            TwoDPoint vectorOfP2 = vector(fourElem.get(1), centroid);
            TwoDPoint vectorOfP3 = vector(fourElem.get(2), centroid);
            TwoDPoint vectorOfP4 = vector(fourElem.get(3), centroid);
            List<TwoDPoint> vectors = new ArrayList<>();
            vectors.add(vectorOfP1);
            vectors.add(vectorOfP2);
            vectors.add(vectorOfP3);
            vectors.add(vectorOfP4);
            List<Double> angles = new ArrayList<>();
            for (TwoDPoint p : vectors) {
                double x = p.coordinates()[0];
                double y = p.coordinates()[1];
                double angle = 0;
                if (x < 0) {
                    if (y < 0) {
                        angle = -Math.atan(y / x) + (2 * Math.PI);
                    } else {
                        angle = -Math.atan(y / x);
                    }
                } else if (x > 0) {
                    angle = -Math.atan(y / x) + (Math.PI);
                } else {
                    if (y < 0) {
                        angle = 3 * Math.PI / 2;
                    } else if (y > 0) {
                        angle = Math.PI / 2;
                    } else {
                        angle = 0;
                    }
                }
                angles.add(angle);
            }
            int firstIndex = findLowestIndex(angles);
            sorted.add(temp.get(firstIndex));
            angles.remove(firstIndex);
            temp.remove(firstIndex);
            int secondIndex = findLowestIndex(angles);
            sorted.add(temp.get(secondIndex));
            angles.remove(secondIndex);
            temp.remove(secondIndex);
            int thirdIndex = findLowestIndex(angles);
            sorted.add(temp.get(thirdIndex));
            angles.remove(thirdIndex);
            temp.remove(thirdIndex);
            sorted.add(temp.get(0));
            rotateList(sorted);
            this.vertices = sorted;
        }
    }

    public int findLowestIndex(List<Double> angles){
        double lowest = angles.get(0);
        int lowestIndex = 0;
        for (int i = 1; i < angles.size(); i++){
            if (lowest > angles.get(i)){
                lowestIndex = i;
                lowest = angles.get(i);
            }
        }
        return lowestIndex;
    }


    public TwoDPoint findCentroid(TwoDPoint p1,TwoDPoint p2,TwoDPoint p3,TwoDPoint p4){
        TwoDPoint centroid1 = findCentroid2Points(findCentroidTriangle(p1,p2,p4),findCentroidTriangle(p2,p3,p4));
        TwoDPoint centroid2 = findCentroid2Points(findCentroidTriangle(p1,p3,p4),findCentroidTriangle(p1,p2,p3));
        return findCentroid2Points(centroid1,centroid2);
    }

    public TwoDPoint findCentroidTriangle(TwoDPoint p1,TwoDPoint p2,TwoDPoint p3){
        double x = (p1.coordinates()[0] + p2.coordinates()[0] + p3.coordinates()[0]) / 3;
        double y = (p1.coordinates()[1] + p2.coordinates()[1] + p3.coordinates()[1]) / 3;
        TwoDPoint center = new TwoDPoint(x,y);
        return center;
    }

    public TwoDPoint findCentroid2Points(TwoDPoint p1,TwoDPoint p2){
        double x = (p1.coordinates()[0] + p2.coordinates()[0]) / 2;
        double y = (p1.coordinates()[1] + p2.coordinates()[1]) / 2;
        TwoDPoint center = new TwoDPoint(x,y);
        return center;
    }

    public TwoDPoint vector(TwoDPoint point, TwoDPoint centroid){
        double x = centroid.coordinates()[0] - point.coordinates()[0];
        double y = centroid.coordinates()[1] - point.coordinates()[1];
        TwoDPoint vector = new TwoDPoint(x,y);
        return vector;
    }

    public double distance(TwoDPoint p1,TwoDPoint p2){
        return Math.sqrt(Math.pow(p2.coordinates()[0] - p1.coordinates()[0],2) + Math.pow(p2.coordinates()[1] - p1.coordinates()[1],2));
    }

    /**
     * Retrieve the position of an object as a list of points. The points are be retrieved and added to the returned
     * list in a clockwise manner on the two-dimensional x-y plane, starting with the point with the least x-value. If
     * two points have the same least x-value, then the clockwise direction starts with the point with the lower y-value.
     *
     * @return the retrieved list of points.
     */
    @Override
    public List<? extends Point> getPosition() {
        return this.vertices;
    }

    public List<TwoDPoint> rotateList(List<TwoDPoint> list){
        double x1 = list.get(0).coordinates()[0];
        double x2 = list.get(1).coordinates()[0];
        double x3 = list.get(2).coordinates()[0];
        double x4 = list.get(3).coordinates()[0];
        double y1 = list.get(0).coordinates()[1];
        double y2 = list.get(1).coordinates()[1];
        double y3 = list.get(2).coordinates()[1];
        double y4 = list.get(3).coordinates()[1];
        List<Double> doublesX = new ArrayList<>();
        doublesX.add(x1);doublesX.add(x2);doublesX.add(x3);doublesX.add(x4);
        List<Double> doublesY = new ArrayList<>();
        doublesY.add(y1);doublesY.add(y2);doublesY.add(y3);doublesY.add(y4);
        int first = findLowestIndexOfSortedList(doublesX,doublesY);
        Collections.rotate(list,4 - first);
        return list;
    }

    public int findLowestIndexOfSortedList(List<Double> xs , List<Double> ys){
        double lowest = xs.get(0);
        int lowestIndex = 0;
        for (int i = 1; i < xs.size(); i++){
            if (lowest > xs.get(i)){
                lowestIndex = i;
                lowest = xs.get(i);
            }
            else if (lowest == xs.get(i)){
                lowestIndex = findIndexBasedOnY(lowestIndex,i,ys);
                lowest = xs.get(lowestIndex);
            }
        }
        return lowestIndex;
    }

    public int findIndexBasedOnY(int first,int second,List<Double> ys){
        if (ys.get(first) < ys.get(second)){
            return first;
        }
        else {
            return second;
        }

    }

    public List<TwoDPoint> getPoints(){
        return this.vertices;
    }
    /**
     * @return the number of sides of this quadrilateral, which is always set to four
     */
    @Override
    public int numSides() {
        return 4;
    }

    /**
     * Checks whether or not a list of vertices forms a valid quadrilateral. The <i>trivial</i> quadrilateral, where all
     * four corner vertices are the same point, is considered to be an invalid quadrilateral.
     *
     * @param vertices the list of vertices to check against, where each vertex is a <code>Point</code> type.
     * @return <code>true</code> if <code>vertices</code> is a valid collection of points for a quadrilateral, and
     * <code>false</code> otherwise. For example, if three of the four vertices are in a straight line is invalid.
     */
    @Override
    public boolean isMember(List<? extends Point> vertices) {
        if (vertices.size() < 4){
            return false;
        }
        Point p1 = vertices.get(0);
        Point p2 = vertices.get(1);
        Point p3 = vertices.get(2);
        Point p4 = vertices.get(3);
        if (isTriangle(p1,p2,p4) && isTriangle(p2,p3,p4) && isTriangle(p1,p3,p4) && isTriangle(p1,p2,p3)){
            return true;
        }
        return false;
    }

    public boolean isTriangle(Point p1,Point p2,Point p3){
        double AB = Math.sqrt(Math.pow(p2.coordinates()[0] - p1.coordinates()[0],2) + Math.pow(p2.coordinates()[1] - p1.coordinates()[1],2));
        double BC = Math.sqrt(Math.pow(p3.coordinates()[0] - p2.coordinates()[0],2) + Math.pow(p3.coordinates()[1] - p2.coordinates()[1],2));
        double AC = Math.sqrt(Math.pow(p3.coordinates()[0] - p1.coordinates()[0],2) + Math.pow(p3.coordinates()[1] - p1.coordinates()[1],2));
        if (AB + BC > AC && AC + BC > AB && AB + AC > BC){
            return true;
        }
        return false;
    }

    /**
     * This method snaps each vertex of this quadrilateral to its nearest integer-valued x-y coordinate. For example, if
     * a corner is at (0.8, -0.1), it will be snapped to (1,0). The resultant quadrilateral will thus have all four
     * vertices in positions with integer x and y values. If the snapping procedure described above results in this
     * quadrilateral becoming invalid (e.g., all four corners collapse to a single point), then it is left unchanged.
     * Snapping is an in-place procedure, and the current instance is modified.
     */
    public void snap() {
        Point p1 = vertices.get(0);
        Point p2 = vertices.get(1);
        Point p3 = vertices.get(2);
        Point p4 = vertices.get(3);
        List<TwoDPoint> vertices = new ArrayList<>();
        TwoDPoint firstPoint = new TwoDPoint(round(p1,0),round(p1,1));
        TwoDPoint secondPoint = new TwoDPoint(round(p2,0),round(p2,1));
        TwoDPoint thirdPoint = new TwoDPoint(round(p3,0),round(p3,1));
        TwoDPoint fourthPoint = new TwoDPoint(round(p4,0),round(p4,1));
        vertices.add(firstPoint);vertices.add(secondPoint);vertices.add(thirdPoint);vertices.add(fourthPoint);
        if (isMember(vertices)) {
            this.vertices = vertices;
        }
    }

    public double round(Point p,int i){
        return Math.round(p.coordinates()[i]);
    }

    /**
     * @return the area of this quadrilateral
     */
    public double area() {
        Point p1 = vertices.get(0);
        Point p2 = vertices.get(1);
        Point p3 = vertices.get(2);
        Point p4 = vertices.get(3);
        return (areaOfTriangle(p1,p2,p4) + areaOfTriangle(p2,p3,p4) + areaOfTriangle(p1,p3,p4) + areaOfTriangle(p1,p2,p3)) / 2;
    }

    public double areaOfTriangle(Point p1,Point p2,Point p3){
        double part1 = p1.coordinates()[0] * (p2.coordinates()[1] - p3.coordinates()[1]);
        double part2 = p2.coordinates()[0] * (p3.coordinates()[1] - p1.coordinates()[1]);
        double part3 = p3.coordinates()[0] * (p1.coordinates()[1] - p2.coordinates()[1]);
        return Math.abs((part1 + part2 + part3) / 2.0);
    }

    /**
     * @return the perimeter (i.e., the total length of the boundary) of this quadrilateral
     */
    public double perimeter() {
        Point p1 = this.vertices.get(0);
        Point p2 = this.vertices.get(1);
        Point p3 = this.vertices.get(2);
        Point p4 = this.vertices.get(3);
        double p1AndP2Dis = Math.sqrt(Math.pow(p2.coordinates()[0] - p1.coordinates()[0],2) + Math.pow(p2.coordinates()[1] - p1.coordinates()[1],2));
        double p2AndP3Dis = Math.sqrt(Math.pow(p3.coordinates()[0] - p2.coordinates()[0],2) + Math.pow(p3.coordinates()[1] - p2.coordinates()[1],2));
        double p3AndP4Dis = Math.sqrt(Math.pow(p4.coordinates()[0] - p3.coordinates()[0],2) + Math.pow(p4.coordinates()[1] - p3.coordinates()[1],2));
        double p4AndP1Dis = Math.sqrt(Math.pow(p1.coordinates()[0] - p4.coordinates()[0],2) + Math.pow(p1.coordinates()[1] - p4.coordinates()[1],2));
        return p1AndP2Dis + p2AndP3Dis + p3AndP4Dis + p4AndP1Dis;
    }

    @Override
    public int compareTo(TwoDShape s){
        if (area() > s.area()){
            return 1;
        }
        else if (area() == s.area()){
            return 0;
        }
        else {
            return -1;
        }
    }

    public String toString(){
        double x1 = this.vertices.get(0).coordinates()[0];
        double x2 = this.vertices.get(1).coordinates()[0];
        double x3 = this.vertices.get(2).coordinates()[0];
        double x4 = this.vertices.get(3).coordinates()[0];
        double y1 = this.vertices.get(0).coordinates()[1];
        double y2 = this.vertices.get(1).coordinates()[1];
        double y3 = this.vertices.get(2).coordinates()[1];
        double y4 = this.vertices.get(3).coordinates()[1];
        return "Quadrilateral[(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + "), (" + x3 + ", " + y3 + "), (" + x4 + ", " + y4 + ")]";
    }
}
