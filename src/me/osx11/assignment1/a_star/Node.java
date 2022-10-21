package me.osx11.assignment1.a_star;

public class Node implements Comparable<Node>
{

    public Coord coord; // coordinates
    public Node parent; // parent node
    public int G; // G: is an accurate value, is the cost from the starting point to the current node
    public int H; // H: is an estimate, the estimated cost from the current node to the destination node

    public Node(int x, int y)
    {
        this.coord = new Coord(x, y);
    }

    public Node(Coord coord, Node parent, int g, int h)
    {
        this.coord = coord;
        this.parent = parent;
        G = g;
        H = h;
    }

    @Override
    public int compareTo(Node o)
    {
        if (o == null) return -1;
        if (G + H > o.G + o.H)
            return 1;
        else if (G + H < o.G + o.H) return -1;
        return 0;
    }
}
