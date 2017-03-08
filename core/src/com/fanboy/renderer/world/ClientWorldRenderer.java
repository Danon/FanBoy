package com.fanboy.renderer.world;

import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.kryonet.Client;
import com.fanboy.ScreenChanger;
import com.fanboy.network.message.ClientDetailsMessage;
import com.fanboy.network.message.ControlsMessage;
import com.fanboy.network.message.GameStateMessage;

public class ClientWorldRenderer extends WorldRenderer {
    private final Client client;

    public ClientWorldRenderer(ScreenChanger changer, Client client) {
        super(changer);

        this.client = client;
        this.client.addListener(stateProcessor);
    }

    @Override
    protected void onLoadLevel(ClientDetailsMessage clientDetails) {
        client.sendTCP(clientDetails);
    }

    @Override
    void onControlsChange(ControlsMessage message) {
        client.sendUDP(message);
    }

    @Override
    protected void renderObjects(float delta) {
        long currentTime = TimeUtils.nanoTime();
        stateProcessor.processStateQueue(currentTime);
        currentTime += stateProcessor.timeOffset;
        GameStateMessage nextStateMessage = stateProcessor.getNextState();

        postRenderObjects(delta, currentTime, getAlpha(currentTime, nextStateMessage.time), nextStateMessage);
    }

    private float getAlpha(long currentTime, long nextTime) {
        if (currentTime > nextTime) {
            return 1;
        }

        if (nextTime == previousTime) {
            return 0;
        }

        return (float) (currentTime - previousTime) / (float) (nextTime - previousTime);
    }

    @Override
    public void dispose() {
        client.stop();
        super.dispose();
    }
}
