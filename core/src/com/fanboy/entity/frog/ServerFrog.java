package com.fanboy.entity.frog;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.category.EnemyCategory;
import com.fanboy.category.LivingCategory;
import com.fanboy.entity.ActorType;
import com.fanboy.entity.ServerEntity;
import com.fanboy.game.Utils;
import com.fanboy.game.manager.WorldBodyUtils;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.game.manager.physics.BodyType;
import com.fanboy.game.manager.physics.CollisionType;
import com.fanboy.network.message.EntityState;

public class ServerFrog extends ServerEntity implements EnemyCategory, LivingCategory {
    public static final float WIDTH = 15f;
    public static final float HEIGHT = 10f;
    private static final float Y_OFFSET = 1f;

    private float velocity = 200f, waitTime = 0, spawnTime = 0.1f;
    private float direction = 1;

    public ServerFrog(short id, Vector2 position, WorldBodyUtils world) {
        super(id, position, ActorType.FROG, world);
    }

    @Override
    protected Body createBody(Vector2 position, WorldBodyUtils world) {
        Body body = world.createBody(this, WIDTH, HEIGHT - Y_OFFSET * 2, position, BodyType.Static);
        body.collisionType = CollisionType.NONE;
        return body;
    }

    @Override
    public void update(float delta) {
        if (spawnTime > 0) {
            spawnTime += delta;
            if (spawnTime > 4) {
                body.collisionType = CollisionType.ENEMY;
                body.setBodyType(BodyType.Dynamic);
                spawnTime = -1f;
            }
            return;
        }
        if (body.isOnGround()) {
            body.restitutionX = 0;
            body.restitutionY = 0;
            body.setVelocity(Vector2.Zero);
            waitTime += delta;
            if (waitTime > 1) {
                body.restitutionX = 1;
                body.restitutionY = 1;
                waitTime = 0;
                Vector2 velocityVector = body.getVelocity();
                velocityVector.x = direction * velocity / 2;
                velocityVector.y = Math.abs(velocity);
                body.setVelocity(velocityVector);
            }
        } else {
            float nextDirection = Math.signum(body.getVelocity().x);
            if (nextDirection != 0) {
                direction = nextDirection;
            }
        }

        position.set(body.getPosition());
        if (Utils.wrapBody(position)) {
            body.setTransform(position);
        }
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
    }

    @Override
    public float getWidth() {
        return WIDTH;
    }

    @Override
    public void updateState(EntityState state) {
        super.updateState(state);
        state.vX = body.getVelocity().x;
        state.vY = body.getVelocity().y;
        state.extra |= (short) (spawnTime > 0.01f ? 0 : 1);
    }

    @Override
    public boolean kill() {
        if (spawnTime < 0 && !body.toDestroy) {
            dispose();
            world.audio.jumpedOn();
            return true;
        }
        return false;
    }
}
