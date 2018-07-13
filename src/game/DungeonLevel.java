package game;

import behaviors.RepeatedSpriteBehavior;
import engine.Behavior;
import graphics.RepeatedSprite;
import graphics.Sprite;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.joml.Vector2d;
import util.Noise;

public class DungeonLevel extends Behavior {

    private static final int SIZE = 100;

    public final RepeatedSpriteBehavior repeatedSprite = require(RepeatedSpriteBehavior.class);

    public Noise noise = new Noise(Math.random() * 1e6);
    public boolean[][] wallArray;

    @Override
    public void createInner() {
        System.out.println("Generating level...");
        wallArray = new boolean[SIZE][SIZE];
        List<Vector2d> wallPositions = new ArrayList();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                wallArray[i][j] = (noise.perlin(i, j, .1) > .5);
                if (wallArray[i][j]) {
                    wallPositions.add(new Vector2d(i, j));
                }
            }
        }
        repeatedSprite.repeatedSprite = new RepeatedSprite(Sprite.load("wall.png"), wallPositions);

        for (int i = 0; i < 10; i++) {
            placeObject(2, pos -> {
                Monster m = new Monster();
                m.position.position = pos;
                m.create();
            });
        }
        System.out.println("Done!");
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
