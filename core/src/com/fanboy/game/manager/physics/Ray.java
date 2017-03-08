package com.fanboy.game.manager.physics;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.renderer.world.WorldRenderer;

import java.util.function.Consumer;

import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_WIDTH;
import static java.lang.Math.pow;

public class Ray {
    private final static Vector2 temp = new Vector2();

    public static Body findBody(World world, Body sourceBody, Vector2 step, float length) {
        return findBody(world, sourceBody, step, length, false);
    }

    public static Body findBody(World world, Body sourceBody, Vector2 step, double length, boolean staticOnly) {
        if (step.isZero()) {
            return null;
        }

        FindBodyByRayConsumer consumer = new FindBodyByRayConsumer(sourceBody, length, step, staticOnly);
        world.forEachBody(consumer);
        return consumer.getClosestBody();
    }

    private static class FindBodyByRayConsumer implements Consumer<Body> {
        private final Body sourceBody;
        private final Vector2 start, step;
        private final double length;
        private final boolean staticOnly;

        private float closestBodyDistance = 100000;
        private Body closestBody = null;

        public FindBodyByRayConsumer(Body sourceBody, double length, Vector2 step, boolean staticOnly) {
            this.sourceBody = sourceBody;
            this.start = sourceBody.getPosition();
            this.length = pow(length, 2);
            this.step = step.scl(2);
            this.staticOnly = staticOnly;
        }

        @Override
        public void accept(Body body) {
            if (body == sourceBody) {
                return;
            }
            if (staticOnly && !body.isStatic()) {
                return;
            }
            temp.set(start);
            float currentDistance = temp.dst2(start);
            while (currentDistance < length && currentDistance < closestBodyDistance) {
                if (body.rectangle.contains((temp.x + VIEWPORT_WIDTH) % VIEWPORT_WIDTH, (temp.y + WorldRenderer.VIEWPORT_HEIGHT) % WorldRenderer.VIEWPORT_HEIGHT)) {
                    closestBody = body;
                    closestBodyDistance = currentDistance;
                    continue;
                }
                temp.add(step);
                currentDistance = temp.dst2(start);
            }
        }

        public Body getClosestBody() {
            return closestBody;
        }
    }
}
