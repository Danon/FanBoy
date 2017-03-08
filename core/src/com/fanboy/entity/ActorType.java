package com.fanboy.entity;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.entity.blob.ClientBlob;
import com.fanboy.entity.bomb.ClientBomb;
import com.fanboy.entity.bullet.ClientBullet;
import com.fanboy.entity.fly.ClientFly;
import com.fanboy.entity.frog.ClientFrog;
import com.fanboy.entity.player.ClientPlayer;
import com.fanboy.renderer.world.WorldRenderer;

public enum ActorType {
    PLAYER(1, ClientPlayer::new),
    BLOB(2, ClientBlob::new),
    BULLET(4, ClientBullet::new),
    FLY(5, ClientFly::new),
    FROG(6, ClientFrog::new),
    BOMB(7, (id, x, y, renderer) -> new ClientBomb(id, new Vector2(x, y), renderer));

    private final byte value;
    private final ClientEntityFactory entityFactory;

    ActorType(int value, ClientEntityFactory entityFactory) {
        this.value = (byte) value;
        this.entityFactory = entityFactory;
    }

    public byte toByte() {
        return value;
    }

    public ClientEntity createEntity(short id, float x, float y, WorldRenderer renderer) {
        return entityFactory.create(id, x, y, renderer);
    }

    public static ActorType fromByte(byte b) {
        for (ActorType type : ActorType.values()) {
            if (type.toByte() == b) {
                return type;
            }
        }
        throw new RuntimeException("Invalid byte");
    }
}
