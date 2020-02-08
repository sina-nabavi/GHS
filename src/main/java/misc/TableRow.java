package misc;

public class TableRow {

    @Override
    public String toString() {
        return String.format("%s - %s - %s",dst,first_node,dist);
    }

    public String dst;
    public String first_node;
    public int dist;

    public TableRow(String dst, String first_node, int dist) {
        this.dst = dst;
        this.first_node = first_node;
        this.dist = dist;
    }
}
