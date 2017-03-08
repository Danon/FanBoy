package com.fanboy.pool;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class AssetLoader {
    public static final AssetLoader instance = new AssetLoader();
    private AssetManager manager = new AssetManager();

    public void loadAll() {
        manager.load("sprites/player.png", Texture.class);
        manager.load("sprites/blob.png", Texture.class);
        manager.load("sprites/blob_dead.png", Texture.class);
        manager.load("sprites/fly.png", Texture.class);
        manager.load("sprites/bullet.png", Texture.class);
        manager.load("sprites/bomb.png", Texture.class);
        manager.load("sprites/frog.png", Texture.class);
        manager.load("sprites/explosion.png", Texture.class);
        manager.load("sprites/green_loader.png", Texture.class);
        manager.load("images/indicator.png", Texture.class);
        manager.finishLoading();
    }

    public Texture getTexture(String path) {
        return manager.get(path, Texture.class);
    }
}
