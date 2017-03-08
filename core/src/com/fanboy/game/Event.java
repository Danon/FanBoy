package com.fanboy.game;

import com.esotericsoftware.kryonet.Listener;

public class Event {
    public enum State {
        CONNECTED {
            public void invokeListener(Listener listener, MyConnection connection, Object object) {
                listener.connected(connection);
            }
        },
        DISCONNECTED {
            public void invokeListener(Listener listener, MyConnection connection, Object object) {
                listener.disconnected(connection);
            }
        },
        RECEIVED {
            public void invokeListener(Listener listener, MyConnection connection, Object object) {
                listener.received(connection, object);
            }
        };

        protected abstract void invokeListener(Listener listener, MyConnection connection, Object object);
    }

    public State state;
    private Object object;

    public void set(State state, Object object) {
        this.state = state;
        this.object = object;
    }

    public void invokeListener(Listener listener, MyConnection dummyConnection) {
        state.invokeListener(listener, dummyConnection, this.object);
    }
}
