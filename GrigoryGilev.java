import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static int[] jack = {0, 0};
    private static int[] davy = {6, 6};
    private static int[] kraken = {7, 1};
    private static int[] rock = {8, 7};
    private static int[] chest = {8, 0};
    private static int[] tortuga = {8, 8};

    private static int finalCost = 0;
    private static List<Coord> path = new ArrayList<>();

    public static void main(String[] args) {
        parseInput();

        Algorithm algorithm = new AStar();
        CaribbeanMap caribbeanMap = generateMap();

        long startTime = System.nanoTime();
        solve(algorithm, caribbeanMap, true);
        long endTime = System.nanoTime();

        saveExecutionTime(algorithm, ((float)(endTime - startTime)) / 1000000);

        algorithm = new Backtracking();
        caribbeanMap = generateMap();

        startTime = System.nanoTime();
        solve(algorithm, caribbeanMap, false);
        endTime = System.nanoTime();

        saveExecutionTime(algorithm, ((float)(endTime - startTime)) / 1000000);
    }

    private static CaribbeanMap generateMap() {
        return new CaribbeanMap(
                new Jack(jack[0], jack[1]),
                new Chest(chest[0], chest[1]),
                new Tortuga(tortuga[0], tortuga[1]),
                new Davy(davy[0], davy[1]),
                new Kraken(kraken[0], kraken[1]),
                new Rock(rock[0], rock[1]),
                new Node(jack[0], jack[1]),
                new Node(chest[0], chest[1])
        );
    }

    private static void solve(Algorithm algorithm, CaribbeanMap caribbeanMap, boolean printDefaultMap) {
        finalCost = 0;
        path = new ArrayList<>();

        algorithm.setMap(caribbeanMap);

        if (printDefaultMap)
            caribbeanMap.print();
        System.out.println();

        boolean result = algorithm.solve();
        recalculateFinalCost(algorithm.getFinalCost());

        if (!result) {
            System.out.println("Chest is blocked. Trying to get to Tortuga first");
            caribbeanMap.end = new Node(tortuga[0], tortuga[1]);

            result = algorithm.solve();

            if (!result) {
                System.out.println("Lose");
                System.out.println("Tortuga unreachable");

                reportLose(algorithm);
                caribbeanMap.print();
                return;
            }

            recalculateFinalCost(algorithm.getFinalCost());
            path.addAll(algorithm.getFinalPath());

            int tortugaToChestCost = findLeastKrakenKillPath(algorithm, caribbeanMap);

            if (tortugaToChestCost < Integer.MAX_VALUE) {
                System.out.println("Kraken was killed. Going to chest");
                recalculateFinalCost(tortugaToChestCost);
            } else {
                System.out.println("Lose");
                System.out.println("Cannot kill Kraken or reach chest after it");

                reportLose(algorithm);
                return;
            }
        } else path.addAll(algorithm.getFinalPath());

        System.out.println();
        System.out.println("Win");
        System.out.println("Final cost is " + finalCost);
        System.out.println("Note: In the below map @ denotes the path.");
        System.out.println("If the route passes through mob, obstacle, etc., this object is replaced by @");
        System.out.println("If Kraken was killed, it and it's danger zones are removed from map");
        System.out.println();
        caribbeanMap.print();
        printPath();

        reportWin(algorithm);
    }

    private static int findLeastKrakenKillPath(Algorithm algorithm, CaribbeanMap caribbeanMap) {
        Node[] possibleEnds = {
                new Node(kraken[0]+1, kraken[1]+1),
                new Node(kraken[0]-1, kraken[1]-1),
                new Node(kraken[0]-1, kraken[1]+1),
                new Node(kraken[0]+1, kraken[1]-1),
        };

        int leastCost = Integer.MAX_VALUE;
        char[][] leastCostMap = new char[9][9];
        List<Coord> leastCostPath = null;

        for (int i = 0; i < 4; i++) {
            if (possibleEnds[i].coord.x > 8 || possibleEnds[i].coord.x < 0 || possibleEnds[i].coord.y > 8 || possibleEnds[i].coord.y < 0) continue;
            if (possibleEnds[i].coord.equals(new Coord(chest[0], chest[1]))) continue;

            char startCell = caribbeanMap.map[possibleEnds[i].coord.y][possibleEnds[i].coord.x];
            if (startCell != MapSymbol.FREE.symbol && startCell != MapSymbol.CHEST.symbol && startCell != MapSymbol.PATH.symbol) {
                continue;
            }

            CaribbeanMap mapCopy = new CaribbeanMap(caribbeanMap);
            mapCopy.start = new Node(tortuga[0], tortuga[1]);
            mapCopy.end = possibleEnds[i];

            algorithm.setMap(mapCopy);
            boolean result = algorithm.solve();

            if (result) {
                List<Coord> path = algorithm.getFinalPath();

                int cost = algorithm.getFinalCost();

                mapCopy.start = new Node(possibleEnds[i].coord.x, possibleEnds[i].coord.y);
                mapCopy.end = new Node(chest[0], chest[1]);
                mapCopy.removeKraken();

                result = algorithm.solve();

                if (result) {
                    cost += algorithm.getFinalCost();

                    if (cost < leastCost) {
                        path.addAll(algorithm.getFinalPath());
                        leastCostPath = new ArrayList<>(path);

                        leastCost = cost;

                        for (int j = 0; j < mapCopy.map.length; j++)
                            for (int k = 0; k < mapCopy.map[j].length; k++)
                                leastCostMap[j][k] = mapCopy.map[j][k];
                    }
                }
            }
        }

        if (leastCost < Integer.MAX_VALUE) {
            caribbeanMap.setMap(leastCostMap);
            path.addAll(leastCostPath);
        } else {
            System.out.println("CHEST UNREACHABLE");
        }

        return leastCost;
    }

    private static void recalculateFinalCost(int newCost) {
        if (newCost != Integer.MAX_VALUE) finalCost += newCost;
    }

    private static void printPath() {
        System.out.println(getFormattedPath());
    }

    private static String getFormattedPath() {
        StringBuilder stringBuilder = new StringBuilder();

        path.forEach(c -> {
            stringBuilder.append(c.getFormatted());
            stringBuilder.append(" ");
        });

        return stringBuilder.toString();
    }

    private static void parseInput() {
        try(BufferedReader bf = new BufferedReader(new FileReader("input.txt"))) {
            String data = bf.readLine();
            String[] dataSplit = data.split(" ");

            for (int i = 0; i < dataSplit.length; i++) {
                String d = dataSplit[i];
                d = d.replace("[", "");
                d = d.replace("]", "");

                int x = Integer.parseInt(d.split(",")[0]);
                int y = Integer.parseInt(d.split(",")[1]);

                switch (i) {
                    case 0:
                        jack = new int[]{x, y};
                    case 1:
                        davy = new int[]{x, y};
                    case 2:
                        kraken = new int[]{x, y};
                    case 3:
                        rock = new int[]{x, y};
                    case 4:
                        chest = new int[]{x, y};
                    case 5:
                        tortuga = new int[]{x, y};
                }
            }
        } catch (IOException e) {}
    }

    private static void reportLose(Algorithm algorithm) {
        try (PrintWriter printWriter = new PrintWriter(algorithm instanceof AStar ? "outputAStar.txt" : "outputBacktracking.txt")) {
            printWriter.println("Lose");
        } catch (IOException e) {}
    }

    private static void reportWin(Algorithm algorithm) {
        try (PrintWriter printWriter = new PrintWriter(algorithm instanceof AStar ? "outputAStar.txt" : "outputBacktracking.txt")) {
            printWriter.println("Win");
            printWriter.println(finalCost);
            printWriter.println(getFormattedPath());
        } catch (IOException e) {}
    }

    private static void saveExecutionTime(Algorithm algorithm, float executionTime) {
        try (PrintWriter printWriter = new PrintWriter(
                new FileOutputStream(
                        algorithm instanceof AStar ? "outputAStar.txt" : "outputBacktracking.txt",
                        true
                )
        )
        ) {
            printWriter.println(executionTime + "ms");
        } catch (IOException e) {}
    }
}

