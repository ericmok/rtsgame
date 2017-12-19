package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nyc.mok.game.components.DeathAnimation;
import nyc.mok.game.components.EntityType;

/**
 * Created by taco on 12/18/17.
 */

public class DeathAnimationSystem extends EntityProcessingSystem {

	public static final String ATLAS_FILE_NAME = "death_flag.atlas";
	public static final float RADIUS = 1f;

	public static final float FRAME_DURATION_BASIC_DEATH_ANIMATION_FRAME_TIME = 0.1f;

	private ComponentMapper<EntityType> entityTypeMapper;
	private ComponentMapper<DeathAnimation> deathAnimationMapper;

	private SpriteBatch spriteBatch;
	private TextureAtlas dyingAtlas;
	private Animation<TextureRegion> dyingAnimation;
	private float totalAnimationTime = 1f;

	public DeathAnimationSystem(SpriteBatch spriteBatch) {
		super(Aspect.all(EntityType.class, DeathAnimation.class));
		this.spriteBatch = spriteBatch;
	}

	@Override
	protected void initialize() {
		super.initialize();
		dyingAtlas = new TextureAtlas(Gdx.files.internal(ATLAS_FILE_NAME));
		dyingAnimation = new Animation<TextureRegion>(FRAME_DURATION_BASIC_DEATH_ANIMATION_FRAME_TIME, dyingAtlas.getRegions(), Animation.PlayMode.NORMAL);
		totalAnimationTime = dyingAtlas.getRegions().size;
	}

	public void drawDeathAnimation(Entity e, DeathAnimation deathAnimation) {
		spriteBatch.begin();

		TextureRegion currentFrame = dyingAnimation.getKeyFrame(deathAnimation.progress);

		spriteBatch.draw(currentFrame.getTexture(),
				deathAnimation.position.x - RADIUS,
				deathAnimation.position.y - RADIUS,
				RADIUS, RADIUS,
				2 * RADIUS, 2 * RADIUS,
				1, 1,
				0,
				currentFrame.getRegionX(), currentFrame.getRegionY(),
				currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),
				false, false);

		spriteBatch.end();
	}

	@Override
	protected void process(Entity e) {
		EntityType entityType = entityTypeMapper.get(e);
		DeathAnimation deathAnimation = deathAnimationMapper.get(e);

		deathAnimation.progress += getWorld().getDelta();

		if (deathAnimation.progress >= (FRAME_DURATION_BASIC_DEATH_ANIMATION_FRAME_TIME * totalAnimationTime)) {
			deathAnimation.progress = 0;
			getWorld().deleteEntity(e);
		} else {
			// Can add entity type specific death animations here
			drawDeathAnimation(e, deathAnimation);
		}
	}
}
