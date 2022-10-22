package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Coord;

import java.util.ArrayList;
import java.util.List;

public class DangerMob extends Mob {
    public final List<Coord> dangerZones = new ArrayList<>();

    public DangerMob(Coord coords, MapSymbol symbol) {
        super(coords, symbol);
    }

    public List<Coord> getDangerZones() {
        return this.dangerZones;
    }

    public boolean hasDangerZones() {
        return this.dangerZones.size() > 0;
    }

    protected void setDirectDangerZones() {
        if (super.coords.x + 1 < 9) this.dangerZones.add(new Coord(super.coords.x+1, super.coords.y));
        if (super.coords.x - 1 > 0) this.dangerZones.add(new Coord(super.coords.x-1, super.coords.y));
        if (super.coords.y + 1 < 9) this.dangerZones.add(new Coord(super.coords.x, super.coords.y+1));
        if (super.coords.y - 1 > 0) this.dangerZones.add(new Coord(super.coords.x, super.coords.y-1));
    }

    protected void setDiagonalDangerZones() {
        if (super.coords.x - 1 > 0 && super.coords.y + 1 < 9)
            this.dangerZones.add(new Coord(super.coords.x-1, super.coords.y+1));

        if (super.coords.x - 1 > 0 && super.coords.y - 1 > 0)
            this.dangerZones.add(new Coord(super.coords.x-1, super.coords.y-1));

        if (super.coords.x + 1 < 9 && super.coords.y + 1 < 9)
            this.dangerZones.add(new Coord(super.coords.x+1, super.coords.y+1));

        if (super.coords.x + 1 < 9 && super.coords.y - 1 > 0)
            this.dangerZones.add(new Coord(super.coords.x+1, super.coords.y-1));
    }
}
