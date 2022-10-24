package me.osx11.assignment1;

import me.osx11.assignment1.a_star.Coord;

import java.util.List;

public interface Algorithm {
    boolean solve();

    int getFinalCost();

    void setMap(CaribbeanMap caribbeanMap);

    List<Coord> getFinalPath();
}
