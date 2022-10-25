package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Coord;
import me.osx11.assignment1.a_star.Node;
import me.osx11.assignment1.a_star.AStar;
import me.osx11.assignment1.backtracking.Backtracking;
import me.osx11.assignment1.mobs.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static int[] jack = {0, 0};
    private static int[] davy = {6, 6};
    private static int[] kraken = {7, 1};
    private static int[] rock = {8, 7};
    private static int[] chest = {8, 0};
    private static int[] tortuga = {8, 8};

    private static int finalCost = 0;
    private static List<Coord> path = new ArrayList<>();

    public static void main(String[] args) {
        parseInput();

        Algorithm algorithm = new AStar();
        CaribbeanMap caribbeanMap = generateMap();

        long startTime = System.nanoTime();
        solve(algorithm, caribbeanMap);
        long endTime = System.nanoTime();

        saveExecutionTime(algorithm, ((float)(endTime - startTime)) / 1000000);

        algorithm = new Backtracking();
        caribbeanMap = generateMap();

        startTime = System.nanoTime();
        solve(algorithm, caribbeanMap);
        endTime = System.nanoTime();

        saveExecutionTime(algorithm, ((float)(endTime - startTime)) / 1000000);
    }

    private static CaribbeanMap generateMap() {
        return new CaribbeanMap(
                new Jack(jack[0], jack[1]),
                new Chest(chest[0], chest[1]),
                new Tortuga(tortuga[0], tortuga[1]),
                new Davy(davy[0], davy[1]),
                new Kraken(kraken[0], kraken[1]),
                new Rock(rock[0], rock[1]),
                new Node(jack[0], jack[1]),
                new Node(chest[0], chest[1])
        );
    }

    private static void solve(Algorithm algorithm, CaribbeanMap caribbeanMap) {
        finalCost = 0;
        path = new ArrayList<>();

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
                System.out.println("Lose");
                System.out.println("Tortuga unreachable");

                reportLose(algorithm);
                caribbeanMap.print();
                return;
            }

            recalculateFinalCost(algorithm.getFinalCost());
            path.addAll(algorithm.getFinalPath());

            int tortugaToChestCost = findLeastKrakenKillPath(algorithm, caribbeanMap);

            if (tortugaToChestCost < Integer.MAX_VALUE) {
                System.out.println("Kraken was killed. Going to chest");
                recalculateFinalCost(tortugaToChestCost);
            } else {
                System.out.println("Lose");
                System.out.println("Cannot kill Kraken or reach chest after it");

                reportLose(algorithm);
                return;
            }
        } else path.addAll(algorithm.getFinalPath());

        System.out.println();
        System.out.println("Win");
        System.out.println("Final cost is " + finalCost);
        System.out.println("Note: In the below map @ denotes the path.");
        System.out.println("If the route passes through mob, obstacle, etc., this object is replaced by @");
        System.out.println("If Kraken was killed, it and it's danger zones are removed from map");
        System.out.println();
        caribbeanMap.print();
        printPath();

        reportWin(algorithm);
    }

    private static int findLeastKrakenKillPath(Algorithm algorithm, CaribbeanMap caribbeanMap) {
        Node[] possibleEnds = {
                new Node(kraken[0]+1, kraken[1]+1),
                new Node(kraken[0]-1, kraken[1]-1),
                new Node(kraken[0]-1, kraken[1]+1),
                new Node(kraken[0]+1, kraken[1]-1),
        };

        int leastCost = Integer.MAX_VALUE;
        char[][] leastCostMap = new char[9][9];
        List<Coord> leastCostPath = null;

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
                List<Coord> path = algorithm.getFinalPath();

                int cost = algorithm.getFinalCost();

                mapCopy.start = new Node(possibleEnds[i].coord.x, possibleEnds[i].coord.y);
                mapCopy.end = new Node(chest[0], chest[1]);
                mapCopy.removeKraken();

                result = algorithm.solve();

                if (result) {
                    cost += algorithm.getFinalCost();

                    if (cost < leastCost) {
                        path.addAll(algorithm.getFinalPath());
                        leastCostPath = new ArrayList<>(path);

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
            path.addAll(leastCostPath);
        } else {
            System.out.println("CHEST UNREACHABLE");
        }

        return leastCost;
    }

    private static void recalculateFinalCost(int newCost) {
        if (newCost != Integer.MAX_VALUE) finalCost += newCost;
    }

    private static void printPath() {
        System.out.println(getFormattedPath());
    }

    private static String getFormattedPath() {
        StringBuilder stringBuilder = new StringBuilder();

        path.forEach(c -> {
            stringBuilder.append(c.getFormatted());
            stringBuilder.append(" ");
        });

        return stringBuilder.toString();
    }

    private static void parseInput() {
        try(BufferedReader bf = new BufferedReader(new FileReader("input.txt"))) {
            String data = bf.readLine();
            String[] dataSplit = data.split(" ");

            for (int i = 0; i < dataSplit.length; i++) {
                String d = dataSplit[i];
                d = d.replace("[", "");
                d = d.replace("]", "");

                int x = Integer.parseInt(d.split(",")[0]);
                int y = Integer.parseInt(d.split(",")[1]);

                switch (i) {
                    case 0:
                        jack = new int[]{x, y};
                    case 1:
                        davy = new int[]{x, y};
                    case 2:
                        kraken = new int[]{x, y};
                    case 3:
                        rock = new int[]{x, y};
                    case 4:
                        chest = new int[]{x, y};
                    case 5:
                        tortuga = new int[]{x, y};
                }
            }
        } catch (IOException e) {}
    }

    private static void reportLose(Algorithm algorithm) {
        try (PrintWriter printWriter = new PrintWriter(algorithm instanceof AStar ? "outputAStar.txt" : "outputBacktracking.txt")) {
            printWriter.println("Lose");
        } catch (IOException e) {}
    }

    private static void reportWin(Algorithm algorithm) {
        try (PrintWriter printWriter = new PrintWriter(algorithm instanceof AStar ? "outputAStar.txt" : "outputBacktracking.txt")) {
            printWriter.println("Win");
            printWriter.println(finalCost);
            printWriter.println(getFormattedPath());
        } catch (IOException e) {}
    }

    private static void saveExecutionTime(Algorithm algorithm, float executionTime) {
        try (PrintWriter printWriter = new PrintWriter(
                new FileOutputStream(
                        algorithm instanceof AStar ? "outputAStar.txt" : "outputBacktracking.txt",
                        true
                )
            )
        ) {
            printWriter.println(executionTime + "ms");
        } catch (IOException e) {}
    }
}
