package com.fanboy.renderer.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fanboy.Constants;
import com.fanboy.InputControl;
import com.fanboy.ProfilePreferences;
import com.fanboy.ScreenChanger;
import com.fanboy.entity.ActorType;
import com.fanboy.entity.bomb.ClientBomb;
import com.fanboy.entity.ClientEntity;
import com.fanboy.network.ControlsSender;
import com.fanboy.network.StateProcessor;
import com.fanboy.network.message.ClientDetailsMessage;
import com.fanboy.network.message.ControlsMessage;
import com.fanboy.network.message.EntityState;
import com.fanboy.network.message.GameStateMessage;
import com.fanboy.renderer.HUDRenderer;
import com.fanboy.sound.SoundPlayer;

import java.util.concurrent.ConcurrentHashMap;

public abstract class WorldRenderer {
    public static int VIEWPORT_WIDTH = 525;
    public static int VIEWPORT_HEIGHT = 375;

    private OrthographicCamera camera = new OrthographicCamera();
    protected OrthogonalTiledMapRenderer renderer;
    private FitViewport viewport;
    protected TiledMap map;
    protected SpriteBatch batch = new SpriteBatch();
    private ControlsSender controlsSender = new ControlsSender();
    public final StateProcessor stateProcessor;
    private final ConcurrentHashMap<Short, ClientEntity> worldMap = new ConcurrentHashMap<>();
    protected long previousTime;
    private int screenWidth;
    private int screenHeight;
    private short recentId = -2;
    private ScreenShaker screenShaker = new ScreenShaker(7, 10);
    public final SoundPlayer audioPlayer = new SoundPlayer();
    public HUDRenderer hudRenderer = new HUDRenderer();
    private ControlsMessage previousControlMessage;
    private InputControl controls = new InputControl();
    private final ScreenChanger screenChanger;

    public WorldRenderer(ScreenChanger changer) {
        this.screenChanger = changer;
        this.stateProcessor = new StateProcessor(worldMap, audioPlayer);
    }

    public void loadLevel(String level, ProfilePreferences preferences) {
        this.map = new TmxMapLoader().load(level);
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("terrain");
        VIEWPORT_WIDTH = (int) (layer.getTileWidth() * layer.getWidth());
        VIEWPORT_HEIGHT = (int) (layer.getTileHeight() * layer.getHeight());
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        renderer = new OrthogonalTiledMapRenderer(map);
        ClientDetailsMessage clientDetails = new ClientDetailsMessage();
        clientDetails.name = preferences.getName();
        clientDetails.protocolVersion = Constants.PROTOCOL_VERSION;
        onLoadLevel(clientDetails);
    }

    protected abstract void onLoadLevel(ClientDetailsMessage clientDetails);

    public void render(float delta) {
        this.screenShaker.update(delta);

        camera.setToOrtho(false, VIEWPORT_WIDTH + this.screenShaker.getX(), VIEWPORT_HEIGHT + this.screenShaker.getY());
        viewport.apply();
        renderer.setView(camera);
        renderer.render();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderObjects(delta);
        batch.end();
        processControls();

        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
    }

    protected abstract void renderObjects(float delta);

    protected void postRenderObjects(float delta, long currentTime, float alpha, GameStateMessage nextStateMessage) {
        for (EntityState state : nextStateMessage.states) {
            short id = recentId;
            if (!worldMap.containsKey(state.id) && state.id > recentId) {
                ActorType actorType = ActorType.fromByte(state.type);
                ClientEntity entity = actorType.createEntity(state.id, state.x, state.y, this);
                worldMap.put(state.id, entity);
                id = (short) Math.max(id, state.id);
            }
            recentId = id;
        }

        nextStateMessage.states.stream()
                .filter(state -> worldMap.get(state.id) != null)
                .forEach(state -> worldMap.get(state.id).processState(state, alpha));

        for (ClientEntity entity : worldMap.values()) {
            if (entity.destroy && entity instanceof ClientBomb) {
                this.screenShaker.shake();
            }
            if (entity.remove) {
                worldMap.remove(entity.getId());
            } else {
                entity.render(delta, batch);
            }
        }
        previousTime = currentTime;
    }

    private void processControls() {
        ControlsMessage message = controlsSender.controlMessage(controls);

        if (!message.hasEqualValues(previousControlMessage)) {
            onControlsChange(message);
            previousControlMessage = new ControlsMessage(message);
        }

        if (controls.closeButton()) {
            screenChanger.mainMenu();
        }
    }

    abstract void onControlsChange(ControlsMessage message);

    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        viewport.update(width, height);
        camera.update();
    }

    public void dispose() {
        batch.dispose();
        hudRenderer.dispose();
        audioPlayer.dispose();
        map.dispose();
        renderer.dispose();
    }
}
