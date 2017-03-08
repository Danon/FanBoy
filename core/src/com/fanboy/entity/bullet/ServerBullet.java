package com.fanboy.entity.bullet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fanboy.category.NonExplodingWeaponCategory;
import com.fanboy.entity.ActorType;
import com.fanboy.entity.ServerEntity;
import com.fanboy.game.Utils;
import com.fanboy.game.manager.WorldBodyUtils;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.game.manager.physics.BodyType;
import com.fanboy.network.message.EntityState;

public class ServerBullet extends ServerEntity implements NonExplodingWeaponCategory {
    private static final float RADIUS = 5f;

    public ServerEntity shooter;
    private float destroyTime = 1f;

    public ServerBullet(short id, Vector2 position, WorldBodyUtils world) {
        super(id, position, ActorType.BULLET, world);
    }

    @Override
    protected Body createBody(Vector2 position, WorldBodyUtils world) {
        Body body = world.createBody(this,RADIUS, RADIUS, position, BodyType.Dynamic);
        body.setVelocity(0, 0);
        body.setGravityScale(0f);
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
            dispose();
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
    public void addKill() {
        // TODO Auto-generated method stub

    }

    @Override
    public ServerEntity getShooter() {
        return shooter;
    }
}
