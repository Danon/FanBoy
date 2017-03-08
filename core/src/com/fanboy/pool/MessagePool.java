package com.fanboy.pool;

import com.fanboy.game.Event;
import com.fanboy.network.message.*;

public class MessagePool {
    public static final MessagePool instance = new MessagePool();

    public Pool<GameStateMessage> gameStateMessagePool = new Pool<>(GameStateMessage::new, 512);
    public Pool<ControlsMessage> controlsMessagePool = new Pool<>(ControlsMessage::new);
    public Pool<ConnectMessage> connectMessagePool = new Pool<>(ConnectMessage::new);
    public Pool<AudioMessage> audioMessagePool = new Pool<>(AudioMessage::new);
    public Pool<EntityState> entityStatePool = new Pool<>(EntityState::new, 1024);
    public Pool<Event> eventPool = new Pool<>(Event::new);
}
