package com.fanboy.control;

import com.badlogic.gdx.controllers.Controller;

public class ControllerInput implements InputControl {
    private final Controller controller;

    public ControllerInput(Controller controller) {
        this.controller = controller;
    }

    public boolean axisLeft() {
        return controller.getAxis(8) == -1;
    }

    public boolean axisRight() {
        return controller.getAxis(8) == 1;
    }

    public boolean axisUp() {
        return controller.getAxis(9) == -1;
    }

    public boolean axisDown() {
        return controller.getAxis(9) == 1;
    }

    public boolean buttonA() {
        return controller.getButton(96);
    }

    public boolean buttonB() {
        return controller.getButton(97);
    }

    public boolean buttonX() {
        return controller.getButton(99);
    }

    public boolean buttonEnter() {
        return false;
    }

    public boolean closeButton() {
        return false;
    }
}
