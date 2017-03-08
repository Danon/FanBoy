package com.fanboy.renderer.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.TimeUtils;
import com.fanboy.ScreenChanger;
import com.fanboy.game.Event;
import com.fanboy.game.manager.WorldManager;
import com.fanboy.network.message.ClientDetailsMessage;
import com.fanboy.network.message.ControlsMessage;
import com.fanboy.pool.MessagePool;

public class ServerWorldRenderer extends WorldRenderer {
    protected final WorldManager worldManager;

    public ServerWorldRenderer(ScreenChanger screenChanger, WorldManager worldManager) {
        super(screenChanger);

        this.worldManager = worldManager;
        this.worldManager.setOutgoingEventListener(stateProcessor);
    }

    @Override
    protected void onLoadLevel(ClientDetailsMessage clientDetails) {
        MapLayer collision = map.getLayers().get("collision");
        for (MapObject object : collision.getObjects()) {
            worldManager.createWorldObject(object);
        }

        Event event = MessagePool.instance.eventPool.obtain();
        event.set(Event.State.RECEIVED, clientDetails);
        worldManager.addIncomingEvent(event);
    }

    @Override
    protected void renderObjects(float delta) {
        postRenderObjects(
                delta,
                TimeUtils.nanoTime(),
                1,
                stateProcessor.stateQueue.get(stateProcessor.stateQueue.size() - 1)
        );
    }

    void onControlsChange(ControlsMessage message) {
        Event event = MessagePool.instance.eventPool.obtain();
        event.set(Event.State.RECEIVED, message);
        worldManager.addIncomingEvent(event);
    }
}