public class AStar implements Algorithm {
    private int finalCost;
    private final Queue<Node> openList;
    private final List<Node> closeList;
    private CaribbeanMap caribbeanMap;
    private List<Coord> path = new ArrayList<>();

    public AStar() {
        this.openList = new PriorityQueue<>();
        this.closeList = new ArrayList<>();
        this.finalCost = 0;
    }

    public void setMap(CaribbeanMap caribbeanMap) {
        this.caribbeanMap = caribbeanMap;
    }

    public int getFinalCost() { return finalCost; }

    /**
     * Determine if the node contains danger to determine if it can be placed in Open List
     * @param x x coordinate
     * @param y y coordinate
     * @return true if coordinate can be added to open list (means there is not danger in the cell), otherwise false
     */
    public boolean canAddNodeToOpen(int x, int y) {
        if (x < 0 || x >= CaribbeanMap.WIDTH || y < 0 || y >= CaribbeanMap.HEIGHT) return false;
        // Determine whether it is an unpassable node

        char currentCell = this.caribbeanMap.map[y][x];

        boolean dangerInCell = this.caribbeanMap.dangerMobs
                .stream()
                .anyMatch(mob -> currentCell == mob.icon) || currentCell == MapSymbol.DANGER_ZONE.symbol;

        if (dangerInCell) return false;

        // Determine whether the node has a close table
        if (isCoordInClose(x, y)) return false;

        return true;
    }

