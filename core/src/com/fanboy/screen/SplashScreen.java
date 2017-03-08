package com.fanboy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fanboy.Fonts;
import com.fanboy.ScreenChanger;

public class SplashScreen implements Screen {
    private final BitmapFont font;
    private final SpriteBatch batch = new SpriteBatch();
    private final OrthographicCamera camera = new OrthographicCamera();
    private final FitViewport viewport = new FitViewport(1920, 1080, camera);
    private final Fonts fonts;
    private final ScreenChanger changer;

    private float totalTime = 0;

    public SplashScreen(Fonts fonts, ScreenChanger changer) {
        this.fonts = fonts;
        this.changer = changer;
        this.camera.setToOrtho(false, 1920, 1080);
        this.font = fonts.getFont(200);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "Fan Boy", viewport.getWorldWidth() / 2 - 350, viewport.getWorldHeight() - 120);
        batch.end();
        if (totalTime < .2f) {
            totalTime += delta;
        } else {
            changer.setScreen(new MainMenuScreen(fonts, changer));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void show() {
    }

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
        font.dispose();
        batch.dispose();
    }
}
