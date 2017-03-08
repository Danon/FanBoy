package com.fanboy.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.fanboy.network.message.AudioMessage;

public class SoundPlayer {
    private final Sound jump, shoot, explode, hurt, jumpedOn;

    public SoundPlayer() {
        jump = Gdx.audio.newSound(Gdx.files.internal("sounds/a.ogg"));
        shoot = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.ogg"));
        explode = Gdx.audio.newSound(Gdx.files.internal("sounds/explode.ogg"));
        hurt = Gdx.audio.newSound(Gdx.files.internal("sounds/hurt.ogg"));
        jumpedOn = Gdx.audio.newSound(Gdx.files.internal("sounds/jumpedon.ogg"));
    }

    private void jump() {
        jump.play(0.01f);
    }

    public void shoot() {
        shoot.play(0.01f);
    }

    private void hurt() {
        hurt.play(0.01f);
    }

    private void explode() {
        explode.play(0.01f);
    }

    private void jumpedOn() {
        jumpedOn.play(0.01f);
    }

    public void dispose() {
        jump.dispose();
        shoot.dispose();
        hurt.dispose();
        explode.dispose();
        jumpedOn.dispose();
    }

    public void playAudioMessage(AudioMessage message) {
        if (message.getJump()) {
            jump();
        }
        if (message.getShoot()) {
            shoot();
        }
        if (message.getHurt()) {
            hurt();
        }
        if (message.getExplode()) {
            explode();
        }
        if (message.getJumpedOn()) {
            jumpedOn();
        }
    }
}
