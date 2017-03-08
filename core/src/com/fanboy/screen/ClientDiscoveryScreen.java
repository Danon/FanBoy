package com.fanboy.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.kryonet.Client;
import com.fanboy.*;
import com.fanboy.game.FocusButton;
import com.fanboy.screen.game.ClientConnectionFailedException;
import com.fanboy.screen.game.ClientGameScreen;
import com.fanboy.screen.game.GameScreen;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ClientDiscoveryScreen implements Screen {
    private final Fonts fonts;
    private final ScreenChanger screenChanger;
    private final Toaster toaster = new Toaster();

    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private List<FocusButton> buttons;
    private FocusButton currentButton;
    private FocusButton backButton;
    private FocusButton refreshButton;
    private FocusButton manualIpButton;
    private List<FocusButton> ipAddresses;
    private boolean markForDispose;
    private Client client;
    private boolean pressedButton;

    public ClientDiscoveryScreen(Fonts fonts, ScreenChanger changer) {
        this.fonts = fonts;
        this.screenChanger = changer;
        show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderButtons(delta);
        batch.end();
        processInput();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void show() {
        client = new Client();
        client.start();
        font = fonts.getFont(50);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);
        camera.setToOrtho(false, 1920, 1080);
        buttons = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        markForDispose = false;
        addAllButtons();
        addIpButtons();
        pressedButton = false;
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
        ipAddresses.clear();
        buttons.clear();
        batch.dispose();
        font.dispose();
        ipAddresses = null;
        buttons = null;
        font = null;
    }

    private void processInput() {
        currentButton = currentButton.process();
        if (InputControl.instance.buttonEnter() || markForDispose) {
            processButton();
        }
    }

    private void processButton() {
        if (currentButton == backButton) {
            screenChanger.setScreen(new MainMenuScreen(fonts, screenChanger));
        } else if (currentButton == refreshButton) {
            addIpButtons();
        } else if (currentButton == manualIpButton) {
            if (pressedButton) {
                return;
            }
            pressedButton = true;

            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input(String text) {
                    joinGame(text);
                }

                @Override
                public void canceled() {
                    pressedButton = false;
                }
            }, "Enter IP", "");
        } else {
            if (!pressedButton) {
                pressedButton = true;
                joinGame(currentButton.getText());
            }
        }
    }

    private FocusButton createButton(String text, float x, float y) {
        FocusButton button = new FocusButton(text, x, y);
        buttons.add(button);
        return button;
    }

    private void renderButtons(float delta) {
        for (FocusButton button : buttons) {
            button.render(batch, font, delta);
        }
        for (FocusButton button : ipAddresses) {
            button.render(batch, font, delta);
        }
    }

    private void addAllButtons() {
        refreshButton = createButton("Refresh", 300, 720);
        backButton = createButton("Back", 700, 720);
        manualIpButton = createButton("Enter IP", 300, 580);
        currentButton = refreshButton;
        currentButton.setActive(true);
        refreshButton.setWest(backButton);
        manualIpButton.setNorth(backButton);
        manualIpButton.setNorth(refreshButton);
    }

    private void addIpButtons() {
        toaster.shortToast("Searching for servers");
        manualIpButton.unsetNorth();
        ipAddresses.clear();
        FocusButton previousButton = refreshButton;
        List<InetAddress> tempAddresses = client.discoverHosts(Constants.DISCOVERY_UDP_PORT, Constants.TIMEOUT);
        if (tempAddresses.size() == 0) {
            toaster.toast("no servers found");
        }

        float y = 580;
        for (InetAddress address : tempAddresses) {
            FocusButton button = new FocusButton(address.getHostName(), 300, y);
            ipAddresses.add(button);
            previousButton.setSouth(button);
            previousButton = button;
            y -= 150;
        }
        manualIpButton.setPosition(300, y);
        manualIpButton.setNorth(previousButton);
    }

    private void joinGame(String host) {
        GameScreen gameScreen = new ClientGameScreen(screenChanger);
        try {
            gameScreen.loadLevel("maps/retro-small.tmx", host, new ProfilePreferences(Gdx.app.getPreferences("profile")));
            screenChanger.setScreen(gameScreen);
        } catch (ClientConnectionFailedException exception) {
            gameScreen.dispose();
        }
    }
}
