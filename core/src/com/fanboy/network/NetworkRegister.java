package com.fanboy.network;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.fanboy.network.message.*;
import com.fanboy.pool.MessagePool;

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkRegister {
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(ConnectMessage.class).setInstantiator(MessagePool.instance.connectMessagePool::obtain);
        kryo.register(ControlsMessage.class).setInstantiator(MessagePool.instance.controlsMessagePool::obtain);
        kryo.register(EntityState.class).setInstantiator(MessagePool.instance.entityStatePool::obtain);
        kryo.register(GameStateMessage.class).setInstantiator(MessagePool.instance.gameStateMessagePool::obtain);
        kryo.register(AudioMessage.class).setInstantiator(MessagePool.instance.audioMessagePool::obtain);

        kryo.register(PlayerNamesMessage.class);
        kryo.register(ClientDetailsMessage.class);
        kryo.register(ServerStatusMessage.class);
        kryo.register(ServerStatusMessage.Status.class);
        kryo.register(ArrayList.class);
        kryo.register(Vector2.class);
        kryo.register(String.class);
        kryo.register(HashMap.class);
    }
}
