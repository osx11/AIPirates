package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Coord;
import me.osx11.assignment1.a_star.Node;
import me.osx11.assignment1.a_star.AStar;
import me.osx11.assignment1.mobs.*;

public class Main {
    private static final int[] jack = {0, 0};
    private static final int[] davy = {6, 6};
    private static final int[] kraken = {7, 1};
    private static final int[] rock = {8, 7};
    private static final int[] chest = {8, 0};
    private static final int[] tortuga = {8, 8};

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
            int tortugaToChestCost = findLeastKrakenKillPath(map);

            if (tortugaToChestCost < Integer.MAX_VALUE) {
                System.out.println("Kraken was killed. Going to chest");
                recalculateFinalCost(tortugaToChestCost);
            } else {
                System.out.println("Cannot kill Kraken or reach chest after it");
                return;
            }
        }

        System.out.println();
        System.out.println("Final cost is " + finalCost);
        System.out.println("Note: In the below map @ denotes the path.");
        System.out.println("If the route passes through mob, obstacle, etc., this object is replaced by @");
        System.out.println("If Kraken was killed, it and it's danger zones are removed from map");
        System.out.println();
        map.print();
    }

    private static int findLeastKrakenKillPath(CaribbeanMap caribbeanMap) {
        Node[] possibleEnds = {
                new Node(kraken[0]+1, kraken[1]+1),
                new Node(kraken[0]-1, kraken[1]-1),
                new Node(kraken[0]-1, kraken[1]+1),
                new Node(kraken[0]+1, kraken[1]-1)
        };

        int leastCost = Integer.MAX_VALUE;
        char[][] leastCostMap = new char[9][9];

        for (int i = 0; i < 4; i++) {
            if (possibleEnds[i].coord.equals(new Coord(chest[0], chest[1]))) continue;

            CaribbeanMap mapCopy = new CaribbeanMap(caribbeanMap);
            mapCopy.start = new Node(tortuga[0], tortuga[1]);
            mapCopy.end = possibleEnds[i];

            AStar aStar = new AStar(mapCopy);
            boolean result = aStar.start();

            if (result) {
                int cost = aStar.getFinalCost();

                mapCopy.start = new Node(possibleEnds[i].coord.x, possibleEnds[i].coord.y);
                mapCopy.end = new Node(chest[0], chest[1]);
                mapCopy.removeKraken();

                result = aStar.start();

                if (result) {
                    cost += aStar.getFinalCost();

                    if (cost < leastCost) {
                        leastCost = cost;

                        for (int j = 0; j < mapCopy.map.length; j++)
                            for (int k = 0; k < mapCopy.map[j].length; k++)
                                leastCostMap[j][k] = mapCopy.map[j][k];
                    }
                }
            }
        }

        if (leastCost < Integer.MAX_VALUE) {
            caribbeanMap.setMap(leastCostMap);
        }

        return leastCost;
    }

    private static void recalculateFinalCost(int newCost) {
        if (newCost != Integer.MAX_VALUE) finalCost += newCost;
    }
}
