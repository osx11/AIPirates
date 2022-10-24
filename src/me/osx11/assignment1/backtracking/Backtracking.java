package me.osx11.assignment1.backtracking;

import me.osx11.assignment1.Algorithm;
import me.osx11.assignment1.CaribbeanMap;
import me.osx11.assignment1.MapSymbol;
import me.osx11.assignment1.a_star.Coord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Backtracking implements Algorithm {

    // Size of the maze
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

    /* A utility function to check
        if x, y is valid index for 9*9 maze */
    boolean isSafe(int x, int y) {
        if (x < 0 || x >= CaribbeanMap.WIDTH || y < 0 || y >= CaribbeanMap.HEIGHT) return false;

        char currentCell = this.caribbeanMap.map[y][x];

        boolean dangerInCell = this.caribbeanMap.dangerMobs
                .stream()
                .anyMatch(mob -> currentCell == mob.icon) || currentCell == MapSymbol.DANGER_ZONE.symbol;

        return !dangerInCell;
    }

    /* This function solves the Maze problem using
    Backtracking. It mainly uses solveMazeUtil()
    to solve the problem. It returns false if no
    path is possible, otherwise return true and
    prints the path in the form of 1s. Please note
    that there may be more than one solutions, this
    function prints one of the feasible solutions.*/
    char[][] solveMaze(Sign[][] signs) {
        this.cost = 0;

        char[][] sol = new char[CaribbeanMap.HEIGHT][CaribbeanMap.WIDTH];

        if (!solveMazeUtil(this.caribbeanMap.start.coord.x, this.caribbeanMap.start.coord.y, sol, signs) || this.cost >= 25) {
            this.cost = Integer.MAX_VALUE;
            return null;
        }

        return sol;
    }

    /* A recursive utility function to solve Maze
    problem */
    boolean solveMazeUtil(int x, int y, char[][] sol, Sign[][] signs) {
        if (this.cost >= this.leastCost || this.cost >= 25) return true;

        // if (x, y is goal) return true
        if (x == this.caribbeanMap.end.coord.x && y == this.caribbeanMap.end.coord.y) {
            sol[y][x] = MapSymbol.FREE.symbol;

            Coord coord = new Coord(x, y);

            if (this.path.stream().noneMatch(c -> c.equals(coord)))
                this.path.add(coord);

            return true;
        }

        // Check if maze[x][y] is valid
        if (isSafe(x, y)) {
            // Check if the current block is already part of solution path.
            if (sol[y][x] == MapSymbol.FREE.symbol)
                return false;

            // mark x, y as part of solution path
            this.cost++;

            Coord coord = new Coord(x, y);

            if (!coord.equals(this.caribbeanMap.start.coord)) {
                sol[y][x] = MapSymbol.FREE.symbol;
                this.path.add(coord);

            }

            if (solveMazeUtil(Sign.calculate(x, 1, signs[0][0]), Sign.calculate(y, 1, signs[0][1]), sol, signs))
                return true;

            if (solveMazeUtil(Sign.calculate(x, 1, signs[1][0]), Sign.calculate(y, 1, signs[1][1]), sol, signs))
                return true;

            if (solveMazeUtil(Sign.calculate(x, 1, signs[2][0]), Sign.calculate(y, 1, signs[2][1]), sol, signs))
                return true;

            if (solveMazeUtil(Sign.calculate(x, 1, signs[3][0]), Sign.calculate(y, 1, signs[3][1]), sol, signs))
                return true;

            if (solveMazeUtil(Sign.calculate(x, 1, signs[4][0]), y, sol, signs))
                return true;

            if (solveMazeUtil(x, Sign.calculate(y, 1, signs[5][1]), sol, signs))
                return true;

            if (solveMazeUtil(Sign.calculate(x, 1, signs[6][0]), y, sol, signs))
                return true;

            if (solveMazeUtil(x, Sign.calculate(y, 1, signs[7][1]), sol, signs))
                return true;

			/* If none of the above movements works then
			BACKTRACK: unmark x, y as part of solution
			path */
            sol[y][x] = '#';
            this.cost--;
            this.path.remove(coord);

            return false;
        }

        return false;
    }

    public boolean solve() {
        // FIXME if uncomment it will crash
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

    private void solveRecursive(int n, Sign[][] elements) {
        if (n == 1) {
            Backtracking backTracking = new Backtracking(this.leastCost);

            CaribbeanMap mapCopy = new CaribbeanMap(this.caribbeanMap);
            backTracking.setMap(mapCopy);

            char[][] result = backTracking.solveMaze(elements);

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

    public List<Coord> getFinalPath() {
        List<Coord> pathCopy = new ArrayList<>(this.path);

        return pathCopy;
    }
}
