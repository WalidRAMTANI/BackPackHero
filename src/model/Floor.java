package model;

import java.awt.Point;
import java.util.*;

import Rooms.CorridorRoom;
import Rooms.EnemyRoom;
import Rooms.HealerRoom;
import Rooms.MerchantRoom;
import Rooms.RoomType;
import Rooms.TreasureRoom;
import Rooms.WallRoom;

/**
 * Represents a game floor composed of a grid of rooms.
 */
public class Floor {

    private final RoomType[][] rooms;
    private final ArrayList<Point> listPoints;
    private Point entrance;
    private Point exit;

    /**
     * @return The entrance point of the floor
     */
    public Point getEntrance() {
        return entrance;
    }

    /**
     * @return The exit point of the floor
     */
    public Point getExit() {
        return exit;
    }

    /**
     * Constructor for Floor.
     */
    public Floor() {
        rooms = new RoomType[5][11];
        listPoints = new ArrayList<Point>();
        generateFloor();
    }

    /**
     * @return The grid of rooms
     */
    public RoomType[][] getRooms() {
        return rooms;
    }

    /**
     * Fills all empty positions in the grid with wall rooms.
     */
    private void fillWalls() {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 11; y++) {
                if (rooms[x][y] == null) {
                    rooms[x][y] = new WallRoom();
                }
            }
        }
    }

    /**
     * Provides a textual representation of the floor layout.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 11; y++) {
                RoomType room = rooms[x][y];
                sb.append(String.format("[%c] ", room.getTypeName().charAt(0)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @param random Random generator
     */
    public void generateEntrance(Random random) {
        Objects.requireNonNull(random);
        int row = random.nextInt(5);
        int col = 0;
        entrance = new Point(row, col);
        rooms[row][col] = new CorridorRoom(true, false, false);
        listPoints.add(new Point(row, col));
    }

    /**
     * @param random Random generator
     */
    public void generateExit(Random random) {
        Objects.requireNonNull(random);
        int row = random.nextInt(5);
        int col = 10;
        exit = new Point(row, col);
        rooms[row][col] = new CorridorRoom(false, true, false);
        listPoints.add(new Point(row, col));
    }

    /**
     * @param random Random generator
     */
    public void generateEnnemies(Random random) {
        Objects.requireNonNull(random);
        int row = random.nextInt(5);
        int col = random.nextInt(11);
        for (int i = 0; i < 3; i++) {
            while (listPoints.contains(new Point(row, col))) {
                row = random.nextInt(5);
                col = random.nextInt(11);
            }
            listPoints.add(new Point(row, col));
            rooms[row][col] = new EnemyRoom();
        }
    }

    /**
     * @param random Random generator
     */
    public void generateHealer(Random random) {
        Objects.requireNonNull(random);
        int row = random.nextInt(5);
        int col = random.nextInt(11);
        while (listPoints.contains(new Point(row, col))) {
            row = random.nextInt(5);
            col = random.nextInt(11);
        }
        listPoints.add(new Point(row, col));
        rooms[row][col] = new HealerRoom();
    }

    /**
     * @param random Random generator
     */
    public void generateMerchant(Random random) {
        Objects.requireNonNull(random);
        int row = random.nextInt(5);
        int col = random.nextInt(11);
        while (listPoints.contains(new Point(row, col))) {
            row = random.nextInt(5);
            col = random.nextInt(11);
        }
        listPoints.add(new Point(row, col));
        rooms[row][col] = new MerchantRoom();
    }

    /**
     * @param random Random generator
     */
    public void generateTreasures(Random random) {
        Objects.requireNonNull(random);
        int rows = rooms.length;
        int cols = rooms[0].length;

        for (int i = 0; i < 2; i++) {
            Point p;
            do {
                int row = random.nextInt(rows);
                int col = random.nextInt(cols);
                p = new Point(row, col);
            } while (listPoints.contains(p));

            listPoints.add(p);
            rooms[p.x][p.y] = new TreasureRoom();
        }
    }

    /**
     * @param p Point to check
     * @return true if point is inside floor bounds
     */
    public static boolean isInside(Point p) {
        Objects.requireNonNull(p);
        return p.x >= 0 && p.x < 5
                && p.y >= 0 && p.y < 11;
    }

    /**
     * Generates a corridor between two points.
     * 
     * @param src    Starting point
     * @param dst    Ending point
     * @param locked true if the corridor should be locked
     */
    public void generateCorridors(Point src, Point dst, boolean locked) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dst);
        if (!isInside(src) || !isInside(dst))
            return;
        if (src.equals(dst))
            return;

        if (src.x < dst.x) {
            if (!listPoints.contains(src) && rooms[src.x][src.y] == null) {
                rooms[src.x][src.y] = new CorridorRoom(false, false, locked);
            }
            generateCorridors(new Point(src.x + 1, src.y), dst, locked);
            return;
        } else if (src.x > dst.x) {
            if (!listPoints.contains(src) && rooms[src.x][src.y] == null) {
                rooms[src.x][src.y] = new CorridorRoom(false, false, locked);
            }
            generateCorridors(new Point(src.x - 1, src.y), dst, locked);
            return;
        }
        if (src.y < dst.y) {
            if (!listPoints.contains(src) && rooms[src.x][src.y] == null) {
                rooms[src.x][src.y] = new CorridorRoom(false, false, locked);
            }
            generateCorridors(new Point(src.x, src.y + 1), dst, locked);
            return;
        } else if (src.y > dst.y) {
            if (!listPoints.contains(src) && rooms[src.x][src.y] == null) {
                rooms[src.x][src.y] = new CorridorRoom(false, false, locked);
            }
            generateCorridors(new Point(src.x, src.y - 1), dst, locked);
            return;
        }
    }

    /**
     * Interconnects all special rooms with corridors.
     */
    public void generateCorridors() {
        ArrayList<Point> connected = new ArrayList<>();
        ArrayList<Point> remaining = new ArrayList<>(listPoints);

        connected.add(remaining.remove(0)); // entrance

        while (!remaining.isEmpty()) {
            Point bestFrom = null;
            Point bestTo = null;
            int bestDist = 1000;

            for (Point from : connected) {
                for (Point to : remaining) {
                    int d = minCorridorRooms(from, to);
                    if (d < bestDist) {
                        bestDist = d;
                        bestFrom = from;
                        bestTo = to;
                    }
                }
            }

            generateCorridors(bestFrom, bestTo, false);
            connected.add(bestTo);
            remaining.remove(bestTo);
        }
    }

    /**
     * Adds random shortcut corridors.
     * 
     * @param random Random generator
     */
    public void secondPath(Random random) {
        Objects.requireNonNull(random);
        for (Point a : listPoints) {
            if (random.nextDouble() < 0.25) { // 25% chance
                Point b = listPoints.get(random.nextInt(listPoints.size()));
                if (!a.equals(b)) {
                    boolean locked = random.nextBoolean();
                    generateCorridors(a, b, locked);
                }
            }
        }
    }

    /**
     * @param a Point A
     * @param b Point B
     * @return Manhattan distance between A and B
     */
    private int minCorridorRooms(Point a, Point b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Generates a complete floor layout.
     */
    public void generateFloor() {
        Random random = new Random();
        generateEntrance(random);
        generateExit(random);
        generateEnnemies(random);
        generateHealer(random);
        generateMerchant(random);
        generateTreasures(random);
        generateCorridors();
        secondPath(random);
        fillWalls();
    }

    /**
     * Checks if a path exists and returns its length.
     * 
     * @param src Starting position
     * @param dst Destination position
     * @return Shortest path length or -1 if blocked
     */
    public int allowedFromTo(Point src, Point dst) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dst);
        if (!isInside(src) || !isInside(dst)) {
            return -1;
        }

        RoomType dstRoom = rooms[dst.x][dst.y];
        if (dstRoom instanceof WallRoom || (dstRoom instanceof CorridorRoom cr && cr.closed())) {
            return -1;
        }
        // check if the path is blocked by an enemy
        int dMin = bfs(src, dst, false);
        if (dMin == -1) {
            return -1;
        }
        // check if the path is blocked by an enemy
        int dClear = bfs(src, dst, true);

        if (dClear != -1 && dClear == dMin) {
            return dClear;
        }

        return -1;
    }

    /**
     * Standard BFS to find shortest path.
     * 
     * @param src          Start point
     * @param dst          End point
     * @param avoidEnemies true if enemy rooms should be avoided
     * @return Path length or -1
     */
    private int bfs(Point src, Point dst, boolean avoidEnemies) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dst);
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Integer> dists = new HashMap<>();

        queue.add(src);
        dists.put(src, 0);

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int d = dists.get(current);

            if (current.equals(dst)) {
                return d;
            }
            
            int[] dr = { 1, -1, 0, 0 };
            int[] dc = { 0, 0, 1, -1 };

            for (int i = 0; i < 4; i++) {
                Point n = new Point(current.x + dr[i], current.y + dc[i]);
                if (isInside(n) && !dists.containsKey(n)) {
                    RoomType room = rooms[n.x][n.y];

                    if (n.equals(dst)) {
                        dists.put(n, d + 1);
                        queue.add(n);
                        continue;
                    }

                    if (room instanceof WallRoom || (room instanceof CorridorRoom cr && cr.closed())) {
                        continue;
                    }

                    if (avoidEnemies && room instanceof EnemyRoom er) {
                        if (!er.isCleared()) {
                            continue;
                        }
                    }

                    dists.put(n, d + 1);
                    queue.add(n);
                }
            }
        }
        return -1;
    }
}
