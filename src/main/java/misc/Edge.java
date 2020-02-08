package misc;

public class Edge {
    public String src;
    public String dst;
    public int weight;

    public Edge(String src, String dst, int weight) {
        this.src = src;
        this.dst = dst;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "src='" + src + '\'' +
                ", dst='" + dst + '\'' +
                ", weight=" + weight +
                '}';
    }
}
