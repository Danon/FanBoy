package com.fanboy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ProfilePreferences {
    private final Preferences preferences;

    public ProfilePreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getName() {
        return preferences.getString("name");
    }

    public void setName(String name) {
        preferences.putString("name", name.trim());
    }

    public boolean isNameEmpty() {
        return !preferences.contains("name") || preferences.getString("name").isEmpty();
    }

    public void save() {
        preferences.flush();
    }

    public static ProfilePreferences getProfilePreferences() {
        return new ProfilePreferences(Gdx.app.getPreferences("profile"));
    }
}
