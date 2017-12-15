package nyc.mok.game.units;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import nyc.mok.game.Constants;
import nyc.mok.game.components.EntityType;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.WallComponent;


public class Wall {

	private static final BodyDef BODY_DEF = new BodyDef() {{
		this.type = BodyType.StaticBody;
		this.linearVelocity.set(0, 0);
	}};

	private static final PolygonShape POLYGON_SHAPE_TO_REUSE = new PolygonShape();
	private static Vector2 vectorToReuse = new Vector2();

	public static Entity create(World ecs, float centerX, float centerY, float width, float height) {
		final Entity e = ecs.createEntity();

		ecs.getMapper(PositionComponent.class).create(e).position.set(centerX, centerY);
		final PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).create(e);


		final EntityType entityType = ecs.getMapper(EntityType.class).create(e);
		entityType.type = EntityType.Type.WALL;

		final WallComponent wallComponent = ecs.getMapper(WallComponent.class).create(e);
		wallComponent.centerX = centerX;
		wallComponent.centerY = centerY;
		wallComponent.width = width;
		wallComponent.height = height;

		return e;
	}

	public static Body createBody(com.badlogic.gdx.physics.box2d.World box2dWorld, float centerX, float centerY, float width, float height) {
		Body body = box2dWorld.createBody(BODY_DEF);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.filter.categoryBits = Constants.BOX2D_CATEGORY_ENV;
		POLYGON_SHAPE_TO_REUSE.setAsBox(width/2, height/2, vectorToReuse.set(centerX, centerY), 0);
		fixtureDef.shape = POLYGON_SHAPE_TO_REUSE;
		body.createFixture(fixtureDef);

		return body;
	}
}