    /**
     * Determine if the coordinate is in close list
     * @param coord coordinate to check
     * @return true if coordinate is in close list, otherwise false
     */
    public boolean isCoordInClose(Coord coord) {
        return coord != null && isCoordInClose(coord.x, coord.y);
    }

    /**
     * Determine if the coordinate is in close list
     * @param x x coordinate to check
     * @param y y coordinate to check
     * @return true if coordinate is in close list, otherwise false
     */
    public boolean isCoordInClose(int x, int y) {
        if (this.closeList.isEmpty()) return false;
        for (Node node : this.closeList)
        {
            if (node.coord.x == x && node.coord.y == y)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates heuristic distance between two coordinates
     * @param end first coordinate
     * @param coord second coordinate
     * @return heuristic distance
     */
    public int calcH(Coord end, Coord coord) {
        return Math.abs(end.x - coord.x) + Math.abs(end.y - coord.y);
    }

    public Node findNodeInOpen(Coord coord)
    {
        if (coord == null || this.openList.isEmpty()) return null;
        for (Node node : this.openList)
        {
            if (node.coord.equals(coord))
            {
                return node;
            }
        }
        return null;
    }

    /**
     * Add all neighbor nodes (left, up, right, down, top left, top right, bottom left, bottom right) to the open table
     * @param current current node
     */
    public void addNeighborNodeInOpen(Node current)
    {
        int x = current.coord.x;
        int y = current.coord.y;

        addNeighborNodeInOpen(current, x - 1, y);
        addNeighborNodeInOpen(current, x, y - 1);
        addNeighborNodeInOpen(current, x + 1, y);
        addNeighborNodeInOpen(current, x, y + 1);
        addNeighborNodeInOpen(current, x - 1, y - 1);
        addNeighborNodeInOpen(current, x + 1, y - 1);
        addNeighborNodeInOpen(current, x + 1, y + 1);
        addNeighborNodeInOpen(current, x - 1, y + 1);
    }

    /**
     * Add a neighbor node to the open table
     * @param current node to add
     * @param x x coordinate of the node
     * @param y y coordinate of the node
     */
    public void addNeighborNodeInOpen(Node current, int x, int y) {
        if (canAddNodeToOpen(x, y)) {
            Node end = this.caribbeanMap.end;
            Coord coord = new Coord(x, y);
            int G = current.G + 1; // Calculate the G value of the adjacent node
            Node child = findNodeInOpen(coord);
            if (child == null)
            {
                int H=calcH(end.coord,coord); // calculate H value

                if (end.coord.equals(coord)) {
                    child=end;
                    child.parent=current;
                    child.G=G;
                    child.H=H;
                } else {
                    child = new Node(coord, current, G, H);
                }
                this.openList.add(child);
            } else if (child.G > G) {
                child.G = G;
                child.parent = current;
                // readjust the heap
                this.openList.add(child);
            }
        }
    }

    /**
     * Draw the final path on the map
     */
    public void drawPath() {
        this.finalCost = this.caribbeanMap.end.G;

        while (this.caribbeanMap.end != null) {
            Coord c = this.caribbeanMap.end.coord;
            if (this.caribbeanMap.map[c.y][c.x] != MapSymbol.JACK.symbol)
                this.caribbeanMap.map[c.y][c.x] = MapSymbol.PATH.symbol;
            this.caribbeanMap.end = this.caribbeanMap.end.parent;

            this.path.add(c);
        }
    }

    /**
     * Common method to solve the task
     */
    public boolean solve() {
        this.path = new ArrayList<>();
        this.finalCost = 0;

        // clean
        this.openList.clear();
        this.closeList.clear();
        // start searching
        this.openList.add(this.caribbeanMap.start);

        return moveNodes();
    }

    /**
     * Move the current node (means move the Jack's position)
     */
    public boolean moveNodes() {
        while (!this.openList.isEmpty()) {
            if (isCoordInClose(this.caribbeanMap.end.coord)) {
                drawPath();
                return true;
            }

            Node current = openList.poll();
            this.closeList.add(current);
            addNeighborNodeInOpen(current);
        }

        this.finalCost = Integer.MAX_VALUE;
        return false;
    }

    /**
     * @return array of coordinates with path
     */
    public List<Coord> getFinalPath() {
        List<Coord> pathCopy = new ArrayList<>(this.path);
        Collections.reverse(pathCopy);

        return pathCopy.subList(1, pathCopy.size());
    }
}

public class Coord {
    public int x;
    public int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getFormatted() {
        return "[" + this.x + ", " + this.y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Coord) {
            Coord c = (Coord) obj;
            return x == c.x && y == c.y;
        }
        return false;
    }
}

public class Node implements Comparable<Node> {

