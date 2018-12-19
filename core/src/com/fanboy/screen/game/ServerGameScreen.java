package com.fanboy.screen.game;

import com.esotericsoftware.kryonet.Server;
import com.fanboy.ProfilePreferences;
import com.fanboy.ScreenChanger;
import com.fanboy.game.manager.ServerWorldManager;
import com.fanboy.game.manager.ServerlessWorldManager;
import com.fanboy.game.manager.WorldManager;
import com.fanboy.network.NetworkRegister;
import com.fanboy.renderer.world.ServerWorldRenderer;

import static com.fanboy.Constants.GAME_TCP_PORT;
import static com.fanboy.Constants.GAME_UDP_PORT;

public class ServerGameScreen extends GameScreen {
    private Server server;
    private WorldManager world;

    public ServerGameScreen(ScreenChanger changer, boolean lonely) {
        super(changer);

        if (!lonely) {
            server = new Server();
            NetworkRegister.register(server);
            server.start();
            try {
                server.bind(GAME_TCP_PORT, GAME_UDP_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadLevel(String level, String host, ProfilePreferences preferences) {
        if (server == null) {
            world = new ServerlessWorldManager(toaster);
        } else {
            world = new ServerWorldManager(server);
        }

        renderer = new ServerWorldRenderer(screenChanger, world);
        renderer.loadLevel(level, preferences);
    }

    @Override
    protected void beforeRender(float delta) {
        world.update(delta);
    }

    @Override
    public void dispose() {
        if (world != null) {
            world.dispose();
        }
        super.dispose();
    }
}
