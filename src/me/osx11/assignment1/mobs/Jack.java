package me.osx11.assignment1.mobs;

import me.osx11.assignment1.MapSymbol;
import me.osx11.assignment1.Mob;
import me.osx11.assignment1.a_star.Coord;

public class Jack extends Mob {
    public Jack(int x, int y) {
        super(new Coord(x, y), MapSymbol.JACK);
    }
}
