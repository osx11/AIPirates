package me.osx11.assignment1.a_star;

import me.osx11.assignment1.Algorithm;
import me.osx11.assignment1.CaribbeanMap;
import me.osx11.assignment1.MapSymbol;

import java.util.*;

public class AStar implements Algorithm {
    private int finalCost;
    private final Queue<Node> openList;
    private final List<Node> closeList;
    private CaribbeanMap caribbeanMap;
    private List<Coord> path = new ArrayList<>();

    public AStar() {
        this.openList = new PriorityQueue<>();
        this.closeList = new ArrayList<>();
        this.finalCost = 0;
    }

    public void setMap(CaribbeanMap caribbeanMap) {
        this.caribbeanMap = caribbeanMap;
    }

    public int getFinalCost() { return finalCost; }

    /**
     * Determine if the node contains danger to determine if it can be placed in Open List
     * @param x x coordinate
     * @param y y coordinate
     * @return true if coordinate can be added to open list (means there is not danger in the cell), otherwise false
     */
    public boolean canAddNodeToOpen(int x, int y) {
        if (x < 0 || x >= CaribbeanMap.WIDTH || y < 0 || y >= CaribbeanMap.HEIGHT) return false;
        // Determine whether it is an unpassable node

        char currentCell = this.caribbeanMap.map[y][x];

        boolean dangerInCell = this.caribbeanMap.dangerMobs
                .stream()
                .anyMatch(mob -> currentCell == mob.icon) || currentCell == MapSymbol.DANGER_ZONE.symbol;

        if (dangerInCell) return false;

        // Determine whether the node has a close table
        if (isCoordInClose(x, y)) return false;

        return true;
    }

    /**
     * Determine if the coordinate is in close list
     * @param coord coordinate to check
     * @return true if coordinate is in close list, otherwise false
     */
    public boolean isCoordInClose(Coord coord) {
        return coord != null && isCoordInClose(coord.x, coord.y);
    }

    /**
     * Determine if the coordinate is in close list
     * @param x x coordinate to check
     * @param y y coordinate to check
     * @return true if coordinate is in close list, otherwise false
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

    /**
     * Calculates heuristic distance between two coordinates
     * @param end first coordinate
     * @param coord second coordinate
     * @return heuristic distance
     */
    public int calcH(Coord end, Coord coord) {
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
     * Add all neighbor nodes (left, up, right, down, top left, top right, bottom left, bottom right) to the open table
     * @param current current node
     */
    public void addNeighborNodeInOpen(Node current)
    {
        int x = current.coord.x;
        int y = current.coord.y;

        addNeighborNodeInOpen(current, x - 1, y);
        addNeighborNodeInOpen(current, x, y - 1);
        addNeighborNodeInOpen(current, x + 1, y);
        addNeighborNodeInOpen(current, x, y + 1);
        addNeighborNodeInOpen(current, x - 1, y - 1);
        addNeighborNodeInOpen(current, x + 1, y - 1);
        addNeighborNodeInOpen(current, x + 1, y + 1);
        addNeighborNodeInOpen(current, x - 1, y + 1);
    }

    /**
     * Add a neighbor node to the open table
     * @param current node to add
     * @param x x coordinate of the node
     * @param y y coordinate of the node
     */
    public void addNeighborNodeInOpen(Node current, int x, int y) {
        if (canAddNodeToOpen(x, y)) {
            Node end = this.caribbeanMap.end;
            Coord coord = new Coord(x, y);
            int G = current.G + 1; // Calculate the G value of the adjacent node
            Node child = findNodeInOpen(coord);
            if (child == null)
            {
                int H=calcH(end.coord,coord); // calculate H value

                if (end.coord.equals(coord)) {
                    child=end;
                    child.parent=current;
                    child.G=G;
                    child.H=H;
                } else {
                    child = new Node(coord, current, G, H);
                }
                this.openList.add(child);
            } else if (child.G > G) {
                child.G = G;
                child.parent = current;
                // readjust the heap
                this.openList.add(child);
            }
        }
    }

    /**
     * Draw the final path on the map
     */
    public void drawPath() {
        this.finalCost = this.caribbeanMap.end.G;

        while (this.caribbeanMap.end != null) {
            Coord c = this.caribbeanMap.end.coord;
            if (this.caribbeanMap.map[c.y][c.x] != MapSymbol.JACK.symbol)
                this.caribbeanMap.map[c.y][c.x] = MapSymbol.PATH.symbol;
            this.caribbeanMap.end = this.caribbeanMap.end.parent;

            this.path.add(c);
        }
    }

    /**
     * Common method to solve the task
     */
    public boolean solve() {
        this.path = new ArrayList<>();
        this.finalCost = 0;

        // clean
        this.openList.clear();
        this.closeList.clear();
        // start searching
        this.openList.add(this.caribbeanMap.start);

        return moveNodes();
    }

    /**
     * Move the current node (means move the Jack's position)
     */
    public boolean moveNodes() {
        while (!this.openList.isEmpty()) {
            if (isCoordInClose(this.caribbeanMap.end.coord)) {
                drawPath();
                return true;
            }

            Node current = openList.poll();
            this.closeList.add(current);
            addNeighborNodeInOpen(current);
        }

        this.finalCost = Integer.MAX_VALUE;
        return false;
    }

    /**
     * @return array of coordinates with path
     */
    public List<Coord> getFinalPath() {
        List<Coord> pathCopy = new ArrayList<>(this.path);
        Collections.reverse(pathCopy);

        return pathCopy.subList(1, pathCopy.size());
    }
}
