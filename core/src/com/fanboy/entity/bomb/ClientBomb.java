package com.fanboy.entity.bomb;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fanboy.entity.ClientEntity;
import com.fanboy.pool.AssetLoader;
import com.fanboy.renderer.world.WorldRenderer;

import static com.badlogic.gdx.graphics.g2d.TextureRegion.split;
import static com.fanboy.entity.bomb.ServerBomb.RADIUS;

public class ClientBomb extends ClientEntity {
    private Sprite sprite;
    private Animation explode;

    private float deadTimer = 0f;

    public ClientBomb(short id, Vector2 position, WorldRenderer renderer) {
        super(id, position, renderer);
        sprite = createSprite(position);
        explode = createExplodeAnimation();
    }

    private Animation createExplodeAnimation() {
        Texture explodeTexture = AssetLoader.instance.getTexture("sprites/explosion.png");

        float duration = 0.03f;
        int width = explodeTexture.getWidth() / 7;
        int height = explodeTexture.getHeight();
        return new Animation(duration, split(explodeTexture, width, height)[0]);
    }

    private Sprite createSprite(Vector2 position) {
        Sprite sprite = new Sprite(AssetLoader.instance.getTexture("sprites/bomb.png"));
        sprite.setSize(RADIUS + 5, RADIUS + 5);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        return sprite;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        float x = position.x - sprite.getWidth() / 2;
        float y = position.y - sprite.getHeight() / 2;
        if (destroy) {
            deadTimer += delta;
            if (deadTimer >= 0.21f) {
                remove = true;
            }
            sprite.setSize(70, 70);
            sprite.setRegion(explode.getKeyFrame(deadTimer));
            sprite.flip(false, false);
        }
        drawAll(sprite, batch, x, y);
    }
}
