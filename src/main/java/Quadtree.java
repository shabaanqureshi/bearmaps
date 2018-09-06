import java.util.LinkedList;

public class Quadtree {
    private Node node;
    private static final int MAXDEPTH = 7;
    private Quadtree nW, nE, sW, sE; //nW = northWest, nE = northEast; sW = southWest; sE = southEast
    private int depth;

    public class Node implements Comparable<Node> {
        private double ullat, ullon, lrlat, lrlon; //ullat = upper left latitude; ullon = lower left longitude; lrlat = lower right latitude; lrlon = lower right longitude
        private int fileNumber; //img/11.png --> 11
        private String fileName;

        public Node(double ullon, double ullat, double lrlon, double lrlat, int fileNumber) {
            this.ullon = ullon;
            this.ullat = ullat;
            this.lrlon = lrlon;
            this.lrlat = lrlat;
            this.fileNumber = fileNumber;
            if (this.fileNumber == 0) {
                this.fileName = "img/root.png";
            } else {
                this.fileName = "img/" + fileNumber + ".png";
            }
        }

        @Override
        public int compareTo(Node n) {
            if (this.ullat > n.ullat) {
                return -1;
            } else if (this.ullat < n.ullat) {
                return 1;
            } else if (this.ullat == n.ullat && this.ullon < n.ullon) {
                return -1;
            } else if (this.ullat == n.ullat && this.ullon > n.ullon) {
                return 1;
            } else {
                return 1;
            }
        }

    }

    public double getUllat() {
        return node.ullat;
    }

    public double getFileNumber() {
        return node.fileNumber;
    }

    public double getUllon() {
        return node.ullon;
    }

    public double getLrlat() {
        return node.lrlat;
    }

    public double getLrlon() {
        return node.lrlon;
    }

    public String getFileName() {
        return node.fileName;
    }

    public boolean checkDepth(int depth) {
        return (this.depth == depth);
    }


    public int getDepth() {
        return this.depth;
    }

    public Node getRoot() {
        return node;
    }


    public Quadtree() {
        this.node = new Node(MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT, MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT, 0);
    }

    public Quadtree(Quadtree nW, Quadtree nE, Quadtree sW, Quadtree sE, Node n, int depth) {
        this.nW = nW;
        this.nE = nE;
        this.sW = sW;
        this.sE = sE;
        this.node = n;
        this.depth = depth;
    }

    public String getFilename() {
        return node.fileName;
    }

    public void insert() {
        if (this.depth > MAXDEPTH) {
            return;
        } else {
            Node ul = new Node(node.ullon, node.ullat, (node.ullon + node.lrlon) / 2, (node.ullat + node.lrlat) / 2, node.fileNumber * 10 + 1);
            nW = new Quadtree(null, null, null, null, ul, depth + 1);
            Node ur = new Node((node.ullon + node.lrlon) / 2, node.ullat, node.lrlon, (node.ullat + node.lrlat) / 2, node.fileNumber * 10 + 2);
            nE = new Quadtree(null, null, null, null, ur, depth + 1);
            Node ll = new Node(node.ullon, (node.ullat + node.lrlat) / 2, (node.ullon + node.lrlon) / 2, node.lrlat, node.fileNumber * 10 + 3);
            sW = new Quadtree(null, null, null, null, ll, depth + 1);
            Node lr = new Node((node.ullon + node.lrlon) / 2, (node.ullat + node.lrlat) / 2, node.lrlon, node.lrlat, node.fileNumber * 10 + 4);
            sE = new Quadtree(null, null, null, null, lr, depth + 1);
        }
    }

    public LinkedList<Quadtree> getKids() {
        LinkedList<Quadtree> kids = new LinkedList<>();
        kids.addLast(nW);
        kids.addLast(nE);
        kids.addLast(sW);
        kids.addLast(sE);
        return kids;
    }

}