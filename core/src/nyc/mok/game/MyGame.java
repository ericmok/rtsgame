package nyc.mok.game;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.PlayerManager;
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
import nyc.mok.game.systems.Box2dSystem;
import nyc.mok.game.systems.ControlFieldSystem;
import nyc.mok.game.systems.DeathAnimationSystem;
import nyc.mok.game.systems.MovementSystem;
import nyc.mok.game.systems.PositionFromPhysicsSystem;
import nyc.mok.game.systems.RenderBattleUnitSystem;
import nyc.mok.game.systems.SpawningBattleUnitSystem;
import nyc.mok.game.systems.TargetsSystem;
import nyc.mok.game.systems.WallSystem;
import nyc.mok.game.units.Field;
import nyc.mok.game.units.Marine;
import nyc.mok.game.units.Wall;


public class MyGame implements Screen, InputProcessor {
	public World ecs;
	//com.badlogic.gdx.physics.box2d.World box2dWorld;

	Vector2 cameraPositionOffset = new Vector2(0, 0);
	float prevTouchX = 0;
	float prevTouchY = 0;

	private float accumulator = 0;
	private SpriteBatch spriteBatch;
	Texture fieldControlTexture;

	OrthographicCamera orthographicCamera;

	float elapsedTime = 0;

	Box2DDebugRenderer debugRenderer;

	Vector3 touchPos = new Vector3(0, 0, 0);

	Game game;

	Vector2 lastTouchDown = new Vector2(0, 0);
	Vector2 fieldDirection = new Vector2(1, 0);

	private Stage stage;
	private Skin skin;
	private Table table;
	private TextButton spawnMarineButton;
	private TextButton spawnTriangleButton;
	private TextButton spawnSquareButton;
	private TextButton spawnZugButton;
	private TextButton fieldButton;
	private TextButton playerModeButton;

	private SpriteBatch ecsBatch;

	private enum UnitMode {
		MARINE,
		TRIANGLE,
		SQUARE,
		ZUG,
		FIELD
	}
	private UnitMode unitMode = UnitMode.MARINE;

	private boolean playerMode = true;

	public PlayerManager playerManager = new PlayerManager();

	MyGame(Game game) {
		this.game = game;
}

	public void create() {
		ecsBatch = new SpriteBatch();
		spriteBatch = new SpriteBatch();

		// This camera will get resized more appropriately later
		orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth() / Gdx.graphics.getHeight() * Constants.VIEWPORT_HEIGHT_METERS, Gdx.graphics.getHeight());

		fieldControlTexture = new Texture(Gdx.files.internal("field-control.png"));

		ecsBatch.setProjectionMatrix(orthographicCamera.combined);

		//box2dWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, 0f), true);

		WorldConfiguration config = new WorldConfigurationBuilder()
				.dependsOn(EntityLinkManager.class)
				.with(playerManager)
				.with(new Box2dSystem())
				.with(new SpawningBattleUnitSystem())
				.with(new WallSystem(ecsBatch))
				.with(new PositionFromPhysicsSystem())
				.with(new TargetsSystem())
				.with(new BattleUnitSystem())
				.with(new ControlFieldSystem())
				.with(new MovementSystem())
				.with(new RenderBattleUnitSystem(ecsBatch, orthographicCamera))
				.with(new DeathAnimationSystem(ecsBatch))
				//.with(new Box2dDebugRendererSystem(orthographicCamera))
				.build();

		ecs = new World(config);

		debugRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);

		setupStage();
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));
	}


	public Stage setupStage() {

		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage(new ScreenViewport());

		skin.getFont("default-font").getData().setScale(2);

		table = new Table();
		table.setWidth(stage.getWidth());
		table.setFillParent(true);

		table.align(Align.left | Align.top);
		table.setPosition(0, Gdx.graphics.getHeight());
		table.setPosition(0, 0);

		table.row();

		playerModeButton = new TextButton("PLAYER", skin);
		playerModeButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				playerMode = !playerMode;
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		table.add(playerModeButton).width(240).height(110).padBottom(40);
		table.row();

		spawnMarineButton = new TextButton("MARINE", skin);
		//spawnMarineButton.setSize(400, 300);
		spawnMarineButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				unitMode = UnitMode.MARINE;
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		table.add(spawnMarineButton).width(240).height(110).padBottom(30);
		table.row();

		spawnTriangleButton = new TextButton("TRIANGLE", skin);
		//spawnTriangleButton.setSize(400, 300);
		spawnTriangleButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				unitMode = UnitMode.TRIANGLE;
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		table.add(spawnTriangleButton).width(240).height(110).padBottom(30);
		table.row();

		spawnSquareButton = new TextButton("SQUARE", skin);
		//spawnSquareButton.setSize(400, 300);
		spawnSquareButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				unitMode = UnitMode.SQUARE;
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		table.add(spawnSquareButton).width(240).height(110).padBottom(30);
		table.row();

		spawnZugButton = new TextButton("ZUG", skin);
		//spawnSquareButton.setSize(400, 300);
		spawnZugButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				unitMode = UnitMode.ZUG;
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		table.add(spawnZugButton).width(240).height(110).padBottom(30);
		table.row();

		fieldButton = new TextButton("FIELD", skin);
		//fieldButton.setSize(400, 300);
		fieldButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				unitMode = UnitMode.FIELD;
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		table.add(fieldButton).width(240).height(110).padBottom(30);
		table.row();

		stage.addActor(table);

		float xOffset = 0; //Constants.MAP_WIDTH / 2;
		float yOffset = 0;
		float mapWidth = Constants.MAP_WIDTH;
		float mapHeight = Constants.MAP_HEIGHT;
		float mapStrokeWidth = 1f;

		Wall.create(ecs, 0, -mapHeight/2, mapWidth, mapStrokeWidth);
		Wall.create(ecs, 0, mapHeight/2, mapWidth, mapStrokeWidth);
		Wall.create(ecs, -(mapWidth/2), 0, mapStrokeWidth, mapHeight);
		Wall.create(ecs, (mapWidth/2), 0, mapStrokeWidth, mapHeight);

		return stage;
	}

	public void step(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		orthographicCamera.update();
		orthographicCamera.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

		ecsBatch.setProjectionMatrix(orthographicCamera.combined);
		//ecsBatch.begin();

		ecs.setDelta(delta);
		ecs.process();

		//ecsBatch.end();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
//
//		doPhysicsStep(delta);

		if (unitMode == UnitMode.FIELD && Gdx.input.isTouched()) {
			spriteBatch.setProjectionMatrix(orthographicCamera.combined);
			spriteBatch.begin();

			float fieldRadius = 10;

			spriteBatch.draw(fieldControlTexture, lastTouchDown.x - fieldRadius / 2, lastTouchDown.y - fieldRadius / 2,
					fieldRadius / 2, fieldRadius / 2, fieldRadius, fieldRadius, 1, 1,
					fieldDirection.angle() - 90,
					0, 0, fieldControlTexture.getWidth(), fieldControlTexture.getHeight(),
					false, false);
			spriteBatch.end();
		}

		// Can't call within SpriteBatch begin and ends...probably shader issue
		//debugRenderer.render(ecs.getSystem(Box2dSystem.class).getBox2dWorld(), orthographicCamera.combined);
	}

	@Override
	public void dispose() {
		ecsBatch.dispose();
		fieldControlTexture.dispose();
		stage.dispose();
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
		float scaledHeightInMeters = Constants.VIEWPORT_HEIGHT_METERS;
		float scaledWidth = aspectRatio * scaledHeightInMeters;

		orthographicCamera.setToOrtho(false, scaledWidth, scaledHeightInMeters);
		orthographicCamera.position.set(Vector3.Zero);
		stage.getViewport().update(width, height);
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

		lastTouchDown.set(touchPos.x, touchPos.y);

		if (unitMode == UnitMode.MARINE) {
			// TODO: Test concurrency with ecs in game loop
			Entity e = Marine.create(ecs, playerManager, playerMode ? Constants.PLAYER_ONE : Constants.PLAYER_TWO, touchPos.x, touchPos.y);
		} else if (unitMode == UnitMode.TRIANGLE) {
			Entity e = Marine.createTriangle(ecs, playerManager,  playerMode ? Constants.PLAYER_ONE : Constants.PLAYER_TWO, touchPos.x, touchPos.y);
		} else if (unitMode == UnitMode.SQUARE) {
			Entity e = Marine.createSquare(ecs, playerManager, playerMode ? Constants.PLAYER_ONE : Constants.PLAYER_TWO, touchPos.x, touchPos.y);
		} else if (unitMode == UnitMode.ZUG) {
			Entity e = Marine.createZug(ecs, playerManager, playerMode ? Constants.PLAYER_ONE : Constants.PLAYER_TWO, touchPos.x, touchPos.y);
		}

		prevTouchX = touchPos.x;
		prevTouchY = touchPos.y;

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		orthographicCamera.unproject(touchPos.set(screenX, screenY, 0));

		prevTouchX = touchPos.x;
		prevTouchY = touchPos.y;

		if (unitMode == UnitMode.FIELD) {
			Entity e = Field.create(ecs, playerManager, Constants.PLAYER_ONE, lastTouchDown.x, lastTouchDown.y, fieldDirection.angle());
			Field.create(ecs, playerManager, Constants.PLAYER_TWO, lastTouchDown.x, -lastTouchDown.y, -fieldDirection.angle());
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		orthographicCamera.unproject(touchPos.set(screenX, screenY, 0));

		float deltaX = touchPos.x - prevTouchX;
		float deltaY = touchPos.y - prevTouchY;

		cameraPositionOffset.set(cameraPositionOffset.x - deltaX, cameraPositionOffset.y - deltaY);
		// Uncomment this for map scroll
		//orthographicCamera.translate(-deltaX, -deltaY);

		if (unitMode == UnitMode.FIELD) {
			fieldDirection.set(deltaX, deltaY);
			fieldDirection.nor();
		}
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
