package com.fanboy.entity.fly;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fanboy.entity.blob.ServerBlob;
import com.fanboy.entity.ClientEntity;
import com.fanboy.pool.AssetLoader;
import com.fanboy.renderer.world.WorldRenderer;

public class ClientFly extends ClientEntity {
    private final Sprite sprite;
    private final Animation walk;

    private float walkDuration;
    private boolean previousXFlip;

    public ClientFly(short id, float x, float y, WorldRenderer renderer) {
        super(id, x, y, renderer);
        Texture texture = AssetLoader.instance.getTexture("sprites/fly.png");
        sprite = createSprite(texture);
        walk = createWalkAnimation(texture);
    }

    private Sprite createSprite(Texture texture) {
        Sprite sprite = new Sprite(texture);
        sprite.setSize(ServerBlob.WIDTH + 5f, ServerFly.HEIGHT);
        return sprite;
    }

    private Animation createWalkAnimation(Texture texture) {
        Animation walk = new Animation(0.25f, TextureRegion.split(texture, texture.getWidth() / 2, texture.getHeight())[0]);
        walk.setPlayMode(Animation.PlayMode.LOOP);
        return walk;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        if ((extra & 0x1) == 1) {
            walkDuration += delta;
            if (velocityX > 15f) {
                sprite.setRegion(walk.getKeyFrame(walkDuration));
                previousXFlip = false;
            } else if (velocityX < -15f) {
                sprite.setRegion(walk.getKeyFrame(walkDuration));
                sprite.flip(true, false);
                previousXFlip = true;
            } else {
                sprite.setRegion(walk.getKeyFrame(walkDuration));
                sprite.flip(previousXFlip, false);
            }
        } else {
            sprite.setRegion(AssetLoader.instance.getTexture("sprites/green_loader.png"));
        }

        float x = position.x - sprite.getWidth() / 2 + 1f;
        float y = position.y - sprite.getHeight() / 2 + ServerBlob.Y_OFFSET;
        drawAll(sprite, batch, x, y);
    }
}
