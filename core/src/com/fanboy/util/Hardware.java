package com.fanboy.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Runtime.getRuntime;

class Hardware {
    public int getUniformSeed() {
        try {
            return getMotherBoardSerialNumber().hashCode();
        } catch (IOException exception) {
            Gdx.app.log("Hardware", "Could not get motherboard serial number");
            return "".hashCode();
        }
    }

    public String getMotherBoardSerialNumber() throws IOException {
        Process process = getRuntime().exec("wmic baseboard get serialnumber");
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = getStringFromReader(input);
        input.close();
        return result;
    }

    private String getStringFromReader(BufferedReader input) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = input.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}
