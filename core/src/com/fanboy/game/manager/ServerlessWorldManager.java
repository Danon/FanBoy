package com.fanboy.game.manager;

import com.esotericsoftware.kryonet.Connection;
import com.fanboy.Toaster;
import com.fanboy.entity.player.ServerPlayer;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.network.message.ClientDetailsMessage;
import com.fanboy.network.message.GameStateMessage;
import com.fanboy.network.message.PlayerNamesMessage;

public class ServerlessWorldManager extends WorldManager {
    public final LevelReaderAndEntitySpawner loader;

    public ServerlessWorldManager(Toaster services) {
        super();
        loader = new LevelReaderAndEntitySpawner("maps/retro.txt", services, this);
    }

    @Override
    protected void onPreUpdate(float delta) {
        loader.loadNextLine(delta);
    }

    @Override
    protected void onGameStateUpdate(GameStateMessage gameStateMessage) {
    }

    @Override
    protected void onPlayerRemove(ServerPlayer player) {
    }

    @Override
    protected void onClientDetailReject(Connection connection, ClientDetailsMessage message) {
    }

    @Override
    protected void onPlayerListUpdate(PlayerNamesMessage players) {
    }

    @Override
    protected void onBodyDestroy(Body body) {
    }
}
