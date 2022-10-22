package me.osx11.assignment1.mobs;

import me.osx11.assignment1.DangerMob;
import me.osx11.assignment1.MapSymbol;
import me.osx11.assignment1.a_star.Coord;

public class Davy extends DangerMob {
    public Davy(int x, int y) {
        super(new Coord(x, y), MapSymbol.DAVY);

        super.setDiagonalDangerZones();
        super.setDirectDangerZones();
    }
}
