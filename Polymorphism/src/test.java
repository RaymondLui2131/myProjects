import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        List<TwoDPoint> list = new ArrayList<>();
        TwoDPoint p1 = new TwoDPoint(-2.34,-2.34);
        TwoDPoint p2 = new TwoDPoint(-2.34,2.34);
        TwoDPoint p3 = new TwoDPoint(2.34,-7);
//        TwoDPoint p4 = new TwoDPoint(2.34,-5);
        list.add(p1);
        list.add(p2);
        list.add(p3);
//        list.add(p4);
        Quadrilateral shalome = new Quadrilateral(list);
        shalome.setPosition(list);
        for (Point pog : shalome.getPosition()){
            System.out.println(pog.coordinates()[0] + " " + pog.coordinates()[1]);
        }
        System.out.println();
        List<TwoDPoint> list2 = new ArrayList<>();
        TwoDPoint pp1 = new TwoDPoint(-2.34,-2.34);
        TwoDPoint pp2 = new TwoDPoint(-2.34,2.34);
        TwoDPoint pp3 = new TwoDPoint(2.34,-7);
        list2.add(pp1);
        list2.add(pp2);
        list2.add(pp3);
        Triangle bob = new Triangle(list2);
        for (Point pog : bob.getPoints()){
            System.out.println(pog.coordinates()[0] + " " + pog.coordinates()[1]);
        }
    }
}
