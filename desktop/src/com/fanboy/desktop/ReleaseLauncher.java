package com.fanboy.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fanboy.FanBoy;

import static com.fanboy.util.error.ExceptionMessage.messageForException;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static org.lwjgl.Sys.alert;

class ReleaseLauncher {
    public static void main(String[] arg) {
        setDefaultUncaughtExceptionHandler((thread, exception) -> alert("Exception", messageForException(exception)));

        new LwjglApplication(new FanBoy(), getConfiguration());
    }

    private static LwjglApplicationConfiguration getConfiguration() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Fan Boy";
        config.width = 1920;
        config.height = 1080;
        config.vSyncEnabled = true;
        config.addIcon("icons/icon32.png", Files.FileType.Internal);
        config.forceExit = false;
        config.fullscreen = true;
        return config;
    }
}
