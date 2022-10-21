package me.osx11.assignment1.a_star;

import me.osx11.assignment1.CaribbeanMap;
import me.osx11.assignment1.Main;

import java.util.List;
import java.util.Queue;

public class Utils {
    /**
     * Determine whether the node is the final node
     */
    public static boolean isEndNode(Coord end,Coord coord) {
        return coord != null && end.equals(coord);
    }

    /**
     * Determine whether the node can be placed in the Open list
     */
    public static boolean canAddNodeToOpen(List<Node> closeList, CaribbeanMap caribbeanMap, int x, int y) {
        // Is it in the me.osx11.assignment1.map
        if (x < 0 || x >= CaribbeanMap.WIDTH || y < 0 || y >= CaribbeanMap.HEIGHT) return false;
        // Determine whether it is an unpassable node

        char currentCell = caribbeanMap.map[y][x];
        if (currentCell == Main.JACK || currentCell == Main.DAVY || currentCell == Main.KRAKEN || currentCell == Main.ROCK || currentCell == Main.DANGER_ZONE) return false;
        // Determine whether the node has a close table
        if (isCoordInClose(closeList, x, y)) return false;

        return true;
    }

    /**
     * Determine whether the coordinates are in the close table
     */
    public static boolean isCoordInClose(List<Node> closeList, Coord coord) {
        return coord!=null&&isCoordInClose(closeList, coord.x, coord.y);
    }

    /**
     * Determine whether the coordinates are in the close table
     */
    public static boolean isCoordInClose(List<Node> closeList, int x, int y) {
        if (closeList.isEmpty()) return false;
        for (Node node : closeList)
        {
            if (node.coord.x == x && node.coord.y == y)
            {
                return true;
            }
        }
        return false;
    }

    public static int calcH(Coord end,Coord coord) {
        return Math.abs(end.x - coord.x) + Math.abs(end.y - coord.y);
    }

    public static Node findNodeInOpen(Queue<Node> openList, Coord coord)
    {
        if (coord == null || openList.isEmpty()) return null;
        for (Node node : openList)
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
    public static void addNeighborNodeInOpen(List<Node> closeList, Queue<Node> openList, CaribbeanMap caribbeanMap, Node current)
    {
        int x = current.coord.x;
        int y = current.coord.y;
        // left
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x - 1, y, Main.DIRECT_VALUE);
        // up
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x, y - 1, Main.DIRECT_VALUE);
        // right
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x + 1, y, Main.DIRECT_VALUE);
        // down
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x, y + 1, Main.DIRECT_VALUE);
        // top left
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x - 1, y - 1, Main.OBLIQUE_VALUE);
        // top right
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x + 1, y - 1, Main.OBLIQUE_VALUE);
        // bottom right
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x + 1, y + 1, Main.OBLIQUE_VALUE);
        // bottom left
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x - 1, y + 1, Main.OBLIQUE_VALUE);
    }

    /**
     * Add a neighbor node to the open table
     */
    public static void addNeighborNodeInOpen(List<Node> closeList, Queue<Node> openList, CaribbeanMap caribbeanMap, Node current, int x, int y, int value)
    {
        if (canAddNodeToOpen(closeList, caribbeanMap,x, y))
        {
            Node end = caribbeanMap.end;
            Coord coord = new Coord(x, y);
            int G = current.G + value; // Calculate the G value of the adjacent node
            Node child = findNodeInOpen(openList, coord);
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
                openList.add(child);
            }
            else if (child.G > G)
            {
                child.G = G;
                child.parent = current;
                // readjust the heap
                openList.add(child);
            }
        }
    }

    public static void drawPath(char[][] maps, Node end) {
        if(end==null||maps==null) return;
        System.out.println("Total cost: " + end.G);
        while (end != null)
        {
            Coord c = end.coord;
            if (maps[c.y][c.x] == Main.FREE) maps[c.y][c.x] = Main.PATH;
            end = end.parent;
        }
    }

    public static void start(List<Node> closeList, Queue<Node> openList, CaribbeanMap caribbeanMap) {
        if(caribbeanMap==null) return;
        // clean
        openList.clear();
        closeList.clear();
        // start searching
        openList.add(caribbeanMap.start);

        boolean result = moveNodes(closeList, openList, caribbeanMap);

        if (!result) System.out.println("NO PATH");

        closeList.forEach(node -> {
            if (node.G == 0 && node.H == 6) System.out.println("OK");
        });

        caribbeanMap.print();
    }

    /**
     * Move the current node
     */
    public static boolean moveNodes(List<Node> closeList, Queue<Node> openList, CaribbeanMap caribbeanMap) {
        while (!openList.isEmpty())
        {
            if (isCoordInClose(closeList, caribbeanMap.end.coord))
            {
                drawPath(caribbeanMap.map, caribbeanMap.end);
                return true;
            }
            Node current = openList.poll();
            closeList.add(current);
            addNeighborNodeInOpen(closeList, openList, caribbeanMap,current);
        }
        return false;
    }
}
