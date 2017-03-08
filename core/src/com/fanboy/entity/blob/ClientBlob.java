package com.fanboy.entity.blob;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fanboy.entity.ClientEntity;
import com.fanboy.pool.AssetLoader;
import com.fanboy.renderer.world.WorldRenderer;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.graphics.g2d.TextureRegion.split;

public class ClientBlob extends ClientEntity {
    private Sprite sprite;
    private Animation walk;
    private float walkDuration;
    private boolean previousXFlip;
    private float deadTimer = 2f;

    public ClientBlob(short id, float x, float y, WorldRenderer renderer) {
        super(id, x, y, renderer);
        Texture texture = AssetLoader.instance.getTexture("sprites/blob.png");
        sprite = createSprite(texture);
        walk = createWalkAnimation(texture);
    }

    private Animation createWalkAnimation(Texture texture) {
        int width = texture.getWidth() / 2;
        int height = texture.getHeight();
        Animation walk = new Animation(0.25f, split(texture, width, height)[0]);
        walk.setPlayMode(LOOP);
        return walk;
    }

    private Sprite createSprite(Texture texture) {
        Sprite sprite = new Sprite(texture);
        sprite.setSize(ServerBlob.WIDTH + 5f, ServerBlob.HEIGHT);
        return sprite;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        if ((extra & 0x1) == 1) {
            walkDuration += delta;
            if (velocityX < -5f && velocityY == 0) {
                sprite.setRegion(walk.getKeyFrame(walkDuration));
                previousXFlip = false;
            } else if (velocityX > 5f && velocityY == 0) {
                sprite.setRegion(walk.getKeyFrame(walkDuration));
                sprite.flip(true, false);
                previousXFlip = true;
            } else {
                sprite.setRegion(walk.getKeyFrame(0));
                sprite.flip(previousXFlip, false);
            }

            if (destroy) {
                deadTimer -= delta;
                if (deadTimer <= 0) {
                    remove = true;
                }
                sprite.setRegion(AssetLoader.instance.getTexture("sprites/explosion.png"));
                sprite.flip(false, false);
            }
        } else {
            sprite.setRegion(AssetLoader.instance.getTexture("sprites/green_loader.png"));
        }
        float x = position.x - sprite.getWidth() / 2 + 1f;
        float y = position.y - sprite.getHeight() / 2 + ServerBlob.Y_OFFSET;
        drawAll(sprite, batch, x, y);
    }
}
