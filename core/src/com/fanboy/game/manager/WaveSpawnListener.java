package com.fanboy.game.manager;

public interface WaveSpawnListener {
    void spawnFly(float x, float y);

    void spawnBlob(float x, float y, float direction);

    void spawnFrog(float x, float y, float direction);

    boolean isUnderpopulated();
}
