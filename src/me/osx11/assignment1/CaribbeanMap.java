package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Node;

public class CaribbeanMap {
    public static final int WIDTH = 9; // width of the me.osx11.assignment1.map
    public static final int HEIGHT = 9; // The height of the me.osx11.assignment1.map

    public final char[][] map = new char[9][9];

    public Node start;
    public Node end;

    public CaribbeanMap(int[] jack, int[] davy, int[] kraken, int[] rock, int[] chest, int[] tortuga) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                this.map[y][x] = (Main.FREE);
            }
        }

        this.map[jack[1]][jack[0]] = Main.JACK;
        this.map[davy[1]][davy[0]] = Main.DAVY;
        this.map[kraken[1]][kraken[0]] = Main.KRAKEN;
        this.map[rock[1]][rock[0]] = Main.ROCK;
        this.map[chest[1]][chest[0]] = Main.CHEST;
        this.map[tortuga[1]][tortuga[0]] = Main.TORTUGA;

        try {this.map[davy[1]-1][davy[0]] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[davy[1]+1][davy[0]] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[davy[1]][davy[0]-1] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[davy[1]][davy[0]+1] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[davy[1]-1][davy[0]-1] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[davy[1]-1][davy[0]+1] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[davy[1]+1][davy[0]+1] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[davy[1]+1][davy[0]-1] = Main.DANGER_ZONE;} catch (Exception e) {}

        try {this.map[kraken[1]-1][kraken[0]] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[kraken[1]+1][kraken[0]] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[kraken[1]][kraken[0]-1] = Main.DANGER_ZONE;} catch (Exception e) {}
        try {this.map[kraken[1]][kraken[0]+1] = Main.DANGER_ZONE;} catch (Exception e) {}

        this.start = new Node(0, 0);
        this.end = new Node(chest[0], chest[1]);
    }

    public void print() {
        System.out.println("  | 0 1 2 3 4 5 6 7 8");
        System.out.println("--|------------------");
        for (int y = 0; y < 9; y++) {
            System.out.print(y + " | ");
            for (int x = 0; x < 9; x++) {
                System.out.print(this.map[y][x] + " ");
            }
            System.out.println();
        }
    }
}
