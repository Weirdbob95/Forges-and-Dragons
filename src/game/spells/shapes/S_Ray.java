package game.spells.shapes;

import behaviors.ColliderBehavior.Rectangle;
import engine.Behavior;
import static game.GraphicsEffect.createGraphicsEffect;
import game.spells.SpellInfo;
import graphics.Graphics;
import java.util.LinkedList;
import org.joml.Vector2d;

public class S_Ray extends SpellShapeMissile {

    @Override
    public void cast(SpellInfo info, Vector2d goal) {
        spawnMissiles(info, goal, S_RayBehavior.class, 3000);
    }

    public static class S_RayBehavior extends Behavior {

        private final static double PERSIST_TIME = .1;

        public final MissileBehavior missile = require(MissileBehavior.class);

        public LinkedList<Vector2d> pastPositions = new LinkedList();
        public LinkedList<Double> pastTimes = new LinkedList();
        public double currentTime;

        @Override
        public void createInner() {
            missile.collider.collisionShape = new Rectangle(missile.position, new Vector2d(8, 8));
            missile.lifetime.lifetime = .2;
            missile.homingRate = 20;

            pastPositions.add(new Vector2d(missile.position.position));
            pastTimes.add(0.);
        }

        @Override
        public void destroyInner() {
            createGraphicsEffect(PERSIST_TIME, t -> {
                if (currentTime + t - pastTimes.peek() > PERSIST_TIME) {
                    pastPositions.poll();
                    pastTimes.poll();
                }
                for (int i = 0; i < pastPositions.size() - 1; i++) {
                    Graphics.drawWideLine(pastPositions.get(i), pastPositions.get(i + 1), 3, missile.info.color());
                }
            });
        }

        @Override
        public void render() {
            for (int i = 0; i < pastPositions.size() - 1; i++) {
                Graphics.drawWideLine(pastPositions.get(i), pastPositions.get(i + 1), 3, missile.info.color());
            }
        }

        @Override
        public void update(double dt) {
            currentTime += dt;
            pastPositions.add(new Vector2d(missile.position.position));
            pastTimes.add(currentTime);
            if (currentTime - pastTimes.peek() > PERSIST_TIME) {
                pastPositions.poll();
                pastTimes.poll();
            }
        }
    }
}
