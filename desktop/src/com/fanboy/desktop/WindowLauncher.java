package com.fanboy.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fanboy.FanBoy;

class WindowLauncher {
    public static void main(String[] arg) {
        new LwjglApplication(new FanBoy(), getConfiguration());
    }

    private static LwjglApplicationConfiguration getConfiguration() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Fan Boy";
        config.width = 1280;
        config.height = 720;
        config.vSyncEnabled = true;
        config.addIcon("icons/icon32.png", Files.FileType.Internal);
        config.forceExit = false;
        config.fullscreen = false;
        return config;
    }
}
