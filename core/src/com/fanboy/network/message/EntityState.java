package com.fanboy.network.message;

import com.fanboy.pool.Poolable;

public class EntityState implements Poolable {
    public short id;
    public byte type;
    public float x, y;
    public float angle;
    public short extra;
    public float vX, vY;

    @Override
    public void reset() {
        id = 0;
        type = 0;
        x = 0;
        y = 0;
        angle = 0;
        extra = 0;
        vX = 0;
        vY = 0;
    }
}
