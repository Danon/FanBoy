package com.fanboy.screen.game;

import com.esotericsoftware.kryonet.Client;
import com.fanboy.ProfilePreferences;
import com.fanboy.ScreenChanger;
import com.fanboy.network.NetworkRegister;
import com.fanboy.renderer.world.ClientWorldRenderer;

import java.io.IOException;

import static com.fanboy.Constants.*;

public class ClientGameScreen extends GameScreen {
    public ClientGameScreen(ScreenChanger changer) {
        super(changer);
        show();
    }

    @Override
    public void loadLevel(String level, String host, ProfilePreferences preferences) {
        Client client = new Client();
        NetworkRegister.register(client);
        client.start();
        try {
            client.connect(TIMEOUT, host, GAME_TCP_PORT, GAME_UDP_PORT);
        } catch (IOException e) {
            throw new ClientConnectionFailedException(e);
        }

        renderer = new ClientWorldRenderer(screenChanger, client);
        renderer.loadLevel(level, preferences);
    }

    @Override
    protected void beforeRender(float delta) {
    }
}
