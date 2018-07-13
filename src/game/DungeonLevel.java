package game;

import engine.Behavior;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.joml.Vector2d;
import util.Noise;

public class DungeonLevel extends Behavior {

    private static final int SIZE = 50;

    public Noise noise = new Noise(Math.random() * 1e6);
    public List<Wall> walls = new LinkedList();
    public boolean[][] wallArray;

    @Override
    public void createInner() {
        wallArray = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                wallArray[i][j] = (noise.perlin(i, j, .1) > .5);

            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (wallArray[i][j]) {
                    Wall w = new Wall();
                    w.position.position = new Vector2d(i, j).mul(32);
                    w.create();
                    walls.add(w);
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            placeObject(2, pos -> {
                Monster m = new Monster();
                m.position.position = pos;
                m.create();
            });
        }
    }

    @Override
    public void destroyInner() {
        walls.forEach(Wall::destroy);
    }

    public void placeObject(int squares, Consumer<Vector2d> placeCallback) {
        while (true) {
            int i = (int) (Math.random() * (SIZE + 1 - squares));
            int j = (int) (Math.random() * (SIZE + 1 - squares));
            boolean empty = true;
            for (int i2 = i; i2 < i + squares; i2++) {
                for (int j2 = j; j2 < j + squares; j2++) {
                    if (wallArray[i2][j2]) {
                        empty = false;
                    }
                }
            }
            if (empty) {
                placeCallback.accept(new Vector2d(i + (squares - 1) / 2., j + (squares - 1) / 2.).mul(32));
                break;
            }
        }
    }
}
