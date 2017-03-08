package com.fanboy.entity.blob;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.category.EnemyCategory;
import com.fanboy.category.LivingCategory;
import com.fanboy.entity.ActorType;
import com.fanboy.entity.player.ServerPlayer;
import com.fanboy.entity.ServerEntity;
import com.fanboy.game.Utils;
import com.fanboy.game.manager.WorldBodyUtils;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.game.manager.physics.BodyType;
import com.fanboy.game.manager.physics.CollisionType;
import com.fanboy.game.manager.physics.Ray;
import com.fanboy.network.message.EntityState;

public class ServerBlob extends ServerEntity implements EnemyCategory, LivingCategory {
    public static final float WIDTH = 15f, HEIGHT = 10f, Y_OFFSET = 1f;

    private Vector2 tempVector = new Vector2();
    private float velocity = 55f, spawnTime = 0.1f;

    public ServerBlob(short id, Vector2 position, WorldBodyUtils world) {
        super(id, position, ActorType.BLOB, world);
    }

    @Override
    protected Body createBody(Vector2 position, WorldBodyUtils world) {
        Body body = world.createBody(this, WIDTH, HEIGHT - Y_OFFSET * 2, position, BodyType.Static);
        body.setVelocity(velocity, 0);
        body.setGravityScale(0.75f);
        body.category = CollisionType.NONE;
        return body;
    }

    public void setDirection(float direction) {
        velocity *= direction;
        body.setVelocity(velocity, 0);
    }

    @Override
    public void update(float delta) {
        if (spawnTime > 0) {
            spawnTime += delta;
            if (spawnTime > 4) {
                body.category = CollisionType.ENEMY;
                body.setBodyType(BodyType.Dynamic);
                spawnTime = -1f;
            }
            return;
        }
        Vector2 velocity = body.getVelocity();
        position.set(body.getPosition());
        if (body.isOnGround()) {
            Body targetBody = Ray.findBody(world.worldManager.getWorld(), body, tempVector.set(Math.signum(velocity.x) * 5, 0), 40f);
            if (targetBody != null) {
                if (targetBody.getUserData() instanceof ServerPlayer) {
                    body.setVelocity(1.5f * velocity.x, velocity.y + 100);
                }
            }
        }

        velocity = tempVector.set(body.getVelocity());
        if (Math.abs(velocity.x) < 40f) {
            this.velocity *= -1;
            velocity.x = this.velocity;
        }

        body.setVelocity(velocity);

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
