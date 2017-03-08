package com.fanboy.network.message;

import com.fanboy.pool.Poolable;

import java.util.ArrayList;
import java.util.List;

public class GameStateMessage implements Poolable {
    public List<EntityState> states = new ArrayList<>();
    public long time;

    public void addNewState(EntityState state) {
        states.add(state);
    }

    @Override
    public void reset() {
        states.clear();
    }
}
