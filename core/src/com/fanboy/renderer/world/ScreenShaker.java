package com.fanboy.renderer.world;

import com.badlogic.gdx.math.MathUtils;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ScreenShaker {
    private final int intense, diversity;

    private float x = 0, y = 0, remaining = 0;

    public ScreenShaker(int intense, int diversity) {
        this.intense = intense;
        this.diversity = diversity;
    }

    public void shake() {
        remaining = 0.2f;
    }

    public void update(float delta) {
        if (remaining > 0) {
            remaining -= delta;

            float x2 = x + MathUtils.random(-intense, intense);
            float y2 = y + MathUtils.random(-intense, intense);

            x = max(min(x2, diversity), -diversity);
            y = max(min(y2, diversity / 2.0f), -diversity / 2.0f);

            resetIfFinished();
        }
    }

    private void resetIfFinished() {
        if (remaining < 0) {
            remaining = 0;
            x = 0;
            y = 0;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
