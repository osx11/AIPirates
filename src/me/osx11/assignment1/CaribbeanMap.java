package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Node;

import java.util.ArrayList;
import java.util.List;

public class CaribbeanMap {
    public static final int WIDTH = 9; // width of the me.osx11.assignment1.map
    public static final int HEIGHT = 9; // The height of the me.osx11.assignment1.map

    public final char[][] map = new char[9][9];

    public Node start;
    public Node end;
    public boolean gainedRum = false;

    public final Mob jack;
    public final Mob chest;
    public final Mob tortuga;
    public final List<DangerMob> dangerMobs = new ArrayList<>();

    public CaribbeanMap(Mob jack, Mob chest, Mob tortuga, DangerMob davy, DangerMob kraken, DangerMob rock, Node start, Node end) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                this.map[y][x] = (MapSymbol.FREE.symbol);
            }
        }

        this.jack = jack;
        this.chest = chest;
        this.tortuga = tortuga;

        this.dangerMobs.add(davy);
        this.dangerMobs.add(kraken);
        this.dangerMobs.add(rock);

        this.fillMob(jack);
        this.fillMob(chest);
        this.fillMob(tortuga);

        this.dangerMobs.forEach(this::fillMob);

        this.start = start;
        this.end = end;
    }

//    public void removeKraken() {
//        this.map[kraken[1]][kraken[0]] = Main.FREE;
//
//        try {this.map[kraken[1]-1][kraken[0]] = Main.FREE;} catch (Exception e) {}
//        try {this.map[kraken[1]+1][kraken[0]] = Main.FREE;} catch (Exception e) {}
//        try {this.map[kraken[1]][kraken[0]-1] = Main.FREE;} catch (Exception e) {}
//        try {this.map[kraken[1]][kraken[0]+1] = Main.FREE;} catch (Exception e) {}
//    }

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

    private void fillMob(Mob mob) {
        this.map[mob.coords.y][mob.coords.x] = mob.icon;

        if (mob instanceof DangerMob) {
            this.fillDangers((DangerMob) mob);
        }
    }

    private void fillDangers(DangerMob danger) {
        if (!danger.hasDangerZones()) return;
        danger.getDangerZones().forEach(coord -> this.map[coord.y][coord.x] = MapSymbol.DANGER_ZONE.symbol);
    }
}
