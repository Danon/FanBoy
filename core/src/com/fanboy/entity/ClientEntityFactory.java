package com.fanboy.entity;

import com.fanboy.renderer.world.WorldRenderer;

@FunctionalInterface
public interface ClientEntityFactory {
    ClientEntity create(short id, float x, float y, WorldRenderer renderer);
}
