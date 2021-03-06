package nyc.mok.game.units;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.PlayerManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import nyc.mok.game.Constants;
import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.ControlNode;
import nyc.mok.game.components.EntityType;
import nyc.mok.game.components.MoveTargetsComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.SpawnLifecycleComponent;
import nyc.mok.game.components.Targets;

/**
 * Created by taco on 12/11/17.
 */

public class Common {

	public static final float COMMON_UNIT_RADIUS = 1;
	public static final float COMMON_UNIT_TARGET_ACQUISITION_RANGE = 16;
	public static final float COMMON_UNIT_MAX_ATTACK_RANGE = 10;

	public static final short FILTER_CATEGORIES = Constants.BOX2D_CATEGORY_UNITS;

	private static final BodyDef TEMPLATE_BODY_DEF = createDynamicBodyDef(0, 0);
	private static final FixtureDef TEMPLATE_FIXTURE_DEF = createCircleFixtureDef(COMMON_UNIT_RADIUS);

	private static BodyDef tempBodyDef = new BodyDef();
	private static FixtureDef tempFixtureDef = new FixtureDef();
	private static CircleShape tempCircleShape = new CircleShape();
	private static PolygonShape polygonShape = new PolygonShape();

	private static ArchetypeBuilder archetypeBuilder = new ArchetypeBuilder()
			.add(PositionComponent.class)
			.add(PhysicsBody.class)
			.add(SpawnLifecycleComponent.class)
			.add(EntityType.class)
			.add(Targets.class)
			.add(BattleBehaviorComponent.class)
			.add(BattleAttackableComponent.class)
			.add(MoveTargetsComponent.class)
			.add(ControlNode.class);

	private static Archetype archetype = null;
	private static World prevWorld = null;

	/**
	 * Creates an entity with components to make this a battle unit.
	 *
	 * @param ecs
	 * @param x
	 * @param y
	 * @return
	 */
	public static Entity create(World ecs, PlayerManager playerManager, String player, float x, float y) {
		if (archetype == null || prevWorld != ecs) {
			archetype = archetypeBuilder.build(ecs);
			prevWorld = ecs;
		}

		int e = ecs.create(archetype);
		Entity entity = ecs.getEntity(e);

		SpawnLifecycleComponent spawnLifecycleComponent = ecs.getMapper(SpawnLifecycleComponent.class).get(entity);
		spawnLifecycleComponent.initX = x;
		spawnLifecycleComponent.initY = y;

		BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(entity);
		battleBehaviorComponent.targetAcquisitionRange = COMMON_UNIT_TARGET_ACQUISITION_RANGE;
		battleBehaviorComponent.maxAttackRange = COMMON_UNIT_MAX_ATTACK_RANGE;
		battleBehaviorComponent.rangeToBeginAttacking = COMMON_UNIT_MAX_ATTACK_RANGE - 2;

		playerManager.setPlayer(entity, player);

		return entity;
	}

	public static BodyDef createDynamicBodyDef(float x, float y) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x, y);

		return bodyDef;
	}

	public static FixtureDef createCircleFixtureDef(float radius) {
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(radius);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 1;
		fixtureDef.filter.categoryBits = FILTER_CATEGORIES;

		return fixtureDef;
	}

	/**
	 * Create box2d bodies for all the components needed
	 */
	public static Body createBody(com.badlogic.gdx.physics.box2d.World box2dWorld, float x, float y) {
		BodyDef bodyDef = useTempBodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x, y);

		Body body = box2dWorld.createBody(bodyDef);

		CircleShape circle = tempCircleShape;
		circle.setPosition(Vector2.Zero);
		circle.setRadius(COMMON_UNIT_RADIUS);
		FixtureDef fixtureDef = useTempFixtureDef();
		fixtureDef.filter.categoryBits = Constants.BOX2D_CATEGORY_UNITS;
		fixtureDef.filter.maskBits = (short)(Constants.BOX2D_CATEGORY_UNITS | Constants.BOX2D_CATEGORY_SENSORS | Constants.BOX2D_CATEGORY_ENV | Constants.BOX2D_CATEGORY_FIELDS);
		fixtureDef.filter.groupIndex = 0;
		fixtureDef.shape = circle;
		fixtureDef.density = 1;
		body.createFixture(fixtureDef);

		FixtureDef sensorDef = useTempFixtureDef();
		circle.setPosition(Vector2.Zero);
		circle.setRadius(COMMON_UNIT_TARGET_ACQUISITION_RANGE);
		sensorDef.shape = circle;
		sensorDef.density = 0;
		sensorDef.isSensor = true;
		sensorDef.filter.categoryBits = Constants.BOX2D_CATEGORY_SENSORS;
		sensorDef.filter.maskBits = Constants.BOX2D_CATEGORY_UNITS;
		sensorDef.filter.groupIndex = 0; // Could set to -1 to make it not collide. Cool.
		body.createFixture(sensorDef);

		return body;
	}

	public static void copyBodyDef(BodyDef src, BodyDef dst) {
		dst.position.set(src.position);
		dst.type = src.type;
		dst.active = src.active;
		dst.allowSleep = src.allowSleep;
		dst.angle = src.angle;
		dst.angularDamping = src.angularDamping;
		dst.angularVelocity = src.angularVelocity;
		dst.awake = src.awake;
		dst.bullet = src.bullet;
		dst.fixedRotation = src.fixedRotation;
		dst.gravityScale = src.gravityScale;
		dst.linearDamping = src.linearDamping;
		dst.linearVelocity.set(src.linearVelocity);
	}

	public static BodyDef useTempBodyDef() {
		copyBodyDef(TEMPLATE_BODY_DEF, tempBodyDef);
		return tempBodyDef;
	}

	public static FixtureDef useTempFixtureDef() {
		FixtureDef fixtureDef = tempFixtureDef;
		fixtureDef.isSensor = false;
		fixtureDef.shape = null;
		fixtureDef.filter.categoryBits = 0x0001;
		fixtureDef.filter.maskBits = 1;
		fixtureDef.filter.groupIndex = 0;
		return fixtureDef;
	}
}