    public Coord coord;
    public Node parent;
    public int G;
    public int H;

    public Node(int x, int y) {
        this.coord = new Coord(x, y);
    }

    public Node(Coord coord, Node parent, int g, int h) {
        this.coord = coord;
        this.parent = parent;
        G = g;
        H = h;
    }

    @Override
    public int compareTo(Node o) {
        if (o == null) return -1;
        if (G + H > o.G + o.H)
            return 1;
        else if (G + H < o.G + o.H) return -1;
        return 0;
    }
}


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

public enum Sign {
    PLUS,
    MINUS,
    NOTHING;

    static int calculate(int n, int add, Sign sign) {
        switch (sign) {
            case PLUS:
                return n + add;
            case MINUS:
                return n - add;
            default: return n;
        }
    }
}


public class Chest extends Mob {
    public Chest(int x, int y) {
        super(new Coord(x, y), MapSymbol.CHEST);
    }
}


public class Davy extends DangerMob {
    public Davy(int x, int y) {
        super(new Coord(x, y), MapSymbol.DAVY);

        super.setDiagonalDangerZones();
        super.setDirectDangerZones();
    }
}


public class Kraken extends DangerMob {
    public Kraken(int x, int y) {
        super(new Coord(x, y), MapSymbol.KRAKEN);
        super.setDirectDangerZones();
    }
}


public class Rock extends DangerMob {
    public Rock(int x, int y) {
        super(new Coord(x, y), MapSymbol.ROCK);
    }
}


public class Tortuga extends Mob {
    public Tortuga(int x, int y) {
        super(new Coord(x, y), MapSymbol.TORTUGA);
    }
}


public interface Algorithm {
    boolean solve();

