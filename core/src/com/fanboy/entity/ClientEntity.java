package com.fanboy.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fanboy.network.message.EntityState;
import com.fanboy.renderer.world.WorldRenderer;

import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_HEIGHT;
import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_WIDTH;

public abstract class ClientEntity {
    private final short id;
    protected final Vector2 position;
    private final Vector2 previousPosition;
    protected final WorldRenderer renderer;

    protected float angle;
    public boolean destroy = false;
    public boolean remove = false;
    protected float velocityX;
    protected float velocityY;
    protected short extra;
    private Vector2 tempVector = new Vector2();

    protected ClientEntity(short id, Vector2 position, WorldRenderer renderer) {
        this.id = id;
        this.position = position;
        this.previousPosition = new Vector2(position);
        this.renderer = renderer;
    }

    public ClientEntity(short id, float x, float y, WorldRenderer renderer) {
        this(id, new Vector2(x, y), renderer);
    }

    public abstract void render(float delta, SpriteBatch batch);

    public void processState(EntityState nextState, float alpha) {
        previousPosition.set(position);
        if (position.x - nextState.x > 20) {
            position.x -= VIEWPORT_WIDTH / 10.0f;
        } else if (position.x - nextState.x < -20) {
            position.x += VIEWPORT_WIDTH / 10.0f;
        }

        if (position.y - nextState.y > 20) {
            position.y -= VIEWPORT_HEIGHT / 10.0f;
        } else if (position.y - nextState.y < -20) {
            position.y += VIEWPORT_HEIGHT / 10.0f;
        }

        tempVector.set(nextState.x, nextState.y);

        if (position.dst2(tempVector) > 60) {
            position.set(tempVector);
        } else {
            position.lerp(tempVector, alpha);
        }
        angle = nextState.angle;
        velocityX = nextState.vX;
        velocityY = nextState.vY;
        extra = nextState.extra;
    }

    protected void drawAll(Sprite sprite, Batch batch, float x, float y) {
        sprite.setPosition(x, y);
        sprite.draw(batch);
        if (x > VIEWPORT_WIDTH / 2) {
            sprite.setPosition(x - VIEWPORT_WIDTH, y);
        } else {
            sprite.setPosition(x + VIEWPORT_WIDTH, y);
        }
        sprite.draw(batch);

        if (position.y > VIEWPORT_HEIGHT / 2) {
            sprite.setPosition(x, y - VIEWPORT_HEIGHT);
        } else {
            sprite.setPosition(x, y + VIEWPORT_HEIGHT);
        }
        sprite.draw(batch);
    }

    public Vector2 getPosition() {
        return position;
    }

    public short getId() {
        return id;
    }
}
