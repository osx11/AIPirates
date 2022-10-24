package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Coord;
import me.osx11.assignment1.a_star.Node;
import me.osx11.assignment1.a_star.AStar;
import me.osx11.assignment1.mobs.*;

public class Main {
    private static final Algorithm algorithm = new AStar();

    private static final int[] jack = {0, 0};
    private static final int[] davy = {6, 6};
    private static final int[] kraken = {7, 1};
    private static final int[] rock = {8, 7};
    private static final int[] chest = {8, 0};
    private static final int[] tortuga = {8, 8};

    private static int finalCost;

    public static void main(String[] args) {
        CaribbeanMap caribbeanMap = new CaribbeanMap(
                new Jack(jack[0], jack[1]),
                new Chest(chest[0], chest[1]),
                new Tortuga(tortuga[0], tortuga[1]),
                new Davy(davy[0], davy[1]),
                new Kraken(kraken[0], kraken[1]),
                new Rock(rock[0], rock[1]),
                new Node(jack[0], jack[1]),
                new Node(chest[0], chest[1])
        );

        algorithm.setMap(caribbeanMap);
        caribbeanMap.print();
        System.out.println();

        boolean result = algorithm.solve();
        recalculateFinalCost(algorithm.getFinalCost());

        if (!result) {
            System.out.println("Chest is blocked. Trying to get to Tortuga first");
            caribbeanMap.end = new Node(tortuga[0], tortuga[1]);

            result = algorithm.solve();

            if (!result) {
                System.out.println("LOSE (Tortuga unreachable)");
                caribbeanMap.print();
                return;
            }

            recalculateFinalCost(algorithm.getFinalCost());
            int tortugaToChestCost = findLeastKrakenKillPath(caribbeanMap);

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
        caribbeanMap.print();
    }

    private static int findLeastKrakenKillPath(CaribbeanMap caribbeanMap) {
        Node[] possibleEnds = {
                new Node(kraken[0]+1, kraken[1]+1),
                new Node(kraken[0]-1, kraken[1]-1),
                new Node(kraken[0]-1, kraken[1]+1),
                new Node(kraken[0]+1, kraken[1]-1),
        };

        int leastCost = Integer.MAX_VALUE;
        char[][] leastCostMap = new char[9][9];

        for (int i = 0; i < 4; i++) {
            if (possibleEnds[i].coord.x > 8 || possibleEnds[i].coord.x < 0 || possibleEnds[i].coord.y > 8 || possibleEnds[i].coord.y < 0) continue;
            if (possibleEnds[i].coord.equals(new Coord(chest[0], chest[1]))) continue;

            char startCell = caribbeanMap.map[possibleEnds[i].coord.y][possibleEnds[i].coord.x];
            if (startCell != MapSymbol.FREE.symbol && startCell != MapSymbol.CHEST.symbol && startCell != MapSymbol.PATH.symbol) {
                continue;
            }

            CaribbeanMap mapCopy = new CaribbeanMap(caribbeanMap);
            mapCopy.start = new Node(tortuga[0], tortuga[1]);
            mapCopy.end = possibleEnds[i];

            algorithm.setMap(mapCopy);
            boolean result = algorithm.solve();

            if (result) {
                int cost = algorithm.getFinalCost();

                mapCopy.start = new Node(possibleEnds[i].coord.x, possibleEnds[i].coord.y);
                mapCopy.end = new Node(chest[0], chest[1]);
                mapCopy.removeKraken();

                System.out.println("XUI XUI XUI " + mapCopy.map[7][5]);
                result = algorithm.solve();

                if (result) {
                    cost += algorithm.getFinalCost();

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
        } else {
            System.out.println("CHEST UNREACHABLE");
        }

        return leastCost;
    }

    private static void recalculateFinalCost(int newCost) {
        if (newCost != Integer.MAX_VALUE) finalCost += newCost;
    }
}
