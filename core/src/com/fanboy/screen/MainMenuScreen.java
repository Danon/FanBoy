package com.fanboy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fanboy.Fonts;
import com.fanboy.InputControl;
import com.fanboy.ProfilePreferences;
import com.fanboy.ScreenChanger;
import com.fanboy.game.FocusButton;
import com.fanboy.pool.AssetLoader;
import com.fanboy.util.AnimatedValue;

import java.util.ArrayList;
import java.util.Collection;

import static com.fanboy.ProfilePreferences.getProfilePreferences;

public class MainMenuScreen implements Screen {
    private final ScreenChanger screenChanger;

    private final BitmapFont font;
    private final SpriteBatch batch = new SpriteBatch();
    private final OrthographicCamera camera = new OrthographicCamera();
    private final FitViewport viewport = new FitViewport(1920, 1080, camera);

    private final Collection<FocusButton> buttons = new ArrayList<>();
    private FocusButton currentButton, startGameButton, joinGameButton,
            practiceButton, exitButton;

    private final Sprite indicator;
    private final AnimatedValue indicatorTop = new AnimatedValue(650);

    public MainMenuScreen(Fonts fonts, ScreenChanger screenChanger) {
        this.screenChanger = screenChanger;

        addButtons();
        repromptName();
        camera.setToOrtho(false, 1920, 1080);
        indicator = createIndicatorSprite();
        font = fonts.getFont(50);
    }

    private Sprite createIndicatorSprite() {
        Texture texture = AssetLoader.instance.getTexture("images/indicator.png");
        return new Sprite(texture, 80, 80);
    }

    @Override
    public void render(float delta) {
        renderButtons(delta);
        renderIndicator(delta);
        processInput();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void show() {
    }

    private void repromptName() {
        ProfilePreferences preferences = getProfilePreferences();
        if (preferences.isNameEmpty()) {
            Gdx.input.getTextInput(new NameChangeTextInputListener(preferences), "Enter name", "");
        }
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
        batch.dispose();
        font.dispose();
    }

    private void processInput() {
        FocusButton newCurrentButton = currentButton.process();
        if (currentButton != newCurrentButton) {
            indicatorTop.setValue(newCurrentButton.getY(), 0.25);
            currentButton = newCurrentButton;
        }
        if (InputControl.instance.buttonEnter()) {
            processButton();
        }
    }

    private void processButton() {
        buttons.clear();
        if (currentButton == startGameButton) {
            screenChanger.startServer(false);
        }
        if (currentButton == joinGameButton) {
            screenChanger.clientDiscoveryScreen();
        }
        if (currentButton == practiceButton) {
            screenChanger.startServer(true);
        }
        if (currentButton == exitButton) {
            Gdx.app.exit();
        }
    }

    private void renderButtons(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        buttons.forEach(button -> button.render(batch, font, delta));
        batch.end();
    }

    private void renderIndicator(float delta) {
        indicatorTop.update(delta);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        indicator.setPosition(50, (int) indicatorTop.getValue() - 70);
        indicator.setOrigin(33, 41);
        if (indicatorTop.isFinished()) {
            indicator.setRotation(0);
        } else {
            double rotation = -(indicatorTop.getValue() - 650) / 100.0 * Math.PI * 2 / 3;
            indicator.setRotation(radToDeg(rotation));
        }
        indicator.draw(batch);

        batch.end();
    }

    private float radToDeg(double radians) {
        return (float) (radians / Math.PI * 180);
    }

    private void addButtons() {
        practiceButton = addButton("Single player", 170, 650);
        startGameButton = addButton("Create network game", 170, 550);
        joinGameButton = addButton("Join network game", 170, 450);
        exitButton = addButton("Exit", 170, 350);
        practiceButton.setActive(true);
        currentButton = practiceButton;

        startGameButton.setNorth(practiceButton);
        joinGameButton.setNorth(startGameButton);
        exitButton.setNorth(joinGameButton);
    }

    private FocusButton addButton(String text, float x, float y) {
        FocusButton button = new FocusButton(text, x, y);
        buttons.add(button);
        return button;
    }

    private static class NameChangeTextInputListener implements TextInputListener {
        private final ProfilePreferences preferences;

        NameChangeTextInputListener(ProfilePreferences preferences) {
            this.preferences = preferences;
        }

        @Override
        public void input(String text) {
            preferences.setName(text);
            preferences.save();
        }

        @Override
        public void canceled() {
        }
    }
}
