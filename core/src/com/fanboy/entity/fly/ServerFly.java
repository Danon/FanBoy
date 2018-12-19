package com.fanboy.entity.fly;

import com.badlogic.gdx.math.MathUtils;
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
import com.fanboy.game.manager.physics.Ray;
import com.fanboy.network.message.EntityState;

import java.util.List;

public class ServerFly extends ServerEntity implements EnemyCategory, LivingCategory {
    private static final float WIDTH = 15f;
    public static final float HEIGHT = 10f;
    private static final float Y_OFFSET = 1f;

    private float randomTime = 0, spawnTime = 0.1f;

    public ServerFly(short id, Vector2 position, WorldBodyUtils world) {
        super(id, position, ActorType.FLY, world);
    }

    @Override
    protected Body createBody(Vector2 position, WorldBodyUtils world) {
        Body body = world.createBody(this,WIDTH, HEIGHT - Y_OFFSET * 2, position, BodyType.Static);
        body.setGravityScale(0f);
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
        } else {
            position.set(body.getPosition());
            List<Vector2> playersPositions = world.getPlayers(position, 100);

            if (playersPositions.isEmpty()) {
                randomTime += delta;
                Vector2 currentVelocity = body.getVelocity();
                float max = Math.max(Math.abs(currentVelocity.x), Math.abs(currentVelocity.y));
                currentVelocity.scl(5 / max);
                Body targetBody = Ray.findBody(world.worldManager.getWorld(), body, currentVelocity, 30f);
                if ((Math.abs(body.getVelocity().x) < 20 && Math.abs(body.getVelocity().y) < 20) || (targetBody != null && targetBody.isStatic()) || randomTime > 4) {
                    do {
                        currentVelocity = body.getVelocity();
                        currentVelocity.set(
                                (MathUtils.random(20, 30)) * (MathUtils.random(-1, 1) < 0 ? -1 : 1),
                                (MathUtils.random(20, 30)) * (MathUtils.random(-1, 1) < 0 ? -1 : 1)
                        );
                        body.setVelocity(currentVelocity);
                        randomTime = 0;
                        targetBody = Ray.findBody(world.worldManager.getWorld(), body, currentVelocity, 30f);
                    } while (
                            (Math.abs(body.getVelocity().x) < 20 && Math.abs(body.getVelocity().y) < 20) ||
                                    (targetBody != null && targetBody.isStatic())
                            );
                }
            } else {
                Vector2 playerPosition = playersPositions.get(0);
                playerPosition.sub(position);
                float max = Math.max(Math.abs(playerPosition.x), Math.abs(playerPosition.y));
                playerPosition.scl(5 / max);

                body.setVelocity(playerPosition.x * 20, playerPosition.y * 20);

            }

            Vector2 currentVelocity = body.getVelocity();
            float max = Math.max(Math.abs(currentVelocity.x), Math.abs(currentVelocity.y));
            if (max > 50) {
                currentVelocity.scl(50 / max);
            }
            body.setVelocity(currentVelocity);
            position.set(body.getPosition());
            if (Utils.wrapBody(position)) {
                body.setTransform(position);
            }
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
            world.audio.jumpedOn();
            dispose();
            return true;
        }
        return false;
    }

}
