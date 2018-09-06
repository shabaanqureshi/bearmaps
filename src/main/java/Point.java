import java.util.HashSet;
import java.util.Set;


public class Point implements Comparable<Point> {
    Set<Edges> edges;
    Long id;
    Double lat, lon;
    double f;

    public Point(Long id, Double lat, Double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        edges = new HashSet<>();
    }

    public void setFn(double f) {
        this.f = f;
    }

    @Override
    public int compareTo(Point n) {
        if (this.f > n.f) {
            return 1;
        } else {
            return -1;
        }
    }
}