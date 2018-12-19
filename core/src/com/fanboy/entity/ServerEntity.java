package com.fanboy.entity;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.game.manager.WorldBodyUtils;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.network.message.EntityState;

public abstract class ServerEntity {
    public short id;
    protected final Vector2 position;
    ActorType actorType;
    protected WorldBodyUtils world;

    public Body body;

    protected ServerEntity(short id, Vector2 position, ActorType actorType, WorldBodyUtils world) {
        this.id = id;
        this.position = position;
        this.actorType = actorType;
        this.world = world;
        this.body = createBody(position, world);
    }

    protected abstract Body createBody(Vector2 position, WorldBodyUtils world);

    public void updateState(EntityState state) {
        state.id = id;
        state.type = actorType.toByte();
        state.x = body.getPosition().x;
        state.y = body.getPosition().y;
    }

    public void addKill() {
    }

    public void reduceKill() {
    }

    public abstract void update(float delta);

    public abstract float getWidth();

    public abstract void dispose();
}
