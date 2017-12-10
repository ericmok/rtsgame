package nyc.mok.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class RTSGame extends com.badlogic.gdx.Game implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	Sprite sprite;
	Animation<TextureRegion> runningAnimation;
	TextureAtlas textureAtlas;
	float elapsedTime = 0;

	float PIXEL_TO_METERS = 256f;

	AssetManager manager = new AssetManager();

	OrthographicCamera orthographicCamera;

	Vector3 touchPos3 = new Vector3(0, 0, 0);

	MyGame myGame = new MyGame(this);


//
//	public void createBounds() {
//		// Create our body definition
//		BodyDef groundBodyDef = new BodyDef();
//// Set its ecs position
//		groundBodyDef.position.set(new Vector2(0, 0));
//		//groundBodyDef.type = BodyDef.BodyType.StaticBody;
//
//// Create a body from the defintion and add it to the ecs
//		Body groundBody = box2dWorld.createBody(groundBodyDef);
//
//// Create a polygon shape
//		PolygonShape groundBox = new PolygonShape();
//// Set the polygon shape as a box which is twice the size of our view port and 20 high
//// (setAsBox takes half-width and half-height as arguments)
//		//groundBox.setAsBox(orthographicCamera.viewportWidth - 10f, orthographicCamera.viewportHeight - 10f);
//		groundBox.setAsBox(Gdx.graphics.getWidth(), 10f);
//// Create a fixture from our polygon shape and add it to our ground body
//		groundBody.createFixture(groundBox, 0.0f);
//// Clean up after ourselves
//		groundBox.dispose();
//	}
//


	@Override
	public void create() {
		//super.create();
		orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		font = new BitmapFont();
		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("marine.png"));
		myGame.create();

		this.setScreen(myGame);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		orthographicCamera.setToOrtho(false, width * 4, height * 4 );
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
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

	// On touch we apply force from the direction of the users touch.
	// This could result in the object "spinning"
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		return true;
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
