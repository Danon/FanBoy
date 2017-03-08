package com.fanboy.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.fanboy.entity.ClientEntity;
import com.fanboy.pool.AssetLoader;
import com.fanboy.renderer.world.WorldRenderer;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.graphics.g2d.TextureRegion.split;
import static com.fanboy.entity.player.ServerPlayer.HEIGHT;
import static com.fanboy.entity.player.ServerPlayer.WIDTH;

public class ClientPlayer extends ClientEntity {
    private Sprite sprite;
    private Animation walk;

    private float walkDuration = 0;
    private boolean previousXFlip;

    public ClientPlayer(short id, float x, float y, WorldRenderer renderer) {
        super(id, x, y, renderer);
        Texture texture = AssetLoader.instance.getTexture("sprites/player.png");
        sprite = new Sprite(texture);
        walk = createWalkAnimation(texture);
    }

    private Animation createWalkAnimation(Texture texture) {
        int width = texture.getWidth() / 10;
        int height = texture.getHeight();

        Animation animation = new Animation(0.05f, split(texture, width, height)[0]);
        animation.setPlayMode(LOOP);
        return animation;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        walkDuration += delta;
        renderPlayer(batch);
    }

    private void renderPlayer(SpriteBatch batch) {
        angle *= MathUtils.radiansToDegrees;

        if (velocityY != 0) {
            walkDuration = 0.49f;
        }
        if (Math.abs(velocityX) > 0.4f) {
            sprite.setRegion(walk.getKeyFrame(walkDuration));
        } else {
            sprite.setRegion(walk.getKeyFrame(0.49f));
        }
        if ((extra & 0x1) == 0) {
            sprite.setAlpha(0.5f);
        } else {
            sprite.setAlpha(1);
        }

        if (angle < -90.1f || angle > 90.1f) {
            previousXFlip = true;
        } else if (angle > -89.9f && angle < 89.9f) {
            previousXFlip = false;
        }

        sprite.flip(previousXFlip, false);
        sprite.setSize(WIDTH + 6f, HEIGHT + 1f);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);

        float x = position.x - sprite.getWidth() / 2;
        float y = position.y - sprite.getHeight() / 2 + ServerPlayer.Y_OFFSET;

        drawAll(sprite, batch, x, y);

        renderer.hudRenderer.render(batch, x, y, extra, renderer.stateProcessor.playerNames.players.get(getId()));
    }
}
