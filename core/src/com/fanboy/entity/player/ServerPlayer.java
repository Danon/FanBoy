package com.fanboy.entity.player;

import com.badlogic.gdx.math.Vector2;
import com.fanboy.category.LivingCategory;
import com.fanboy.entity.ActorType;
import com.fanboy.entity.ServerEntity;
import com.fanboy.entity.bomb.ServerBomb;
import com.fanboy.entity.bullet.ServerBullet;
import com.fanboy.game.Utils;
import com.fanboy.game.manager.WorldBodyUtils;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.network.message.ControlsMessage;
import com.fanboy.network.message.EntityState;

import static com.fanboy.game.manager.physics.BodyType.Dynamic;
import static java.lang.Math.max;

public class ServerPlayer extends ServerEntity implements LivingCategory {
    public static final float WIDTH = 12;
    public static final float HEIGHT = 20;
    public static final float Y_OFFSET = 1f;

    private ControlsMessage currentControls = new ControlsMessage();
    private float reloadTime = 0;
    private Vector2 velocity = new Vector2();
    private float direction = 1;
    private float directionX;
    private float directionY;
    private float spawnTime = 0.1f;
    private byte score = 0;
    private byte totalBombs = 3;
    private float addBombTimer = 0;
    private String name = "";

    private Vector2 respawnPosition;

    public ServerPlayer(short id, Vector2 position, WorldBodyUtils world) {
        super(id, position, ActorType.PLAYER, world);
        this.respawnPosition = new Vector2(position);
    }

    @Override
    protected Body createBody(Vector2 position, WorldBodyUtils world) {
        return world.createBody(this, WIDTH, HEIGHT - Y_OFFSET * 2, position, Dynamic);
    }

    @Override
    public void update(float delta) {
        if (totalBombs < 3) {
            addBombTimer += delta;
            if (addBombTimer >= 10) {
                totalBombs++;
                addBombTimer = 0;
            }
        } else {
            addBombTimer = 0;
        }

        if (spawnTime > 0) {
            spawnTime += delta;
            if (spawnTime > 2f) {
                body.setBodyType(Dynamic);
                spawnTime = -1f;
            }
        }

        processPlayer();
        position.set(body.getPosition());
        reloadTime += delta;
    }

    private void processPlayer() {
        processControls(currentControls);
    }

    private void processControls(ControlsMessage controls) {
        velocity.set(body.getVelocity());
        position.set(body.getPosition());

        capFallingVelocity();

        if (Utils.wrapBody(position)) {
            body.setTransform(position);
        }

        float x = controls.right ? 1 : (controls.left ? -1 : Math.signum(directionX) * 0.01f);
        float y = controls.up ? 1 : (controls.down ? -1 : 0);
        if (Math.abs(x) < 0.02f && y == 0) {
            x = direction;
        } else {
            direction = Math.signum(x);
        }
        if (Math.abs(x) == 1 && Math.abs(y) == 1) {
            x = Math.signum(x) * 0.707f;
            y = 0;
        }
        directionX = x;
        directionY = y;

        if (reloadTime > 1) {
            if (controls.shoot) {
                ServerBullet bullet = world.createBullet(position.x + x * 15, position.y + y * 15, this);
                bullet.body.setVelocity(x * 200, y * 200);
                reloadTime = 0;
            } else if (controls.throwBomb && totalBombs > 0) {
                ServerBomb bomb = world.createBomb(position.x + Math.signum(x) * ServerBomb.RADIUS, position.y + 10, this);
                bomb.body.setVelocity(Math.signum(x) * 100, 100);
                reloadTime = 0;
                totalBombs--;
            }
        }

        if (controls.right) {
            velocity.x = 100f;
        } else if (controls.left) {
            velocity.x = -100f;
        } else {
            velocity.x = 0;
        }

        body.setVelocity(velocity);

        if (controls.up && body.isOnGround()) {
            body.applyLinearImpulse(0, 290f);
            world.audio.jump();
        }
    }

    private void capFallingVelocity() {
        velocity.y = max(velocity.y, -450f);
    }

    public void setCurrentControls(ControlsMessage controls) {
        currentControls = new ControlsMessage(controls);
    }

    @Override
    public boolean kill() {
        if (spawnTime >= 0) {
            return false;
        }
        world.audio.hurt();
        respawnPlayer();
        return true;
    }

    private void respawnPlayer() {
        Vector2 position = body.getPosition();
        position.set(respawnPosition);
        body.setTransform(position);
        spawnTime = 0.1f;
        totalBombs = 3;
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
    }

    @Override
    public void updateState(EntityState state) {
        super.updateState(state);
        state.vX = body.getVelocity().x;
        state.vY = body.getVelocity().y;
        state.angle = (float) Math.atan2(directionY, directionX);
        state.extra |= (short) (spawnTime > 0.01f ? 0 : 1);
        state.extra |= (totalBombs << 1);
        state.extra |= (score << 4);
    }

    @Override
    public float getWidth() {
        return WIDTH;
    }

    @Override
    public void addKill() {
        score++;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void reduceKill() {
        score--;
    }
}
