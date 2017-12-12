package nyc.mok.game.units;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.BattleUnitTypeComponent;
import nyc.mok.game.components.MoveTargetsComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.SpawnLifecycleComponent;

/**
 * Created by taco on 12/11/17.
 */

public class Common {

	public static Entity create(World ecs, float x, float y) {
		final Entity e = ecs.createEntity();

		ecs.getMapper(PositionComponent.class).create(e).position.set(x, y);
		final PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).create(e);
		physicsBody.initialX = x;
		physicsBody.initialY = y;

		ecs.getMapper(SpawnLifecycleComponent.class).create(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW;

		final BattleUnitTypeComponent battleUnitTypeComponent = ecs.getMapper(BattleUnitTypeComponent.class).create(e);
		battleUnitTypeComponent.battleUnitType = BattleUnitTypeComponent.BattleUnitType.MARINE;

		final BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).create(e);
		final BattleAttackableComponent battleAttackableComponent = ecs.getMapper(BattleAttackableComponent.class).create(e);
		final MoveTargetsComponent moveTargetsComponent = ecs.getMapper(MoveTargetsComponent.class).create(e);

		return e;
	}


	/**
	 * Create box2d bodies for all the components needed
	 */
	public static void createPhysics(World ecs, Entity e, com.badlogic.gdx.physics.box2d.World box2dWorld, float radius, float x, float y) {

		PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).get(e);
		BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);

		// TODO: Check if body already exists
		// TODO: Recycle bodyDef and circles if possible

		physicsBody.bodyDef = new BodyDef();
		physicsBody.bodyDef.type = BodyDef.BodyType.DynamicBody;
		physicsBody.bodyDef.position.set(physicsBody.initialX, physicsBody.initialY);

		physicsBody.body = box2dWorld.createBody(physicsBody.bodyDef);

		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		physicsBody.body.createFixture(circle, 1f);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();

		physicsBody.body.setUserData(e);
	}

}
