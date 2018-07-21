package game;

import behaviors.ColliderBehavior;
import behaviors.RepeatedSpriteBehavior;
import engine.Behavior;
import graphics.RepeatedSprite;
import graphics.Sprite;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2d;

public class Dungeon extends Behavior {

    public final RepeatedSpriteBehavior repeatedSprite = require(RepeatedSpriteBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);

    public boolean[][] wallArray;

    @Override
    public void createInner() {
        List<Vector2d> wallPositions = new ArrayList();
        for (int i = 0; i < wallArray.length; i++) {
            for (int j = 0; j < wallArray[0].length; j++) {
                if (wallArray[i][j]) {
                    wallPositions.add(new Vector2d(i, j));
                }
            }
        }
        repeatedSprite.repeatedSprite = new RepeatedSprite(Sprite.load("bigwall.png"), wallPositions);
        collider.collisionShape = new ColliderBehavior.RectangleGrid(collider.position, wallArray, new Vector2d(64));
        collider.setSolid(true);
    }
}
