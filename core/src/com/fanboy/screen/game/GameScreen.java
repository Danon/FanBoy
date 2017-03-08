package com.fanboy.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.fanboy.ProfilePreferences;
import com.fanboy.ScreenChanger;
import com.fanboy.Toaster;
import com.fanboy.renderer.world.WorldRenderer;

public abstract class GameScreen implements Screen {
    protected WorldRenderer renderer;
    protected Toaster toaster = new Toaster();
    protected ScreenChanger screenChanger;

    public GameScreen(ScreenChanger changer) {
        this.screenChanger = changer;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        beforeRender(delta);

        renderer.render(delta);
        if (renderer.stateProcessor.disconnected) {
            toaster.toast("server disconnected");
            screenChanger.disconnected();
        }
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void show() {
    }

    protected abstract void beforeRender(float delta);

    public abstract void loadLevel(String level, String host, ProfilePreferences preferences);

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
        }
    }
}
