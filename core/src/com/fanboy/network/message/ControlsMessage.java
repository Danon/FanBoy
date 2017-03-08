package com.fanboy.network.message;

import com.fanboy.pool.Poolable;

public class ControlsMessage implements Poolable {
    public boolean up, down, left, right, shoot, a, throwBomb;

    public ControlsMessage() {
    }

    public ControlsMessage(ControlsMessage message) {
        this.up = message.up;
        this.down = message.down;
        this.left = message.left;
        this.right = message.right;
        this.shoot = message.shoot;
        this.a = message.a;
        this.throwBomb = message.throwBomb;
    }

    @Override
    public void reset() {
        up = false;
        down = false;
        left = false;
        right = false;
        a = false;
        shoot = false;
        throwBomb = false;
    }

    public boolean hasEqualValues(ControlsMessage message) {
        return message != null &&
                message.up == up &&
                message.up == down &&
                message.left == left &&
                message.right == right &&
                message.a == a &&
                message.shoot == shoot &&
                message.throwBomb == throwBomb;
    }
}
