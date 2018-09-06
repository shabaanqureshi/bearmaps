import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //your own QuadTree since there is no built-in quadtree in Java.

    Quadtree q;


    /**
     * imgRoot is the name of the directory containing the images.
     * You may not actually need this for your class.
     */

    public Rasterer(String imgRoot) {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
<<<<<<< HEAD
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>Has dimensions of at least w by h, where w and h are the user viewport width
     * and height.</li>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
=======
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
>>>>>>> 1c0df392fa29a10a01640a01900abcf4fb511fb7
     * </p>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image
     * string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     * forget to set this to true! <br>
     * @see # REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        q = new Quadtree();
        LinkedList<String> tiles = new LinkedList<>();
        LinkedList<Quadtree> s1 = findWaldo(q, params, new LinkedList<>());
        String[][] s2 = grid(s1);
        double rasteredUllon = yo(s1, "rastered_ullon");
        double rasteredUllat = yo(s1, "rastered_ullat");
        double rasteredLrlon = yo(s1, "rastered_lrlon");
        double rasteredLrlat = yo(s1, "rastered_lrlat");
        results.put("raster_ul_lon", rasteredUllon);
        results.put("depth", s1.peek().getDepth());
        results.put("raster_lr_lon", rasteredLrlon);
        results.put("raster_lr_lat", rasteredLrlat);
        results.put("raster_ul_lat", rasteredUllat);
        results.put("query_success", true);
        results.put("render_grid", s2);
        return results;
    }


    public String[][] grid(LinkedList<Quadtree> daddy) {
        HashSet<Double> lats = new HashSet<>(); // seen latitudes
        LinkedList<Double> theseLats = new LinkedList<>();
        for (Quadtree d : daddy) {
            if (!lats.contains(d.getUllat())) {
                theseLats.addLast(d.getUllat());
                lats.add(d.getUllat());
            }
        }

        LinkedList<Quadtree> sorted = new LinkedList<>();
        for (double l : theseLats) {
            for (Quadtree da : daddy) {
                if (da.getUllat() == l) {
                    sorted.addLast(da);
                }
            }
        }
        int rows = lats.size();
        int cols = daddy.size() / rows;
        String[][] a = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                a[i][j] = sorted.removeFirst().getFileName();
            }
        }
        return a;
    }


    private Double yo(LinkedList<Quadtree> daddy, String s) {
        HashSet<Double> lats = new HashSet<>(); // seen latitudes
        LinkedList<Double> theseLats = new LinkedList<>();
        for (Quadtree d : daddy) {
            if (!lats.contains(d.getUllat())) {
                theseLats.addLast(d.getUllat());
                lats.add(d.getUllat());
            }
        }
        LinkedList<Quadtree> sorted = new LinkedList<>();
        for (double l : theseLats) {
            for (Quadtree da : daddy) {
                if (da.getUllat() == l) {
                    sorted.addLast(da);
                }
            }
        }
        if (s.equals("rastered_ullon")) {
            return sorted.get(0).getUllon();
        }
        if (s.equals("rastered_lrlon")) {
            return sorted.get(sorted.size() - 1).getLrlon();
        }
        if (s.equals("rastered_ullat")) {
            return sorted.get(0).getUllat();
        }
        if (s.equals("rastered_lrlat")) {
            return sorted.get(sorted.size() - 1).getLrlat();
        } else {
            throw new RuntimeException("Please insert an eligible String");
        }
    }


    public LinkedList<Quadtree> findWaldo(Quadtree n, Map<String, Double> p,
                                          LinkedList<Quadtree> daddy) {
        if (intersectTiles(p.get("ullat"), p.get("lrlat"), p.get("ullon"), p.get("lrlon"), n)) {
            if (longerThanLongDPP(p, n)) {
//                image.add
                daddy.addLast(n);
            } else {
                n.insert();
                LinkedList<Quadtree> children = n.getKids();
                for (Quadtree c : children) {
                    daddy = findWaldo(c, p, daddy);
                }
            }
        }
        return daddy;
    }

    public boolean intersectTiles(double queryUllat, double queryLrlat,
                                  double queryUllon, double queryLrlon, Quadtree n) {
        if (queryLrlat > n.getUllat() || n.getLrlat() > queryUllat
                || queryLrlon < n.getUllon() || queryUllon > n.getLrlon()) {
            return false;
        }
        if ((n.getUllat() >= queryUllat && queryUllat >= n.getLrlat())
                || (n.getUllat() >= queryLrlat && queryLrlat >= n.getLrlat())) {
            return true;
        }
        if ((queryUllat >= n.getUllat() && n.getUllat() >= queryLrlat)
                || (queryUllat >= n.getLrlat() && n.getLrlat() >= queryLrlat)) {
            return true;
        }
        return false;
    }

    public boolean longerThanLongDPP(Map<String, Double> p, Quadtree n) {
        if (n.getDepth() != 0) {
            return (n.getDepth() == 7
                    || (Double) (49 / Math.pow(2, (n.getDepth() - 1))) <= lonDPP(p));
        }
        return false;
    }

    public double lonDPP(Map<String, Double> p) {
        return ((p.get("lrlon") - (p.get("ullon"))) * 288200) / (p.get("w"));
    }


}


