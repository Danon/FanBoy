package com.fanboy.entity.bullet;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fanboy.entity.ClientEntity;
import com.fanboy.pool.AssetLoader;
import com.fanboy.renderer.world.WorldRenderer;

public class ClientBullet extends ClientEntity {
    private final static float RADIUS = 3.5f;
    private final Sprite sprite;

    public ClientBullet(short id, float x, float y, WorldRenderer renderer) {
        super(id, x, y, renderer);
        sprite = createSprite(x, y);
        renderer.audioPlayer.shoot();
    }

    private Sprite createSprite(final float x, final float y) {
        Sprite sprite = new Sprite(AssetLoader.instance.getTexture("sprites/bullet.png"));
        sprite.setSize(RADIUS * 4, RADIUS * 1.5f);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        return sprite;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        sprite.setRotation(angle * MathUtils.radiansToDegrees);

        Vector2 position = getTopLeftPosition();
        drawAll(sprite, batch, position.x, position.y);
    }

    private Vector2 getTopLeftPosition() {
        return new Vector2(
                getPosition().x - sprite.getWidth() / 2,
                getPosition().y - sprite.getHeight() / 2
        );
    }
}
