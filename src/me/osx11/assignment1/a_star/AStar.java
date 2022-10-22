package me.osx11.assignment1.a_star;

import me.osx11.assignment1.CaribbeanMap;
import me.osx11.assignment1.Main;
import me.osx11.assignment1.MapSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class AStar {
    private int finalCost;
    private final Queue<Node> openList;
    private final List<Node> closeList;
    private final CaribbeanMap caribbeanMap;

    public AStar(CaribbeanMap caribbeanMap) {
        this.openList = new PriorityQueue<>();
        this.closeList = new ArrayList<>();
        this.finalCost = 0;
        this.caribbeanMap = caribbeanMap;
    }

    public int getFinalCost() { return finalCost; }

    /**
     * Determine whether the node is the final node
     */
    public boolean isEndNode(Coord end, Coord coord) {
        return end.equals(coord);
    }

    /**
     * Determine whether the node can be placed in the Open list
     */
    public boolean canAddNodeToOpen(int x, int y) {
        if (x < 0 || x >= CaribbeanMap.WIDTH || y < 0 || y >= CaribbeanMap.HEIGHT) return false;
        // Determine whether it is an unpassable node

        char currentCell = this.caribbeanMap.map[y][x];

        boolean dangerInCell = this.caribbeanMap.dangerMobs
                .stream()
                .anyMatch(mob -> currentCell == mob.icon || currentCell == MapSymbol.DANGER_ZONE.symbol);

        if (dangerInCell) return false;

        // Determine whether the node has a close table
        if (isCoordInClose(x, y)) return false;

        return true;
    }

    /**
     * Determine whether the coordinates are in the close table
     */
    public boolean isCoordInClose(Coord coord) {
        return coord != null && isCoordInClose(coord.x, coord.y);
    }

    /**
     * Determine whether the coordinates are in the close table
     */
    public boolean isCoordInClose(int x, int y) {
        if (this.closeList.isEmpty()) return false;
        for (Node node : this.closeList)
        {
            if (node.coord.x == x && node.coord.y == y)
            {
                return true;
            }
        }
        return false;
    }

    public int calcH(Coord end,Coord coord) {
        return Math.abs(end.x - coord.x) + Math.abs(end.y - coord.y);
    }

    public Node findNodeInOpen(Coord coord)
    {
        if (coord == null || this.openList.isEmpty()) return null;
        for (Node node : this.openList)
        {
            if (node.coord.equals(coord))
            {
                return node;
            }
        }
        return null;
    }

    /**
     * Add all neighbor nodes to the open table
     */
    public void addNeighborNodeInOpen(Node current)
    {
        int x = current.coord.x;
        int y = current.coord.y;
        // left
        addNeighborNodeInOpen(current, x - 1, y, Main.DIRECT_COST);
        // up
        addNeighborNodeInOpen(current, x, y - 1, Main.DIRECT_COST);
        // right
        addNeighborNodeInOpen(current, x + 1, y, Main.DIRECT_COST);
        // down
        addNeighborNodeInOpen(current, x, y + 1, Main.DIRECT_COST);
        // top left
        addNeighborNodeInOpen(current, x - 1, y - 1, Main.DIAGONAL_COST);
        // top right
        addNeighborNodeInOpen(current, x + 1, y - 1, Main.DIAGONAL_COST);
        // bottom right
        addNeighborNodeInOpen(current, x + 1, y + 1, Main.DIAGONAL_COST);
        // bottom left
        addNeighborNodeInOpen(current, x - 1, y + 1, Main.DIAGONAL_COST);
    }

    /**
     * Add a neighbor node to the open table
     */
    public void addNeighborNodeInOpen(Node current, int x, int y, int value) {
        if (canAddNodeToOpen(x, y))
        {
            Node end = this.caribbeanMap.end;
            Coord coord = new Coord(x, y);
            int G = current.G + value; // Calculate the G value of the adjacent node
            Node child = findNodeInOpen(coord);
            if (child == null)
            {
                int H=calcH(end.coord,coord); // calculate H value
                if(isEndNode(end.coord,coord))
                {
                    child=end;
                    child.parent=current;
                    child.G=G;
                    child.H=H;
                }
                else
                {
                    child = new Node(coord, current, G, H);
                }
                this.openList.add(child);
            }
            else if (child.G > G)
            {
                child.G = G;
                child.parent = current;
                // readjust the heap
                this.openList.add(child);
            }
        }
    }

    public void drawPath(char[][] maps, Node end) {
        if(end==null||maps==null) return;

        finalCost = end.G;

        while (end != null)
        {
            Coord c = end.coord;
            maps[c.y][c.x] = MapSymbol.PATH.symbol;
            end = end.parent;
        }
    }

    public boolean start() {
        finalCost = 0;

        // clean
        this.openList.clear();
        this.closeList.clear();
        // start searching
        this.openList.add(this.caribbeanMap.start);

        return moveNodes();
    }

    /**
     * Move the current node
     */
    public boolean moveNodes() {
        while (!this.openList.isEmpty()) {
            if (isCoordInClose(this.caribbeanMap.end.coord)) {
                drawPath(this.caribbeanMap.map, this.caribbeanMap.end);
                return true;
            }

            Node current = openList.poll();
            this.closeList.add(current);
            addNeighborNodeInOpen(current);
        }

        finalCost = Integer.MAX_VALUE;
        return false;
    }
}
