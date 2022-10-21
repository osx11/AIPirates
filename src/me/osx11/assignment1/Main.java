package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Node;
import me.osx11.assignment1.a_star.Utils;

import java.util.*;

public class Main {
    private static final int[] jack = {0, 2};
    private static final int[] davy = {4, 7};
    private static final int[] kraken = {7, 6};
    private static final int[] rock = {6, 4};
    private static final int[] chest = {8, 7};
    private static final int[] tortuga = {0, 6};

    public static final char FREE = '·';
    public static final char JACK = 'J';
    public static final char DAVY = 'D';
    public static final char KRAKEN = 'K';
    public static final char ROCK = 'R';
    public static final char CHEST = 'C';
    public static final char TORTUGA = 'T';
    public static final char DANGER_ZONE = '#';
    public static final char PATH = '*';
    public static final int DIRECT_VALUE = 1; // horizontal and vertical movement cost
    public static final int OBLIQUE_VALUE = 1; // Oblique movement cost

    public static void main(String[] args) {
        CaribbeanMap map = new CaribbeanMap(jack, davy, kraken, rock, chest, tortuga);

        Queue<Node> openList = new PriorityQueue<>();
        List<Node> closeList = new ArrayList<>();

        Utils.start(closeList, openList, map);
    }
}
