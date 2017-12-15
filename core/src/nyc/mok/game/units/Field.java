package nyc.mok.game.units;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.PlayerManager;

import nyc.mok.game.components.ControlField;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.Targets;

/**
 * Created by taco on 12/15/17.
 */

public class Field {
	public static Entity create(World ecs, PlayerManager playerManager, String player, float x, float y, float angle) {
		final Entity e = ecs.createEntity();

		ecs.getMapper(PositionComponent.class).create(e).position.set(x, y);
		final PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).create(e);

		// These would be replaced by spawning system
		//	physicsBody.bodyDef = bodyDef;
		//	physicsBody.fixtureDef = fixtureDef;
//
//		final SpawnLifecycleComponent spawnLifecycleComponent = ecs.getMapper(SpawnLifecycleComponent.class).create(e);
//		spawnLifecycleComponent.lifeCycle = SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW;
//		spawnLifecycleComponent.initX = x;
//		spawnLifecycleComponent.initY = y;

		final Targets targets = ecs.getMapper(Targets.class).create(e);

		ControlField controlField = ecs.getMapper(ControlField.class).create(e);
		controlField.initPos.set(x, y);
		controlField.initAngle = angle;
//		final EntityType entityType = ecs.getMapper(EntityType.class).create(e);
//		final BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).create(e);
//		battleBehaviorComponent.targetAcquisitionRange = COMMON_UNIT_TARGET_ACQUISITION_RANGE;
//		battleBehaviorComponent.maxAttackRange = COMMON_UNIT_MAX_ATTACK_RANGE;

//		final BattleAttackableComponent battleAttackableComponent = ecs.getMapper(BattleAttackableComponent.class).create(e);
//		final MoveTargetsComponent moveTargetsComponent = ecs.getMapper(MoveTargetsComponent.class).create(e);

		playerManager.setPlayer(e, player);

		return e;
	}
}
