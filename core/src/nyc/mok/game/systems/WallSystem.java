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

/**
 * Created by taco on 12/14/17.
 */

public class WallSystem extends EntityProcessingSystem {
	private World box2dWorld;
	private SpriteBatch spriteBatch;
	private OrthographicCamera orthographicCamera;

	private Texture wallTexture;

	ComponentMapper<WallComponent> wallComponentComponentMapper;
	ComponentMapper<PhysicsBody> physicsBodyComponentMapper;

	public WallSystem(World box2dWorld, SpriteBatch spriteBatch, OrthographicCamera orthographicCamera) {
		super(Aspect.all(EntityType.class, PhysicsBody.class, WallComponent.class));
		this.box2dWorld = box2dWorld;
		this.spriteBatch = spriteBatch;
		this.orthographicCamera = orthographicCamera;
	}

	@Override
	protected void initialize() {
		wallTexture = new Texture(Gdx.files.internal("square.png"));
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

		//sprite.scaledDraw(spriteBatch, wallComponent.centerX, wallComponent.centerY, wallComponent.width / 2, wallComponent.height / 2, 0);
		spriteBatch.draw(wallTexture,
				wallComponent.centerX - wallComponent.width/2,
				wallComponent.centerY - wallComponent.height/2,
				wallComponent.width,
				wallComponent.height,
				0, 0,
				wallTexture.getWidth(), wallTexture.getHeight(),
				false, false);
	}
}
