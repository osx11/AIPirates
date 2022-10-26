package me.osx11.assignment1.backtracking;

import me.osx11.assignment1.Algorithm;
import me.osx11.assignment1.CaribbeanMap;
import me.osx11.assignment1.MapSymbol;
import me.osx11.assignment1.a_star.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Backtracking implements Algorithm {

    private int cost = 0;
    private int leastCost = Integer.MAX_VALUE;
    private char[][] leastMap = new char[CaribbeanMap.HEIGHT][CaribbeanMap.WIDTH];
    private CaribbeanMap caribbeanMap;
    private List<Coord> path = new ArrayList<>();

    public Backtracking() {}

    public Backtracking(int leastCost) {
        this.leastCost = leastCost;
    }

    public void setMap(CaribbeanMap caribbeanMap) {
        this.caribbeanMap = caribbeanMap;

        for (int y = 0; y < CaribbeanMap.HEIGHT; y++) {
            for (int x = 0; x < CaribbeanMap.WIDTH; x++) {
                this.leastMap[y][x] = caribbeanMap.map[y][x];
            }
        }
    }

    public int getFinalCost() {
        return this.leastCost;
    }

    /**
     *
     * Determine if the node contains danger
     * @param x x coordinate
     * @param y y coordinate
     * @return true is there is no danger in the cell, otherwise false
     */
    boolean isSafe(int x, int y) {
        if (x < 0 || x >= CaribbeanMap.WIDTH || y < 0 || y >= CaribbeanMap.HEIGHT) return false;

        char currentCell = this.caribbeanMap.map[y][x];

        boolean dangerInCell = this.caribbeanMap.dangerMobs
                .stream()
                .anyMatch(mob -> currentCell == mob.icon) || currentCell == MapSymbol.DANGER_ZONE.symbol;

        return !dangerInCell;
    }

    /**
     * Solves the task using recursive method Backtracking#solveMapUtil
     * @param signs list of permutation of signs (see Backtracking#solveMapUtil method)
     * @return solution map
     */
    char[][] solveMap(Sign[][] signs) {
        this.cost = 0;

        char[][] sol = new char[CaribbeanMap.HEIGHT][CaribbeanMap.WIDTH];

        if (!solveMapUtil(this.caribbeanMap.start.coord.x, this.caribbeanMap.start.coord.y, sol, signs) || this.cost >= 25) {
            this.cost = Integer.MAX_VALUE;
            return null;
        }

        return sol;
    }

    /**
     * Recursively solve the map. Check where the next coordinates are ok and add it to the final path, otherwise backtrack
     * @param x x coordinate of current position
     * @param y y coordinate of current position
     * @param sol 2d array with solution map
     * @param signs permutations of signs. It is needed to find all the solutions and not the any one.
     * @return true if the cell has been added, otherwise false
     */
    boolean solveMapUtil(int x, int y, char[][] sol, Sign[][] signs) {
        if (this.cost >= this.leastCost || this.cost >= 25) return true;

        // if (x, y is goal) return true
        if (x == this.caribbeanMap.end.coord.x && y == this.caribbeanMap.end.coord.y) {
            sol[y][x] = MapSymbol.FREE.symbol;

            Coord coord = new Coord(x, y);

            if (this.path.stream().noneMatch(c -> c.equals(coord)))
                this.path.add(coord);

            return true;
        }

        if (isSafe(x, y)) {
            if (sol[y][x] == MapSymbol.FREE.symbol)
                return false;

            this.cost++;

            Coord coord = new Coord(x, y);

            if (!coord.equals(this.caribbeanMap.start.coord)) {
                sol[y][x] = MapSymbol.FREE.symbol;
                this.path.add(coord);

            }

            if (solveMapUtil(Sign.calculate(x, 1, signs[0][0]), Sign.calculate(y, 1, signs[0][1]), sol, signs))
                return true;

            if (solveMapUtil(Sign.calculate(x, 1, signs[1][0]), Sign.calculate(y, 1, signs[1][1]), sol, signs))
                return true;

            if (solveMapUtil(Sign.calculate(x, 1, signs[2][0]), Sign.calculate(y, 1, signs[2][1]), sol, signs))
                return true;

            if (solveMapUtil(Sign.calculate(x, 1, signs[3][0]), Sign.calculate(y, 1, signs[3][1]), sol, signs))
                return true;

            if (solveMapUtil(Sign.calculate(x, 1, signs[4][0]), y, sol, signs))
                return true;

            if (solveMapUtil(x, Sign.calculate(y, 1, signs[5][1]), sol, signs))
                return true;

            if (solveMapUtil(Sign.calculate(x, 1, signs[6][0]), y, sol, signs))
                return true;

            if (solveMapUtil(x, Sign.calculate(y, 1, signs[7][1]), sol, signs))
                return true;

            // backtrack
            sol[y][x] = '#';
            this.cost--;
            this.path.remove(coord);

            return false;
        }

        return false;
    }

    /**
     * Common method to solve the task
     */
    public boolean solve() {
        this.cost = 0;
        this.leastCost = Integer.MAX_VALUE;
        this.leastMap = new char[CaribbeanMap.HEIGHT][CaribbeanMap.WIDTH];
        this.path = new ArrayList<>();

        Sign[][] signs = {
                {Sign.PLUS, Sign.PLUS},
                {Sign.MINUS, Sign.MINUS},
                {Sign.PLUS, Sign.MINUS},
                {Sign.MINUS, Sign.PLUS},
                {Sign.NOTHING, Sign.PLUS},
                {Sign.NOTHING, Sign.MINUS},
                {Sign.PLUS, Sign.NOTHING},
                {Sign.MINUS, Sign.NOTHING}
        };

        this.solveRecursive(signs.length, signs);

        if (this.leastCost != Integer.MAX_VALUE) {
            this.caribbeanMap.setMap(this.leastMap);
            return true;
        }

        return false;
    }

    /**
     * Find all the permutation of signs and runs Backtracking#solveMap method
     * @param n number of signs
     * @param elements signs
     */
    private void solveRecursive(int n, Sign[][] elements) {
        if (n == 1) {
            Backtracking backTracking = new Backtracking(this.leastCost);

            CaribbeanMap mapCopy = new CaribbeanMap(this.caribbeanMap);
            backTracking.setMap(mapCopy);

            char[][] result = backTracking.solveMap(elements);

            if (backTracking.cost < this.leastCost && result != null) {
                this.leastCost = backTracking.cost;

                this.setMap(caribbeanMap);

                for (int y = 0; y < CaribbeanMap.HEIGHT; y++) {
                    for (int x = 0; x < CaribbeanMap.WIDTH; x++) {
                        if (result[y][x] == MapSymbol.FREE.symbol)
                            this.leastMap[y][x] = MapSymbol.PATH.symbol;
                    }
                }

                this.path = backTracking.getFinalPath();
            }
        } else {
            for(int i = 0; i < n-1; i++) {
                this.solveRecursive(n - 1, elements);

                if(n % 2 == 0) {
                    this.swap(elements, i, n-1);
                } else {
                    this.swap(elements, 0, n-1);
                }
            }
            this.solveRecursive(n - 1, elements);
        }
    }

    private void swap(Sign[][] input, int a, int b) {
        Sign[] tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }


    /**
     * @return array of coordinates with path
     */
    public List<Coord> getFinalPath() {
        List<Coord> pathCopy = new ArrayList<>(this.path);

        return pathCopy;
    }
}
