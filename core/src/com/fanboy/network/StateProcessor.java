package com.fanboy.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.fanboy.Toaster;
import com.fanboy.entity.bomb.ClientBomb;
import com.fanboy.entity.ClientEntity;
import com.fanboy.network.message.AudioMessage;
import com.fanboy.network.message.GameStateMessage;
import com.fanboy.network.message.PlayerNamesMessage;
import com.fanboy.network.message.ServerStatusMessage;
import com.fanboy.pool.MessagePool;
import com.fanboy.sound.SoundPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fanboy.network.message.ServerStatusMessage.Status.DISCONNECT;

public class StateProcessor extends Listener {
    private static final int QUEUE_LENGTH = 6;

    private final Toaster toaster = new Toaster();

    public List<GameStateMessage> stateQueue = new ArrayList<>();
    public PlayerNamesMessage playerNames = new PlayerNamesMessage();
    public boolean disconnected = false;
    public long timeOffset = 0;

    private ConcurrentHashMap<Short, ClientEntity> world;
    private GameStateMessage nextState;
    private AtomicBoolean wait = new AtomicBoolean(false);
    private SoundPlayer audioPlayer;
    private int lag = 0;

    public StateProcessor(ConcurrentHashMap<Short, ClientEntity> worldMap, SoundPlayer audioPlayer) {
        this.nextState = MessagePool.instance.gameStateMessagePool.obtain();
        this.nextState.time = 0;
        this.world = worldMap;
        this.audioPlayer = audioPlayer;
    }

    @Override
    public void connected(Connection connection) {
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof GameStateMessage) {
            addNewState((GameStateMessage) object);
        }
        if (object instanceof Short) {
            if (world.get(object) != null) {
                world.get(object).destroy = true;
                //FIXME Set remove in the client entity
                if (!(world.get(object) instanceof ClientBomb)) {
                    world.get(object).remove = true;
                }
            }
        }
        if (object instanceof AudioMessage) {
            audioPlayer.playAudioMessage((AudioMessage) object);
        }
        if (object instanceof PlayerNamesMessage) {
            playerNames = (PlayerNamesMessage) object;
        }
        if (object instanceof ServerStatusMessage) {
            ServerStatusMessage message = (ServerStatusMessage) object;
            toaster.toast(message.text);
            disconnected = message.status == DISCONNECT;
        }
        if (object instanceof String) {
            toaster.toast((String) object);
        }
        super.received(connection, object);
    }

    @Override
    public void disconnected(Connection connection) {
        disconnected = true;
    }

    private void addNewState(GameStateMessage state) {
        if (wait == null) {
            wait = new AtomicBoolean(false);
        }
        if (stateQueue == null) {
            stateQueue = new ArrayList<>();
        }
        while (!wait.compareAndSet(false, true)) ;

        if (stateQueue.size() == 0) {
            stateQueue.add(state);
        }
        for (int i = stateQueue.size() - 1; i >= 0; i--) {
            if (stateQueue.get(i).time < state.time) {
//                Gdx.app.log("inserted at ", Integer.toString(i + 1));
                stateQueue.add(i + 1, state);
                break;
            }
        }
        wait.set(false);
    }

    public void processStateQueue(long currentTime) {
        while (!wait.compareAndSet(false, true)) ;
        if (stateQueue.size() < QUEUE_LENGTH) {
            wait.set(false);
            return;
        }

        while (stateQueue.size() > QUEUE_LENGTH) {
            stateQueue.remove(0);
        }

        long currentServerTime = currentTime + timeOffset;
        if (currentServerTime < stateQueue.get(0).time) {
            lag++;
            if (lag > 3) {
                lag = 0;
                timeOffset = stateQueue.get(QUEUE_LENGTH - 2).time - currentTime;
                currentServerTime = currentTime + timeOffset;
            }
        } else if (currentServerTime > stateQueue.get(QUEUE_LENGTH - 1).time) {
            lag++;
            if (lag > 3) {
                lag = 0;
                timeOffset -= 10000;
                currentServerTime = currentTime + timeOffset;
            }
        } else {
            lag = 0;
        }

        for (GameStateMessage state : stateQueue) {
            this.nextState = state;
            if (state.time > currentServerTime) {
                break;
            }
        }

        wait.set(false);
    }

    public GameStateMessage getNextState() {
        return nextState;
    }
}
