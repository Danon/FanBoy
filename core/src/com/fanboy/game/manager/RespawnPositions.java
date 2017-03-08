package com.fanboy.game.manager;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class RespawnPositions {
    private final List<Vector2> positions = new ArrayList<>();
    private int index = 0;

    public RespawnPositions() {
        positions.add(new Vector2(50, 85));
        positions.add(new Vector2(395, 85));
        positions.add(new Vector2(50, 230));
        positions.add(new Vector2(395, 230));
    }

    public Vector2 getNext() {
        index = index + 1 % positions.size();
        return positions.get(index);
    }
}
