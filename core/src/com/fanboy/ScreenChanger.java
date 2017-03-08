package com.fanboy;

import com.badlogic.gdx.Screen;

public interface ScreenChanger {
    void mainMenu();

    void setScreen(Screen screen);

    void startServer(boolean lonely);

    void disconnected();

    void clientDiscoveryScreen();
}
