package com.fanboy.network;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.network.message.*;
import com.fanboy.pool.MessagePool;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkRegisterer {
    static public void register(EndPoint endPoint) {
        Registration registration;
        Kryo kryo = endPoint.getKryo();
        registration = kryo.register(ConnectMessage.class);
        registration.setInstantiator(() -> MessagePool.instance.connectMessagePool.obtain());

        registration = kryo.register(ControlsMessage.class);
        registration.setInstantiator(() -> MessagePool.instance.controlsMessagePool.obtain());

        registration = kryo.register(EntityState.class);
        registration.setInstantiator(() -> MessagePool.instance.entityStatePool.obtain());

        registration = kryo.register(GameStateMessage.class);
        registration.setInstantiator(() -> MessagePool.instance.gameStateMessagePool.obtain());

        registration = kryo.register(AudioMessage.class);
        registration.setInstantiator(() -> MessagePool.instance.audioMessagePool.obtain());

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
