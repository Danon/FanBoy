package com.fanboy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.fanboy.pool.AssetLoader;
import com.fanboy.screen.ClientDiscoveryScreen;
import com.fanboy.screen.MainMenuScreen;
import com.fanboy.screen.SplashScreen;
import com.fanboy.screen.game.ServerGameScreen;

import static com.fanboy.ProfilePreferences.getProfilePreferences;

public class FanBoy extends ApplicationAdapter implements ScreenChanger {
    private Screen currentScreen;
    private Fonts fonts;

    private int width;
    private int height;

    private Pixmap cursorPixmap;

    @Override
    public void create() {
        AssetLoader.instance.loadAll();

        cursorPixmap = new Pixmap(Gdx.files.internal("images/cursor.png"));
        fonts = new Fonts();
        currentScreen = new SplashScreen(fonts, this);

        Gdx.input.setCursorImage(cursorPixmap, 0, 0);
    }

    @Override
    public void render() {
        currentScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        this.currentScreen.resize(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void pause() {
        currentScreen.pause();
    }

    @Override
    public void resume() {
        currentScreen.resume();
    }

    @Override
    public void dispose() {
        currentScreen.dispose();
        fonts.dispose();
        cursorPixmap.dispose();
    }

    @Override
    public void setScreen(Screen screen) {
        currentScreen.dispose();
        currentScreen = screen;
        currentScreen.resize(width, height);
    }

    @Override
    public void startServer(boolean lonely) {
        ServerGameScreen gameScreen = new ServerGameScreen(this, lonely);
        gameScreen.loadLevel("maps/retro.tmx", "localhost", getProfilePreferences());
        setScreen(gameScreen);
    }

    @Override
    public void disconnected() {
        setScreen(new MainMenuScreen(fonts, this));
    }

    @Override
    public void clientDiscoveryScreen() {
        setScreen(new ClientDiscoveryScreen(fonts, this));
    }

    @Override
    public void mainMenu() {
        setScreen(new MainMenuScreen(fonts, this));
    }
}
