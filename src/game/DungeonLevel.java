package game;

import engine.Behavior;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector2d;
import util.Noise;

public class DungeonLevel extends Behavior {

    public Noise noise = new Noise(Math.random() * 1e6);
    public List<Wall> walls = new LinkedList();

    @Override
    public void createInner() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (noise.perlin(i, j, .1) > .5) {
                    Wall w = new Wall();
                    w.position.position = new Vector2d(i, j).mul(32);
                    w.create();
                    walls.add(w);
                }
            }
        }
    }

    @Override
    public void destroyInner() {
        walls.forEach(Wall::destroy);
    }
}
