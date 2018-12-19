package com.fanboy.game.manager;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fanboy.category.LivingCategory;
import com.fanboy.entity.ServerEntity;
import com.fanboy.entity.bomb.ServerBomb;
import com.fanboy.entity.bullet.ServerBullet;
import com.fanboy.entity.player.ServerPlayer;
import com.fanboy.game.manager.physics.Body;
import com.fanboy.game.manager.physics.BodyType;
import com.fanboy.game.manager.physics.Ray;
import com.fanboy.game.manager.physics.World;
import com.fanboy.network.message.AudioMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_HEIGHT;
import static com.fanboy.renderer.world.WorldRenderer.VIEWPORT_WIDTH;

public class WorldBodyUtils {
    public final WorldManager worldManager;
    public final AudioMessage audio;
    private final World world;
    final List<ServerEntity> entities = new ArrayList<>();
    private Circle circle = new Circle();

    public WorldBodyUtils(WorldManager worldManager) {
        this.worldManager = worldManager;
        this.audio = worldManager.audio;
        this.world = worldManager.getWorld();
    }

    public Body createBody(ServerEntity entity, float width, float height, Vector2 position, BodyType type) {
        float x = position.x - width / 2;
        float y = position.y - height / 2;

        Body body = new Body(entity, world, x, y, width, height, type);
        world.addBody(body);
        return body;
    }

    void createWorldObject(MapObject object) {
        if (object instanceof RectangleMapObject) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            Body body = new Body(null, rectangle);
            world.addBody(body);

            if (rectangle.x < 20) {
                rectangle = new Rectangle(rectangle);
                rectangle.x += VIEWPORT_WIDTH;
                body = new Body(null, rectangle);
                world.addBody(body);
            }

            if (rectangle.x + rectangle.width > VIEWPORT_WIDTH - 20) {
                rectangle = new Rectangle(rectangle);
                rectangle.x -= VIEWPORT_WIDTH;
                body = new Body(null, rectangle);
                world.addBody(body);
            }

            if (rectangle.y < 20) {
                rectangle = new Rectangle(rectangle);
                rectangle.y += VIEWPORT_HEIGHT;
                body = new Body(null, rectangle);
                world.addBody(body);
            }
            if (rectangle.y > VIEWPORT_HEIGHT - 20) {
                rectangle = new Rectangle(rectangle);
                rectangle.y -= VIEWPORT_HEIGHT;
                body = new Body(null, rectangle);
                world.addBody(body);
            }
        }
    }

    public ServerBullet createBullet(float x, float y, ServerPlayer shooter) {
        ServerBullet bullet = new ServerBullet(worldManager.id++, new Vector2(x, y), this);
        bullet.shooter = shooter;
        entities.add(bullet);
        return bullet;
    }

    public ServerBomb createBomb(float x, float y, ServerPlayer bomber) {
        ServerBomb bomb = new ServerBomb(worldManager.id++, new Vector2(x, y), this);
        bomb.attacker = bomber;
        entities.add(bomb);
        return bomb;
    }

    public void destroyBody(Body body) {
        body.toDestroy = true;
    }

    public List<Vector2> getPlayers(Vector2 point, float distance) {
        List<Vector2> players = new LinkedList<>();
        float distancePow = distance * distance;

        for (ServerPlayer player : worldManager.playerList.values()) {
            Vector2 position = player.body.getPosition();
            if (point.dst2(position.x, position.y) < distancePow) {
                players.add(new Vector2(position));
            } else if (point.dst2(position.x + VIEWPORT_WIDTH, position.y) < distancePow) {
                players.add(new Vector2(position.x + VIEWPORT_WIDTH, position.y));
            } else if (point.dst2(position.x - VIEWPORT_WIDTH, position.y) < distancePow) {
                players.add(new Vector2(position.x - VIEWPORT_WIDTH, position.y));
            } else if (point.dst2(position.x, position.y + VIEWPORT_HEIGHT) < distancePow) {
                players.add(new Vector2(position.x, position.y + VIEWPORT_HEIGHT));
            } else if (point.dst2(position.x, position.y - VIEWPORT_HEIGHT) < distancePow) {
                players.add(new Vector2(position.x, position.y - VIEWPORT_HEIGHT));
            }
        }
        return players;
    }

    public void destroyEntities(ServerBomb bomb, float radius, Vector2 position) {
        Body body = bomb.body;
        circle.set(position, radius);
        for (ServerEntity entity : worldManager.entities) {
            if (entity.body == body || entity.body.toDestroy) {
                continue;
            }
            if (Intersector.overlaps(circle, entity.body.rectangle)) {
                Vector2 step = entity.body.getPosition();
                float length = position.dst(step);
                step.sub(position);
                float max = Math.max(step.x, step.y);
                step.scl(4 / max);
                Body otherBody = Ray.findBody(world, body, step, length, true);

                if (otherBody == null) {
                    if (entity instanceof LivingCategory) {
                        if (((LivingCategory) entity.body.getUserData()).kill()) {
                            if (bomb.attacker != entity.body.getUserData())
                                bomb.attacker.addKill();
                            else {
                                bomb.attacker.reduceKill();
                            }
                        }
                    }
                }
            }
        }
    }
}
