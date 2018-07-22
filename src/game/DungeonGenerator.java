package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import org.joml.Vector2d;
import util.Noise;

public class DungeonGenerator {

    public Noise noise = new Noise(Math.random() * 1e6);
    public Map<IntPair, SquareInfo> world = new HashMap();

    public void createLevel(int numRooms, int numMonsters) {
        for (int i = 0; i < numRooms; i++) {
            DungeonRoom room = placeRoom();
            if (numRooms > 100) {
                System.out.println("Placed room number " + i);
            }
            if (i > 0) {
                List<IntPair> doors = new ArrayList();
                doors.add(connectRoom(room, doors));
                while (Math.random() < .5) {
                    doors.add(connectRoom(room, doors));
                }
            }
        }
        Dungeon d = new Dungeon();
        List<IntPair> openPositions = world.entrySet().stream()
                .filter(e -> e.getValue().isOpen)
                .map(Entry::getKey)
                .collect(Collectors.toList());
        int xMin = openPositions.stream().mapToInt(ip -> ip.x).min().getAsInt() - 1;
        int xMax = openPositions.stream().mapToInt(ip -> ip.x).max().getAsInt() + 2;
        int yMin = openPositions.stream().mapToInt(ip -> ip.y).min().getAsInt() - 1;
        int yMax = openPositions.stream().mapToInt(ip -> ip.y).max().getAsInt() + 2;
        d.collider.position.position = new Vector2d(xMin, yMin).mul(64);
        d.wallArray = new boolean[xMax - xMin][yMax - yMin];
        for (int i = xMin; i < xMax; i++) {
            for (int j = yMin; j < yMax; j++) {
                d.wallArray[i - xMin][j - yMin] = !squareAt(i, j).isOpen;
            }
        }
        d.create();

        Set<IntPair> chosen = new HashSet();
        for (int i = 0; i < numMonsters; i++) {
            IntPair pos = openPositions.get((int) (Math.random() * openPositions.size()));
            while (chosen.contains(pos) || pos.distance(new IntPair(0, 0)) < 10) {
                pos = openPositions.get((int) (Math.random() * openPositions.size()));
            }
            chosen.add(pos);
            Monster m = new Monster();
            m.position.position = new Vector2d(pos.x + .5, pos.y + .5).mul(64);
            m.create();
        }
    }

    public IntPair connectRoom(DungeonRoom room, List<IntPair> avoid) {
        IntPair start = new IntPair(room.x, room.y);
        Map<IntPair, Double> cost = new HashMap();
        cost.put(start, 0.);
        Map<IntPair, IntPair> parents = new HashMap();
        PriorityQueue<IntPair> toCheck = new PriorityQueue(Comparator.comparingDouble(cost::get));
        toCheck.add(start);
        while (!toCheck.isEmpty()) {
            IntPair ip = toCheck.poll();
            if (!room.contains(ip) && squareAt(ip).isOpen) {
                while (true) {
                    squareAt(ip).isOpen = true;
                    if (room.contains(parents.get(ip))) {
                        return ip;
                    }
                    ip = parents.get(ip);
                }
            }
            for (IntPair n : neighbors(ip)) {
                double newCost = cost.get(ip);
                if (avoid.isEmpty()) {
                    newCost += squareAt(n).cost();
                } else if (avoid.contains(n)) {
                    newCost += squareAt(n).cost() + 10000;
                } else {
                    newCost += squareAt(n).cost() / minDistance(n, avoid);
                }

                if (!cost.containsKey(n) || cost.get(n) > newCost) {
                    cost.put(n, newCost);
                    parents.put(n, ip);
                    toCheck.remove(n);
                    toCheck.add(n);
                }
            }
        }
        return null;
    }

    public double minDistance(IntPair pos, List<IntPair> list) {
        return list.stream().mapToDouble(pos::distance).min().getAsDouble();
    }

    public List<IntPair> neighbors(IntPair ip) {
        return Arrays.asList(new IntPair(ip.x + 1, ip.y), new IntPair(ip.x - 1, ip.y),
                new IntPair(ip.x, ip.y + 1), new IntPair(ip.x, ip.y - 1));
    }

    public DungeonRoom placeRoom() {
        int width = (int) (Math.random() * 5 + 5);
        int height = (int) (Math.random() * 5 + 5);
        double distance = 0;
        while (true) {
            double theta = Math.random() * 360;
            DungeonRoom room = new DungeonRoom();
            room.x = (int) Math.round(distance * Math.cos(theta) - width / 2.);
            room.y = (int) Math.round(distance * Math.sin(theta) - height / 2.);
            room.width = width;
            room.height = height;
            if (room.canPlace()) {
                room.place();
                return room;
            }
            distance += Math.random() * 10;
        }
    }

    public SquareInfo squareAt(int x, int y) {
        return squareAt(new IntPair(x, y));
    }

    public SquareInfo squareAt(IntPair pos) {
        world.putIfAbsent(pos, new SquareInfo(pos));
        return world.get(pos);
    }

    public class DungeonRoom {

        public int x, y;
        public int width, height;

        public boolean canPlace() {
            for (int i = x - 1; i <= x + width; i++) {
                for (int j = y - 1; j <= y + height; j++) {
                    if (squareAt(i, j).isOpen) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean contains(IntPair pos) {
            return pos.x >= x && pos.x < x + width && pos.y >= y && pos.y < y + height;
        }

        public void place() {
            for (int i = x - 1; i <= x + width; i++) {
                for (int j = y - 1; j <= y + height; j++) {
                    SquareInfo si = squareAt(i, j);
                    si.isOpen = contains(new IntPair(i, j));
                    si.costModifier = si.isOpen ? 0 : 5;
                }
            }
        }
    }

    public class IntPair {

        public final int x, y;

        public IntPair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public double distance(IntPair other) {
            return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IntPair other = (IntPair) obj;
            if (this.x != other.x) {
                return false;
            }
            if (this.y != other.y) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 19 * hash + this.x;
            hash = 19 * hash + this.y;
            return hash;
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + "]";
        }
    }

    public class SquareInfo {

        public boolean isOpen;
        public double perlinCost;
        public double costModifier;

        public SquareInfo(IntPair pos) {
            isOpen = false;
            perlinCost = noise.perlin(pos.x, pos.y, .1);
            costModifier = 1;
        }

        public double cost() {
            return perlinCost * costModifier;
        }
    }
}
