package com.fanboy;

import com.badlogic.gdx.Gdx;

public class Toaster {
    public void toast(String message) {
        Gdx.app.log("Toast", message);
    }

    public void shortToast(String message) {
        Gdx.app.log("Toast", message);
    }
}
