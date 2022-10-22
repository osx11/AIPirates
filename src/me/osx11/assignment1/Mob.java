package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Coord;

public abstract class Mob {
    public final Coord coords;
    public final char icon;

    public Mob(Coord coords, MapSymbol symbol) {
        this.coords = coords;
        this.icon = symbol.symbol;
    }
}
