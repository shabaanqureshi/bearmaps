import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.HashSet;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */

    public static LinkedList<Long> shortestPath(GraphDB g, double stlon,
                                                double stlat, double destlon, double destlat) {
        LinkedList<Long> r = new LinkedList<>();
        double distance = Double.MAX_VALUE;
        Point start = null;
        Point end = null;
        for (Map.Entry<Long, Point> entry : g.getConnected().entrySet()) {
            Point p = entry.getValue();
            double d = Math.sqrt((stlon - p.lon)
                    * (stlon - p.lon) + (stlat - p.lat) * (stlat - p.lat));
            if (d < distance) {
                start = p;
                distance = d;
            }
        }
        distance = Double.MAX_VALUE;
        for (Map.Entry<Long, Point> entry : g.getConnected().entrySet()) {
            Point p = entry.getValue();
            double newDist = Math.sqrt((destlon - p.lon)
                    * (destlon - p.lon) + (destlat - p.lat) * (destlat - p.lat));
            if (newDist < distance) {
                end = p;
                distance = newDist;
            }
        }
        HashMap<Point, Double> dd = new HashMap<>();
        HashMap<Point, Point> previous = new HashMap<>();
        PriorityQueue<Point> fringe = new PriorityQueue<>();
        HashSet<Point> visited = new HashSet<>();
        fringe.add(start);
        dd.put(start, 0.0);
        while (fringe.size() > 0) {
            Point v = fringe.poll();
            if (visited.contains(v)) {
                continue;
            }
            visited.add(v);
            if (v == end) {
                break;
            }
            for (Edges e : v.edges) {
                if (!dd.containsKey(e.y) || dd.get(e.y) > dd.get(v)
                        + Math.sqrt((e.y.lon - v.lon) * (e.y.lon - v.lon)
                        + (e.y.lat - v.lat) * (e.y.lat - v.lat))) {
                    dd.put(e.y, dd.get(v) + Math.sqrt((e.y.lon - v.lon)
                            * (e.y.lon - v.lon) + (e.y.lat - v.lat) * (e.y.lat - v.lat)));
                    e.y.setFn(Math.sqrt((e.y.lon - end.lon)
                            * (e.y.lon - end.lon) + (e.y.lat - end.lat)
                            * (e.y.lat - end.lat)) + dd.get(e.y));
                    previous.put(e.y, v);
                    fringe.add(e.y);
                }

            }
        }
        Point k = end;
        while (start != previous.get(k)) {
            r.addFirst(k.id);
            k = previous.get(k);
        }
        r.addFirst(k.id);
        r.addFirst(start.id);
        return r;
    }
}
