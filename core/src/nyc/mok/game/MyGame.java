package nyc.mok.game;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import nyc.mok.game.systems.BattleUnitSystem;
import nyc.mok.game.systems.PositionFromPhysicsSystem;
import nyc.mok.game.systems.RenderBattleUnitSystem;
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
//                .dependsOn()
                .with(new PositionFromPhysicsSystem())
                .with(new BattleUnitSystem(box2dWorld))
                .with(new RenderBattleUnitSystem(ecsBatch, orthographicCamera))
                .build();

        ecs = new World(config);

        debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);

        Gdx.input.setInputProcessor(this);
    }

    public void step(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthographicCamera.update();
        orthographicCamera.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        ecs.setDelta(delta);
        ecs.process();
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

        // TODO: Test concurrency with ecs in game loop
        Marine.create(ecs, touchPos.x, touchPos.y);

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
