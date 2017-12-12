package nyc.mok.game;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import nyc.mok.game.systems.BattleUnitSystem;
import nyc.mok.game.systems.MovementSystem;
import nyc.mok.game.systems.PositionFromPhysicsSystem;
import nyc.mok.game.systems.RenderBattleUnitSystem;
import nyc.mok.game.systems.SpawningBattleUnitSystem;
import nyc.mok.game.units.Marine;


public class MyGame implements Screen, InputProcessor {
    public World ecs;
    com.badlogic.gdx.physics.box2d.World box2dWorld;

    private float accumulator = 0;
    SpriteBatch batch;
    Texture img;

    OrthographicCamera orthographicCamera;

    float elapsedTime = 0;

    Box2DDebugRenderer debugRenderer;

    Vector3 touchPos = new Vector3(0, 0, 0);

    Game game;

    private Stage stage;
    private Skin skin;
    private Table table;
    private TextButton spawnMarineButton;
    private TextButton spawnTriangleButton;
    private TextButton spawnSquareButton;

    private enum UnitMode {
        MARINE,
        TRIANGLE,
        SQUARE
    }
    private UnitMode unitMode = UnitMode.MARINE;

    MyGame(Game game) {
        this.game = game;
    }

    public void create() {
        // This camera will get resized more appropriately later
        orthographicCamera = new OrthographicCamera(800, 600);

        batch = new SpriteBatch();
        img = new Texture(Gdx.files.internal("marine.png"));

        SpriteBatch ecsBatch = new SpriteBatch();
        ecsBatch.setProjectionMatrix(orthographicCamera.combined);

        box2dWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0f), true);

        WorldConfiguration config = new WorldConfigurationBuilder()
                .dependsOn(EntityLinkManager.class)
                .with(new SpawningBattleUnitSystem(box2dWorld))
                .with(new PositionFromPhysicsSystem())
                .with(new BattleUnitSystem(box2dWorld))
                .with(new MovementSystem())
                .with(new RenderBattleUnitSystem(ecsBatch, orthographicCamera))
                .build();

        ecs = new World(config);

        debugRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);

        //Gdx.input.setInputProcessor(this);
        Gdx.input.setInputProcessor(new InputMultiplexer(setupStage(), this));
    }

    public Stage setupStage() {

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());

        skin.getFont("default-font").getData().setScale(4);

        table = new Table();
        table.setWidth(stage.getWidth());
        table.setFillParent(true);

        table.align(Align.left | Align.top);
        table.setPosition(0, Gdx.graphics.getHeight());
        table.setPosition(0, 0);

        table.row();

        spawnMarineButton = new TextButton("MARINE", skin);
        spawnMarineButton.setSize(400, 300);
        spawnMarineButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                unitMode = UnitMode.MARINE;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        table.add(spawnMarineButton).width(400).height(200).padBottom(50);
        table.row();

        spawnTriangleButton = new TextButton("TRIANGLE", skin);
        spawnTriangleButton.setSize(400, 300);
        spawnTriangleButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                unitMode = UnitMode.TRIANGLE;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        table.add(spawnTriangleButton).width(400).height(200).padBottom(50);
        table.row();

        spawnSquareButton = new TextButton("SQUARE", skin);
        spawnSquareButton.setSize(400, 300);
        spawnSquareButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                unitMode = UnitMode.SQUARE;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        table.add(spawnSquareButton).width(400).height(200);
        table.row();

        stage.addActor(table);
        return stage;
    }

    public void step(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthographicCamera.update();
        orthographicCamera.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        ecs.setDelta(delta);
        ecs.process();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        doPhysicsStep(delta);

        batch.setProjectionMatrix(orthographicCamera.combined);

//        batch.begin();
//
//        batch.draw(img, touchPos.x - img.getWidth() / 2, touchPos.y - img.getHeight() / 2);
//        batch.end();

        debugRenderer.render(box2dWorld, orthographicCamera.combined);
    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1f/60) {
            accumulator -= 1f/60;

            box2dWorld.step(1f/60, 6, 2);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        float dt = Gdx.graphics.getDeltaTime();
        elapsedTime += dt;

        this.step(dt);
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width / height;
        float scaledHeightInMeters = Constants.VIEWPORT_MIN_METERS;

        orthographicCamera.setToOrtho(false, scaledHeightInMeters * aspectRatio, scaledHeightInMeters);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        orthographicCamera.unproject(touchPos.set(screenX, screenY, 0));

        if (unitMode == UnitMode.MARINE) {
            // TODO: Test concurrency with ecs in game loop
            Entity e = Marine.create(ecs, touchPos.x, touchPos.y);
        } else if (unitMode == UnitMode.TRIANGLE) {
            Entity e = Marine.createTriangle(ecs, touchPos.x, touchPos.y);
        } else if (unitMode == UnitMode.SQUARE) {
            Entity e = Marine.createSquare(ecs, touchPos.x, touchPos.y);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
