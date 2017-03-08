package com.fanboy.game.manager;

import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.fanboy.entity.player.ServerPlayer;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.network.message.ClientDetailsMessage;
import com.fanboy.network.message.GameStateMessage;
import com.fanboy.network.message.PlayerNamesMessage;
import com.fanboy.network.message.ServerStatusMessage;

public class ServerWorldManager extends WorldManager {
    private final Server server;

    public ServerWorldManager(Server server) {
        super();
        this.server = server;
        this.server.addListener(serverListener);
    }

    @Override
    protected void onPreUpdate(float delta) {
    }

    @Override
    protected void onGameStateUpdate(GameStateMessage gameStateMessage) {
        gameStateMessage.time = TimeUtils.nanoTime();

        server.sendToAllTCP(gameStateMessage);
        if (audio.audio != 0) {
            server.sendToAllUDP(audio);
        }
    }

    @Override
    protected void onPlayerRemove(ServerPlayer player) {
        server.sendToAllTCP(player.id);
    }

    @Override
    protected void onClientDetailReject(Connection connection, ClientDetailsMessage message) {
        server.sendToTCP(connection.getID(), ServerStatusMessage.mismatchVersionMessage(message.protocolVersion));
    }

    @Override
    protected void onPlayerListUpdate(PlayerNamesMessage players) {
        server.sendToAllTCP(players);
    }

    @Override
    protected void onBodyDestroy(Body body) {
        server.sendToAllTCP(body.getUserData().id);
    }

    @Override
    public void dispose() {
        server.stop();
    }
}
