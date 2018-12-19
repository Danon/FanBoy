package com.fanboy.control;

import com.badlogic.gdx.Gdx;

import static com.badlogic.gdx.Input.Keys.*;

public class GdxInput implements InputControl {
    public boolean axisLeft() {
        return Gdx.input.isKeyPressed(A) || Gdx.input.isKeyPressed(LEFT);
    }

    public boolean axisRight() {
        return Gdx.input.isKeyPressed(D) || Gdx.input.isKeyPressed(RIGHT);
    }

    public boolean axisUp() {
        return Gdx.input.isKeyPressed(W) || Gdx.input.isKeyPressed(UP);
    }

    public boolean axisDown() {
        return Gdx.input.isKeyPressed(S) || Gdx.input.isKeyPressed(DOWN);
    }

    public boolean buttonEnter() {
        return Gdx.input.isKeyJustPressed(ENTER);
    }

    public boolean buttonA() {
        return Gdx.input.isKeyPressed(Z);
    }

    public boolean buttonB() {
        return Gdx.input.isKeyPressed(C);
    }

    public boolean buttonX() {
        return Gdx.input.isKeyPressed(X);
    }

    public boolean closeButton() {
        return Gdx.input.isKeyPressed(BACK) || Gdx.input.isKeyJustPressed(ESCAPE);
    }
}
