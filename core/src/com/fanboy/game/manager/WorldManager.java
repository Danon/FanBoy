package com.fanboy.game.manager;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.fanboy.Constants;
import com.fanboy.entity.ServerEntity;
import com.fanboy.entity.blob.ServerBlob;
import com.fanboy.entity.fly.ServerFly;
import com.fanboy.entity.frog.ServerFrog;
import com.fanboy.entity.player.ServerPlayer;
import com.fanboy.game.Event;
import com.fanboy.game.MyConnection;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.game.manager.physics.World;
import com.fanboy.network.DisconnectedClientDetailsException;
import com.fanboy.network.message.*;
import com.fanboy.pool.MessagePool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.fanboy.game.Event.State.RECEIVED;

public abstract class WorldManager implements WaveSpawnListener {
    public AudioMessage audio = new AudioMessage();
    public short id = 0;

    ConcurrentHashMap<Integer, ServerPlayer> playerList = new ConcurrentHashMap<>();
    List<ServerEntity> entities = new ArrayList<>();

    private WorldBodyUtils worldBodyUtils;
    private MyConnection dummyConnection = new MyConnection();
    private List<Event> outgoingEventQueue = new ArrayList<>();
    private List<Event> incomingEventQueue = new ArrayList<>();
    private Listener outgoingEventListener;
    protected Listener serverListener = new WorldManagerServerListener();
    private World world = new World(new Vector2(0, -500f));

    private RespawnPositions positions = new RespawnPositions();

    public WorldManager() {
        Event incoming = MessagePool.instance.eventPool.obtain();
        Event outgoing = MessagePool.instance.eventPool.obtain();

        incoming.set(Event.State.CONNECTED, null);
        outgoing.set(Event.State.CONNECTED, null);

        incomingEventQueue.add(incoming);
        outgoingEventQueue.add(outgoing);

        worldBodyUtils = new WorldBodyUtils(this);
    }

    public void setOutgoingEventListener(Listener listener) {
        outgoingEventListener = listener;
    }

    public void update(float delta) {
        audio.reset();
        onPreUpdate(delta);

        for (ServerEntity entity : entities) {
            entity.update(delta);
        }
        world.step(delta, 1, this);

        GameStateMessage gameStateMessage = MessagePool.instance.gameStateMessagePool.obtain();
        for (ServerEntity entity : entities) {
            EntityState state = MessagePool.instance.entityStatePool.obtain();
            entity.updateState(state);
            gameStateMessage.addNewState(state);
        }

        entities.addAll(worldBodyUtils.entities);
        worldBodyUtils.entities.clear();

        gameStateMessage.time = TimeUtils.nanoTime();
        onGameStateUpdate(gameStateMessage);

        Event gameStateEvent = MessagePool.instance.eventPool.obtain();
        gameStateEvent.set(RECEIVED, gameStateMessage);

        Event audioEvent = MessagePool.instance.eventPool.obtain();
        audioEvent.set(RECEIVED, audio);

        addOutgoingEvent(gameStateEvent);
        addOutgoingEvent(audioEvent);

        processEvents(serverListener, incomingEventQueue);
        processEvents(outgoingEventListener, outgoingEventQueue);
    }

    protected abstract void onPreUpdate(float delta);

    protected abstract void onGameStateUpdate(GameStateMessage gameStateMessage);

    public World getWorld() {
        return world;
    }

    private class WorldManagerServerListener extends Listener {
        @Override
        public void connected(Connection connection) {
        }

        @Override
        public void received(Connection connection, Object object) {
            try {
                if (object instanceof ControlsMessage) {
                    updateControls(connection, (ControlsMessage) object);
                }
                if (object instanceof ClientDetailsMessage) {
                    updateClientDetails(connection, (ClientDetailsMessage) object);
                }
            } catch (DisconnectedClientDetailsException e) {
                ServerPlayer player = new ServerPlayer(id++, positions.getNext(), worldBodyUtils);

                playerList.put(connection.getID(), player);
                entities.add(player);
                if (object instanceof ClientDetailsMessage) {
                    updateClientDetails(connection, (ClientDetailsMessage) object);
                }
            }
        }

        @Override
        public void disconnected(Connection connection) {
            ServerPlayer player = playerList.get(connection.getID());
            if (player != null) {
                player.dispose();
                playerList.remove(connection.getID());
                entities.remove(player);
                onPlayerRemove(player);
                Event event = MessagePool.instance.eventPool.obtain();
                event.set(RECEIVED, player.id);
                addOutgoingEvent(event);
            }
        }
    }

    protected abstract void onPlayerRemove(ServerPlayer player);

    private void updateControls(Connection connection, ControlsMessage object) {
        if (!playerList.containsKey(connection.getID())) {
            throw new DisconnectedClientDetailsException();
        }

        playerList.get(connection.getID()).setCurrentControls(object);
    }

    private void updateClientDetails(Connection connection, ClientDetailsMessage message) {
        if (message.protocolVersion != Constants.PROTOCOL_VERSION) {
            onClientDetailReject(connection, message);
            return;
        }

        if (!playerList.containsKey(connection.getID())) {
            throw new DisconnectedClientDetailsException();
        }
        playerList.get(connection.getID()).setName(message.name);
        PlayerNamesMessage players = new PlayerNamesMessage();
        for (ServerPlayer tempPlayer : playerList.values()) {
            players.players.put(tempPlayer.id, tempPlayer.getName());
        }
        onPlayerListUpdate(players);

        Event event = MessagePool.instance.eventPool.obtain();
        event.set(RECEIVED, players);
        addOutgoingEvent(event);
    }

    protected abstract void onClientDetailReject(Connection connection, ClientDetailsMessage message);

    protected abstract void onPlayerListUpdate(PlayerNamesMessage players);

    private void processEvents(Listener listener, Collection<Event> eventQueue) {
        eventQueue.forEach(event -> event.invokeListener(listener, dummyConnection));
        eventQueue.clear();
    }

    public void addIncomingEvent(Event event) {
        incomingEventQueue.add(event);
    }

    protected void addOutgoingEvent(Event event) {
        outgoingEventQueue.add(event);
    }

    public void createWorldObject(MapObject object) {
        worldBodyUtils.createWorldObject(object);
    }

    public void destroyBody(Body body) {
        entities.remove(body.getUserData());
        onBodyDestroy(body);
        Event event = MessagePool.instance.eventPool.obtain();
        event.set(RECEIVED, body.getUserData().id);
        addOutgoingEvent(event);
    }

    protected abstract void onBodyDestroy(Body body);

    public void dispose() {
    }

    @Override
    public void spawnFly(float x, float y) {
        ServerFly fly = new ServerFly(id++, new Vector2(x, y), worldBodyUtils);
        entities.add(fly);
    }

    @Override
    public void spawnBlob(float x, float y, float direction) {
        ServerBlob blob = new ServerBlob(id++, new Vector2(x, y), worldBodyUtils);
        blob.setDirection(direction);

        entities.add(blob);
    }

    @Override
    public void spawnFrog(float x, float y, float direction) {
        ServerFrog frog = new ServerFrog(id++, new Vector2(x, y), worldBodyUtils);
        entities.add(frog);
    }

    @Override
    public boolean isUnderpopulated() {
        return entities.size() <= playerList.size();
    }
}