    int getFinalCost();

    void setMap(CaribbeanMap caribbeanMap);

    List<Coord> getFinalPath();
}


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
        this.map = new char[CaribbeanMap.HEIGHT][CaribbeanMap.WIDTH];

        for (int y = 0; y < CaribbeanMap.HEIGHT; y++) {
            for (int x = 0; x < CaribbeanMap.WIDTH; x++) {
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
        for (int y = 0; y < CaribbeanMap.HEIGHT; y++) {
            System.out.print(y + " | ");
            for (int x = 0; x < CaribbeanMap.WIDTH; x++) {
                System.out.print(this.getMapCell(x, y) + " ");
            }
            System.out.println();
        }
    }

    public void fillMob(Mob mob) {
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
        mob.dangerZones.forEach(coord -> this.setMapCell(coord.x, coord.y, MapSymbol.DANGER_ZONE));
    }

    private void freeDangerZones(DangerMob mob) {
        mob.dangerZones.forEach(coord -> {
            boolean cellCanBeCleared = dangerMobs
                    .stream()
                    .filter(dangerMob -> dangerMob != mob)
                    .allMatch(dangerMob -> {
                        return dangerMob.dangerZones
                                .stream()
                                .noneMatch(anotherCoord -> anotherCoord.equals(coord));
                    });

            if (this.getMapCell(coord.x, coord.y) == MapSymbol.DANGER_ZONE.symbol && cellCanBeCleared) {
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


public class DangerMob extends Mob {
    public final List<Coord> dangerZones = new ArrayList<>();

    public DangerMob(Coord coords, MapSymbol symbol) {
        super(coords, symbol);
    }

    protected void setDirectDangerZones() {
        if (super.coords.x + 1 < 9) this.dangerZones.add(new Coord(super.coords.x+1, super.coords.y));
        if (super.coords.x - 1 >= 0) this.dangerZones.add(new Coord(super.coords.x-1, super.coords.y));
        if (super.coords.y + 1 < 9) this.dangerZones.add(new Coord(super.coords.x, super.coords.y+1));
        if (super.coords.y - 1 >= 0) this.dangerZones.add(new Coord(super.coords.x, super.coords.y-1));
    }

    protected void setDiagonalDangerZones() {
        if (super.coords.x - 1 >= 0 && super.coords.y + 1 < 9)
            this.dangerZones.add(new Coord(super.coords.x-1, super.coords.y+1));

        if (super.coords.x - 1 >= 0 && super.coords.y - 1 >= 0)
            this.dangerZones.add(new Coord(super.coords.x-1, super.coords.y-1));

        if (super.coords.x + 1 < 9 && super.coords.y + 1 < 9)
            this.dangerZones.add(new Coord(super.coords.x+1, super.coords.y+1));

        if (super.coords.x + 1 < 9 && super.coords.y - 1 >= 0)
            this.dangerZones.add(new Coord(super.coords.x+1, super.coords.y-1));
    }
}


public enum MapSymbol {
    FREE('~'),
    JACK('J'),
    DAVY('D'),
    KRAKEN('K'),
    ROCK('R'),
    CHEST('C'),
    TORTUGA('T'),
    DANGER_ZONE('#'),
    PATH('@');

    public final char symbol;

    MapSymbol(char symbol) {
        this.symbol = symbol;
    }
}


public abstract class Mob {
    public final Coord coords;
    public final char icon;

    public Mob(Coord coords, MapSymbol symbol) {
        this.coords = coords;
        this.icon = symbol.symbol;
    }
}


