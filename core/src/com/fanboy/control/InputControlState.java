package com.fanboy.control;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

public class InputControlState implements InputControl {
    public static InputControlState instance = new InputControlState();

    @Override
    public boolean axisLeft() {
        return getState().axisLeft();
    }

    @Override
    public boolean axisRight() {
        return getState().axisRight();
    }

    @Override
    public boolean axisUp() {
        return getState().axisUp();
    }

    @Override
    public boolean axisDown() {
        return getState().axisDown();
    }

    @Override
    public boolean buttonA() {
        return getState().buttonA();
    }

    @Override
    public boolean buttonB() {
        return getState().buttonB();
    }

    @Override
    public boolean buttonX() {
        return getState().buttonX();
    }

    @Override
    public boolean buttonEnter() {
        return getState().buttonEnter();
    }

    @Override
    public boolean closeButton() {
        return getState().closeButton();
    }

    private InputControl getState() {
        Array<Controller> controllers = Controllers.getControllers();
        if (controllers.size > 0) {
            return new ControllerInput(controllers.first());
        }
        return new GdxInput();
    }
}
