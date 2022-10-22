package me.osx11.assignment1.a_star;

import me.osx11.assignment1.CaribbeanMap;
import me.osx11.assignment1.Main;
import me.osx11.assignment1.MapSymbol;
import me.osx11.assignment1.mobs.Kraken;

import java.util.List;
import java.util.Queue;

public class Utils {
    private static int finalCost = 0;

    public static int getFinalCost() { return finalCost; }

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
        if (x < 0 || x >= CaribbeanMap.WIDTH || y < 0 || y >= CaribbeanMap.HEIGHT) return false;
        // Determine whether it is an unpassable node

        char currentCell = caribbeanMap.map[y][x];

        if (caribbeanMap.dangerMobs.stream().anyMatch(mob -> {
            if (mob instanceof Kraken) return mob.icon == currentCell && !caribbeanMap.gainedRum;
            return currentCell == mob.icon || currentCell == MapSymbol.DANGER_ZONE.symbol;
        })) return false;

//        if (currentCell == MapSymbol.DAVY.symbol || (currentCell == MapSymbol.KRAKEN.symbol && !caribbeanMap.gainedRum) || currentCell == MapSymbol.ROCK.symbol || currentCell == MapSymbol.DANGER_ZONE.symbol) return false;
        // Determine whether the node has a close table
        if (isCoordInClose(closeList, x, y)) return false;

        return true;
    }

    /**
     * Determine whether the coordinates are in the close table
     */
    public static boolean isCoordInClose(List<Node> closeList, Coord coord) {
        return coord != null && isCoordInClose(closeList, coord.x, coord.y);
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
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x - 1, y, Main.DIRECT_COST);
        // up
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x, y - 1, Main.DIRECT_COST);
        // right
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x + 1, y, Main.DIRECT_COST);
        // down
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x, y + 1, Main.DIRECT_COST);
        // top left
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x - 1, y - 1, Main.DIAGONAL_COST);
        // top right
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x + 1, y - 1, Main.DIAGONAL_COST);
        // bottom right
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x + 1, y + 1, Main.DIAGONAL_COST);
        // bottom left
        addNeighborNodeInOpen(closeList, openList, caribbeanMap,current, x - 1, y + 1, Main.DIAGONAL_COST);
    }

    /**
     * Add a neighbor node to the open table
     */
    public static void addNeighborNodeInOpen(List<Node> closeList, Queue<Node> openList, CaribbeanMap caribbeanMap, Node current, int x, int y, int value) {
        if (canAddNodeToOpen(closeList, caribbeanMap, x, y))
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

        finalCost = end.G;

        while (end != null)
        {
            Coord c = end.coord;
            maps[c.y][c.x] = MapSymbol.PATH.symbol;
            end = end.parent;
        }
    }

    public static boolean start(List<Node> closeList, Queue<Node> openList, CaribbeanMap caribbeanMap) {
        finalCost = 0;

        if (caribbeanMap==null) return false;
        // clean
        openList.clear();
        closeList.clear();
        // start searching
        openList.add(caribbeanMap.start);

        return moveNodes(closeList, openList, caribbeanMap);
    }

    /**
     * Move the current node
     */
    public static boolean moveNodes(List<Node> closeList, Queue<Node> openList, CaribbeanMap caribbeanMap) {
        while (!openList.isEmpty()) {
            if (isCoordInClose(closeList, caribbeanMap.end.coord)) {
                drawPath(caribbeanMap.map, caribbeanMap.end);
                return true;
            }

            Node current = openList.poll();
            closeList.add(current);
            addNeighborNodeInOpen(closeList, openList, caribbeanMap,current);
        }

        finalCost = Integer.MAX_VALUE;
        return false;
    }
}
