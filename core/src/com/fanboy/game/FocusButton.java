package com.fanboy.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Vector2;
import com.fanboy.control.InputControlState;

public class FocusButton {
    private FocusButton north, south, east, west;
    private final String text;
    private boolean active = false;
    private float x, y, slackTime = 0;

    public FocusButton(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.slackTime = 0;
    }

    public void render(Batch batch, BitmapFont font, float delta) {
        if (active) {
            font.setColor(0, 0, 0, 1);
        } else {
            font.setColor(0.5f, 0.5f, 0.5f, 1);
        }
        font.draw(batch, text, x, y);
        slackTime += delta;
    }

    public void setNorth(FocusButton north) {
        this.north = north;
        north.south = this;
    }

    public void setSouth(FocusButton south) {
        this.south = south;
        south.north = this;
    }

    public void setEast(FocusButton east) {
        this.east = east;
        east.west = this;
    }

    public void setWest(FocusButton west) {
        this.west = west;
        west.east = this;
    }

    public FocusButton process() {
        FocusButton pressedButton = null;
        if (slackTime > 0.2f) {
            if (InputControlState.instance.axisDown()) {
                pressedButton = south;
            } else if (InputControlState.instance.axisUp()) {
                pressedButton = north;
            } else if (InputControlState.instance.axisLeft()) {
                pressedButton = east;
            } else if (InputControlState.instance.axisRight()) {
                pressedButton = west;
            }
            if (pressedButton != null) {
                active = false;
                pressedButton.setActive(true);
                return pressedButton;
            }
        }
        return this;
    }

    public boolean isPressed(Vector2 touchVector, BitmapFont font) {
        TextBounds bounds = font.getBounds(text);
        return (x < touchVector.x)
                && (touchVector.x < x + bounds.width)
                && (y > touchVector.y)
                && (touchVector.y > y - bounds.height);
    }

    public String getText() {
        return text;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void unsetNorth() {
        north = null;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
