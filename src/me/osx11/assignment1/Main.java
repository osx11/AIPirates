package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Node;
import me.osx11.assignment1.a_star.Utils;
import me.osx11.assignment1.mobs.*;

import java.util.*;

public class Main {
    private static final int[] jack = {0, 2};
    private static final int[] davy = {4, 7};
    private static final int[] kraken = {7, 6};
    private static final int[] rock = {8, 7};
    private static final int[] chest = {8, 8};
    private static final int[] tortuga = {0, 6};

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

        Queue<Node> openList = new PriorityQueue<>();
        List<Node> closeList = new ArrayList<>();

        boolean result = Utils.start(closeList, openList, map);
        recalculateFinalCost();

        if (!result) {
            map.end = new Node(tortuga[0], tortuga[1]);

            openList = new PriorityQueue<>();
            closeList = new ArrayList<>();
            result = Utils.start(closeList, openList, map);

            if (!result) {
                System.out.println("LOSE (Tortuga unreachable)");
                map.print();
                return;
            }

            recalculateFinalCost();

            map.start = new Node(tortuga[0], tortuga[1]);
            map.end = new Node(chest[0], chest[1]);
            map.gainedRum = true;
//            map.removeKraken();

            openList = new PriorityQueue<>();
            closeList = new ArrayList<>();

            result = Utils.start(closeList, openList, map);
            recalculateFinalCost();

            if (!result) {
                System.out.println("LOSE (reached Tortuga and tried to kill Kraken, but something still blocks the chest)");
            }
        }

        System.out.println("Final cost " + finalCost);
        map.print();
    }

    private static void recalculateFinalCost() {
        int newCost = Utils.getFinalCost();
        if (newCost != Integer.MAX_VALUE) finalCost += newCost;
    }
}
