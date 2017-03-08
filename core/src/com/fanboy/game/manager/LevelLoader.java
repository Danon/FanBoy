package com.fanboy.game.manager;

import com.badlogic.gdx.Gdx;
import com.fanboy.Toaster;

import static java.lang.Float.parseFloat;

public class LevelLoader {
    private final String[] level;
    private final WaveSpawnListener listener;
    private final Toaster toaster;

    private float currentTime = 0;
    private float nextTime = 0;
    private int line = 0;

    LevelLoader(String file, Toaster toaster, WaveSpawnListener listener) {
        this.level = Gdx.files.internal(file).readString().split("\n");
        this.toaster = toaster;
        this.listener = listener;
    }

    void loadNextLine(float delta) {
        currentTime += delta;
        line %= level.length;

        while (shouldSpawn()) {
            if (nextTime == 1000) {
                toaster.toast(level[line++].trim());
            }
            String entity = readNextLine();
            String param[] = readNextLine().split(" ");

            spawnEntity(entity, param);

            currentTime = 0;
            nextTime = Long.parseLong(readNextLine());
        }
    }

    private void spawnEntity(String entity, String[] params) {
        float x = parseFloat(params[0]);
        float y = parseFloat(params[1]);

        if (entity.contentEquals("fly")) {
            listener.spawnFly(x, y);
            System.out.println("Spawning fly");
        }

        if (entity.contentEquals("blob")) {
            System.out.println("Spawning blob");
            listener.spawnBlob(x, y, parseFloat(params[2]));
        }

        if (entity.contentEquals("frog")) {
            System.out.println("Spawning frog");
            listener.spawnFrog(x, y, parseFloat(params[2]));
        }
    }

    private boolean shouldSpawn() {
        if (line < level.length) {
            return listener.isUnderpopulated() || currentTime >= nextTime;
        }

        return false;
    }

    private String readNextLine() {
        line %= level.length;
        String line = level[this.line++].trim();
        while (line.isEmpty() && this.line < level.length) {
            this.line %= level.length;
            line = level[this.line++].trim();
        }
        return line;
    }
}
