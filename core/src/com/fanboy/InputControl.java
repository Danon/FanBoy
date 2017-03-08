package com.fanboy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

import static com.badlogic.gdx.Input.Keys.*;

public class InputControl {
    public static final InputControl instance = new InputControl();

    public boolean axisLeft() {
        return Gdx.input.isKeyPressed(A) ||
                (controllerEnabled() && getFirstController().getAxis(8) == -1);
    }

    public boolean axisRight() {
        return Gdx.input.isKeyPressed(D) ||
                (controllerEnabled() && getFirstController().getAxis(8) == 1);
    }

    public boolean axisUp() {
        return Gdx.input.isKeyPressed(W) ||
                (controllerEnabled() && getFirstController().getAxis(9) == -1);
    }

    public boolean axisDown() {
        return Gdx.input.isKeyPressed(S) ||
                (controllerEnabled() && getFirstController().getAxis(9) == 1);
    }

    public boolean buttonEnter() {
        return Gdx.input.isKeyJustPressed(ENTER);
    }

    public boolean buttonA() {
        return Gdx.input.isKeyPressed(Z) ||
                controllerEnabled() && getFirstController().getButton(96);
    }

    public boolean buttonB() {
        return Gdx.input.isKeyPressed(C) ||
                controllerEnabled() && getFirstController().getButton(97);
    }

    public boolean buttonX() {
        return Gdx.input.isKeyPressed(X) ||
                (controllerEnabled() && getFirstController().getButton(99));
    }

    public boolean closeButton() {
        return Gdx.input.isKeyPressed(BACK) || Gdx.input.isKeyJustPressed(ESCAPE);
    }

    private boolean controllerEnabled() {
        return Controllers.getControllers().size > 0;
    }

    private Controller getFirstController() {
        return Controllers.getControllers().get(0);
    }
}
