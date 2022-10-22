package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Node;
import me.osx11.assignment1.mobs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaribbeanMap {
    public static final int WIDTH = 9; // width of the me.osx11.assignment1.map
    public static final int HEIGHT = 9; // The height of the me.osx11.assignment1.map

    public char[][] map;

    public Node start;
    public Node end;

    public final Jack jack;
    public final Chest chest;
    public final Tortuga tortuga;
    public final Davy davy;
    public final Kraken kraken;
    public final Rock rock;
    public final List<DangerMob> dangerMobs = new ArrayList<>();

    public CaribbeanMap(Jack jack, Chest chest, Tortuga tortuga, Davy davy, Kraken kraken, Rock rock, Node start, Node end) {
        this.map = new char[9][9];

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                this.setMapCell(x, y, MapSymbol.FREE);
            }
        }

        this.jack = jack;
        this.chest = chest;
        this.tortuga = tortuga;
        this.davy = davy;
        this.kraken = kraken;
        this.rock = rock;

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

    public CaribbeanMap(CaribbeanMap caribbeanMap) {
        this(
                caribbeanMap.jack,
                caribbeanMap.chest,
                caribbeanMap.tortuga,
                caribbeanMap.davy,
                caribbeanMap.kraken,
                caribbeanMap.rock,
                new Node(caribbeanMap.start.coord.x, caribbeanMap.start.coord.y),
                new Node(caribbeanMap.end.coord.x, caribbeanMap.end.coord.y)
        );

        for (int i = 0; i < caribbeanMap.map.length; i++)
            for (int j = 0; j < caribbeanMap.map[i].length; j++)
                this.map[i][j] = caribbeanMap.map[i][j];
    }

    public void setMap(char[][] map) {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                this.map[i][j] = map[i][j];
    }

    public void removeKraken() {
        DangerMob kraken = dangerMobs.stream().filter(mob -> mob instanceof Kraken).findFirst().get();
        this.freeMob(kraken);
    }

    public void print() {
        System.out.println("  | 0 1 2 3 4 5 6 7 8");
        System.out.println("——|——————————————————");
        for (int y = 0; y < 9; y++) {
            System.out.print(y + " | ");
            for (int x = 0; x < 9; x++) {
                System.out.print(this.getMapCell(x, y) + " ");
            }
            System.out.println();
        }
    }

    private void fillMob(Mob mob) {
        this.setMapCell(mob);

        if (mob instanceof DangerMob) {
            this.fillDangerZones((DangerMob) mob);
        }
    }

    private void freeMob(Mob mob) {
        if (this.getMapCell(mob) == mob.icon)
            this.setMapCell(mob, MapSymbol.FREE);

        if (mob instanceof DangerMob) {
            this.freeDangerZones((DangerMob) mob);
        }
    }

    private void fillDangerZones(DangerMob mob) {
        mob.getDangerZones().forEach(coord -> this.setMapCell(coord.x, coord.y, MapSymbol.DANGER_ZONE));
    }

    private void freeDangerZones(DangerMob mob) {
        mob.getDangerZones().forEach(coord -> {
            if (this.getMapCell(coord.x, coord.y) == MapSymbol.DANGER_ZONE.symbol) {
                this.setMapCell(coord.x, coord.y, MapSymbol.FREE);
            }
        });
    }

    private char getMapCell(int x, int y) {
        return this.map[y][x];
    }

    private char getMapCell(Mob mob) {
        return this.getMapCell(mob.coords.x, mob.coords.y);
    }

    private void setMapCell(int x, int y, MapSymbol symbol) {
        this.map[y][x] = symbol.symbol;
    }

    private void setMapCell(int x, int y, char symbol) {
        this.map[y][x] = symbol;
    }

    private void setMapCell(Mob mob, MapSymbol symbol) {
        this.setMapCell(mob.coords.x, mob.coords.y, symbol);
    }

    private void setMapCell(Mob mob) {
        this.setMapCell(mob.coords.x, mob.coords.y, mob.icon);
    }
}
