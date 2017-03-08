package com.fanboy.entity.bomb;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fanboy.category.ExplodingWeaponCategory;
import com.fanboy.entity.ActorType;
import com.fanboy.entity.ServerEntity;
import com.fanboy.game.Utils;
import com.fanboy.game.manager.WorldBodyUtils;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.game.manager.physics.BodyType;
import com.fanboy.network.message.EntityState;

import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_HEIGHT;
import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_WIDTH;

public class ServerBomb extends ServerEntity implements ExplodingWeaponCategory {
    public static final float RADIUS = 15f;
    public ServerEntity attacker;
    private float destroyTime = 1.5f;

    public ServerBomb(short id, Vector2 position, WorldBodyUtils world) {
        super(id, position, ActorType.BOMB, world);
    }

    @Override
    protected Body createBody(Vector2 position, WorldBodyUtils world) {
        Body body = world.createBody(this, RADIUS, RADIUS, position, BodyType.Dynamic);
        body.setVelocity(0, 0);
        body.restitutionX = 0.5f;
        body.restitutionY = 0.5f;
        body.xDamping = 0.02f;
        body.setGravityScale(0.75f);
        return body;
    }

    @Override
    public void update(float delta) {
        destroyTime -= delta;
        position.set(body.getPosition());

        if (Utils.wrapBody(position)) {
            body.setTransform(position);
        }

        if (destroyTime < 0) {
            explode();
        }
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
    }

    @Override
    public void updateState(EntityState state) {
        super.updateState(state);
        state.angle = MathUtils.atan2(body.getVelocity().y, body.getVelocity().x);
    }

    @Override
    public float getWidth() {
        return RADIUS;
    }

    @Override
    public void explode() {
        if (body.toDestroy) {
            return;
        }
        world.audio.explode();
        Vector2 position = body.getPosition();
        world.destroyEntities(this, 35, position);

        if (position.x > VIEWPORT_WIDTH - 20) {
            position.x -= VIEWPORT_WIDTH;
        } else if (position.x < 20) {
            position.x += VIEWPORT_WIDTH;
        }
        world.destroyEntities(this, 35, position);

        position = body.getPosition();

        if (position.y > VIEWPORT_HEIGHT - 20) {
            position.y -= VIEWPORT_HEIGHT;
        } else if (position.x < 20) {
            position.y += VIEWPORT_HEIGHT;
        }
        world.destroyEntities(this, 35, position);

        dispose();
    }

    @Override
    public ServerEntity getShooter() {
        return attacker;
    }
}
