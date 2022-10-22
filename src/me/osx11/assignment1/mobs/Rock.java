package me.osx11.assignment1.mobs;

import me.osx11.assignment1.DangerMob;
import me.osx11.assignment1.MapSymbol;
import me.osx11.assignment1.Mob;
import me.osx11.assignment1.a_star.Coord;

import java.util.List;

public class Rock extends DangerMob {
    public Rock(int x, int y) {
        super(new Coord(x, y), MapSymbol.ROCK);
    }
}
