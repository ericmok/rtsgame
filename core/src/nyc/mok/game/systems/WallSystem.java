package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.components.EntityType;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.WallComponent;
import nyc.mok.game.units.Wall;
import nyc.mok.game.utils.ScaledSprite;

/**
 * Created by taco on 12/14/17.
 */

public class WallSystem extends EntityProcessingSystem {
	private World box2dWorld;
	private SpriteBatch spriteBatch;
	private ScaledSprite sprite = new ScaledSprite();
	private OrthographicCamera orthographicCamera;

	ComponentMapper<WallComponent> wallComponentComponentMapper;
	ComponentMapper<PhysicsBody> physicsBodyComponentMapper;

	public WallSystem(World box2dWorld, SpriteBatch spriteBatch, OrthographicCamera orthographicCamera) {
		super(Aspect.all(EntityType.class, PhysicsBody.class, WallComponent.class));
		this.box2dWorld = box2dWorld;
		this.spriteBatch = spriteBatch;
		this.orthographicCamera = orthographicCamera;
	}

	@Override
	protected void begin() {
		Texture wallTexture = new Texture(Gdx.files.internal("square.png"));
		sprite.setTexture(wallTexture);
	}

	@Override
	public void inserted(Entity e) {
		WallComponent wallComponent = wallComponentComponentMapper.get(e);
		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);

		physicsBody.body = Wall.createBody(box2dWorld, wallComponent.centerX, wallComponent.centerY, wallComponent.width, wallComponent.height);
	}

	@Override
	protected void process(Entity e) {
		WallComponent wallComponent = wallComponentComponentMapper.get(e);

		sprite.scaledDraw(spriteBatch, wallComponent.centerX, wallComponent.centerY, wallComponent.width, wallComponent.height, 0);
	}
}
