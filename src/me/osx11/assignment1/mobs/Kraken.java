package me.osx11.assignment1.mobs;

import me.osx11.assignment1.DangerMob;
import me.osx11.assignment1.MapSymbol;
import me.osx11.assignment1.a_star.Coord;

public class Kraken extends DangerMob {
    public Kraken(int x, int y) {
        super(new Coord(x, y), MapSymbol.KRAKEN);
        super.setDirectDangerZones();
    }
}
