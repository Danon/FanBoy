package com.fanboy.game.manager.physics;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.entity.player.ServerPlayer;
import com.fanboy.game.manager.WorldManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class World {
    private final List<Body> bodies = new ArrayList<>();
    private final Vector2 gravity;

    public World(Vector2 gravity) {
        this.gravity = gravity;
    }

    public void step(float delta, int iterations, WorldManager worldManager) {
        int i = 0;
        while (i < bodies.size()) {
            Body body = bodies.get(i);
            if (body.toDestroy) {
                worldManager.destroyBody(body);
                bodies.remove(i);
            }
            i++;
        }

        for (i = 0; i < iterations; i++) {
            step(delta, iterations);
        }
    }

    private void step(float delta, float iterations) {
        // Update players first
        bodies.stream()
                .filter(body -> body.getUserData() instanceof ServerPlayer)
                .forEach(body -> body.update(delta / iterations));

        // then everything else
        bodies.stream()
                .filter(Body::isDynamic)
                .filter(body -> !(body.getUserData() instanceof ServerPlayer))
                .forEach(body -> body.update(delta / iterations));
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public void forEachBody(Consumer<Body> consumer) {
        this.bodies.forEach(consumer);
    }

    public Vector2 getGravity() {
        return gravity;
    }
}
