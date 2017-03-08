package com.fanboy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Fonts {
    private final FreeTypeFontGenerator generator;
    private final FreeTypeFontParameter parameter = new FreeTypeFontParameter();

    public Fonts() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/splash.ttf"));
    }

    public BitmapFont getFont(int size) {
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        font.setColor(0, 0, 0, 1);
        return font;
    }

    public void dispose() {
        generator.dispose();
    }
}
