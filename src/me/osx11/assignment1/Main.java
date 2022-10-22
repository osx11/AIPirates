package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Coord;
import me.osx11.assignment1.a_star.Node;
import me.osx11.assignment1.a_star.AStar;
import me.osx11.assignment1.mobs.*;

public class Main {
    private static final int[] jack = {1, 1};
    private static final int[] davy = {6, 7};
    private static final int[] kraken = {8, 6};
    private static final int[] rock = {8, 4};
    private static final int[] chest = {8, 8};
    private static final int[] tortuga = {0, 6};

    public static final int DIRECT_COST = 1; // horizontal and vertical movement cost
    public static final int DIAGONAL_COST = 1; // diagonal movement cost

    private static int finalCost;

    public static void main(String[] args) {
        CaribbeanMap map = new CaribbeanMap(
                new Jack(jack[0], jack[1]),
                new Chest(chest[0], chest[1]),
                new Tortuga(tortuga[0], tortuga[1]),
                new Davy(davy[0], davy[1]),
                new Kraken(kraken[0], kraken[1]),
                new Rock(rock[0], rock[1]),
                new Node(jack[0], jack[1]),
                new Node(chest[0], chest[1])
        );

        map.print();
        System.out.println();

        AStar aStar = new AStar(map);
        boolean result = aStar.start();
        recalculateFinalCost(aStar.getFinalCost());

        if (!result) {
            System.out.println("Chest is blocked. Trying to get to Tortuga first");
            map.end = new Node(tortuga[0], tortuga[1]);

            result = aStar.start();

            if (!result) {
                System.out.println("LOSE (Tortuga unreachable)");
                map.print();
                return;
            }

            recalculateFinalCost(aStar.getFinalCost());

            Pair<Node, Integer> leastCostKrakenKill = findLeastKrakenKillPath(map);
            recalculateFinalCost(leastCostKrakenKill.t2);

            if (leastCostKrakenKill.t1 == null) {
                System.out.println("LOSE (Cannot kill Kraken)");
                return;
            }

            map.start = new Node(leastCostKrakenKill.t1.coord.x, leastCostKrakenKill.t1.coord.y);
            map.end = new Node(chest[0], chest[1]);
            map.removeKraken();

            result = aStar.start();
            recalculateFinalCost(aStar.getFinalCost());

            if (!result) {
                System.out.println("LOSE (reached Tortuga and tried to kill Kraken, but something still blocks the chest)");
            }
        }

        System.out.println("Final cost " + finalCost + "\n");
        map.print();
    }

    private static Pair<Node, Integer> findLeastKrakenKillPath(CaribbeanMap caribbeanMap) {
        Node[] possibleEnds = {
                new Node(kraken[0]+1, kraken[1]+1),
                new Node(kraken[0]-1, kraken[1]-1),
                new Node(kraken[0]-1, kraken[1]+1),
                new Node(kraken[0]+1, kraken[1]-1)
        };

        int leastCost = Integer.MAX_VALUE;
        Node leastCostNode = null;
        char[][] leastCostMap = new char[9][9];

        for (int i = 0; i < 4; i++) {
            if (possibleEnds[i].coord.equals(new Coord(chest[0], chest[1]))) continue;

            CaribbeanMap mapCopy = new CaribbeanMap(caribbeanMap);
            mapCopy.start = new Node(tortuga[0], tortuga[1]);
            mapCopy.end = possibleEnds[i];

            AStar aStar = new AStar(mapCopy);
            boolean result = aStar.start();

            if (result && aStar.getFinalCost() < leastCost) {
                leastCost = aStar.getFinalCost();
                leastCostNode = possibleEnds[i];

                for (int j = 0; j < mapCopy.map.length; j++)
                    for (int k = 0; k < mapCopy.map[j].length; k++)
                        leastCostMap[j][k] = mapCopy.map[j][k];
            }
        }

        if (leastCostNode != null) {
            caribbeanMap.setMap(leastCostMap);
            caribbeanMap.removeKraken();
        }

        return new Pair<>(leastCostNode, leastCost);
    }

    private static void recalculateFinalCost(int newCost) {
        if (newCost != Integer.MAX_VALUE) finalCost += newCost;
    }
}
