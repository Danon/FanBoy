package com.fanboy.game;

import com.badlogic.gdx.math.Vector2;

import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_HEIGHT;
import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_WIDTH;

public class Utils {
    public static boolean wrapBody(Vector2 position) {
        boolean wrap = false;
        if (position.x > VIEWPORT_WIDTH) {
            wrap = true;
            position.x -= VIEWPORT_WIDTH;
        } else if (position.x < 0) {
            wrap = true;
            position.x += VIEWPORT_WIDTH;
        }

        if (position.y > VIEWPORT_HEIGHT) {
            position.y -= VIEWPORT_HEIGHT;
            return true;
        }
        if (position.y < 0) {
            position.y += VIEWPORT_HEIGHT;
            return true;
        }

        return wrap;
    }
}
