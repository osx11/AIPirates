package me.osx11.assignment1;

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
    public static final int DIAGONAL_COST = 1; // Diagonal movement cost

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

            aStar.reset();
            result = aStar.start();

            if (!result) {
                System.out.println("LOSE (Tortuga unreachable)");
                map.print();
                return;
            }

            recalculateFinalCost(aStar.getFinalCost());

            map.start = new Node(tortuga[0], tortuga[1]);
            map.end = new Node(chest[0], chest[1]);
            map.gainedRum = true;

            aStar.reset();
            result = aStar.start();
            recalculateFinalCost(aStar.getFinalCost());

            map.removeKraken();

            if (!result) {
                System.out.println("LOSE (reached Tortuga and tried to kill Kraken, but something still blocks the chest)");
            }
        }

        System.out.println("Final cost " + finalCost + "\n");
        map.print();
    }

    private static void recalculateFinalCost(int newCost) {
        if (newCost != Integer.MAX_VALUE) finalCost += newCost;
    }
}
