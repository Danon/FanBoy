package com.fanboy.network;

import com.fanboy.control.InputControl;
import com.fanboy.network.message.ControlsMessage;
import com.fanboy.pool.MessagePool;

public class ControlsMessageFactory {
    public ControlsMessage fromInputControl(InputControl controls) {
        ControlsMessage message = MessagePool.instance.controlsMessagePool.obtain();
        message.up = controls.axisUp();
        message.down = controls.axisDown();
        message.left = controls.axisLeft();
        message.right = controls.axisRight();
        message.a = controls.buttonA();
        message.shoot = controls.buttonB();
        message.throwBomb = controls.buttonX();
        return message;
    }
}
