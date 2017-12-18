package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

import nyc.mok.game.components.EntityType;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.SpawnLifecycleComponent;
import nyc.mok.game.units.Common;

/**
 * Created by taco on 12/10/17.
 */
public class SpawningBattleUnitSystem extends EntityProcessingSystem {

	private Box2dSystem box2dSystem;

	ComponentMapper<SpawnLifecycleComponent> spawnLifecycleMapper;
	ComponentMapper<EntityType> entityTypeMapper;
	ComponentMapper<PhysicsBody> physicsBodyMapper;

	public SpawningBattleUnitSystem() {
		super(Aspect.all(SpawnLifecycleComponent.class, EntityType.class, PhysicsBody.class));
	}

	@Override
	public void inserted(Entity e) {
		spawnLifecycleMapper.get(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW;

	}

	@Override
	public void removed(Entity e) {
		world.getMapper(SpawnLifecycleComponent.class).get(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.INACTIVE;

		// TODO: Recycle box2d resources?
		PhysicsBody physicsBody = physicsBodyMapper.get(e);
		box2dSystem.getBox2dWorld().destroyBody(physicsBody.body);
	}

	@Override
	protected void process(Entity e) {
		SpawnLifecycleComponent spawnLifecycleComponent = spawnLifecycleMapper.get(e);
		if (spawnLifecycleComponent.lifeCycle == SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW) {
			createPhysicsBodies(e);
			spawnLifecycleComponent.lifeCycle = SpawnLifecycleComponent.LifeCycle.ALIVE;
		}
	}

	/**
	 * Allocate box2d resources for the entity containing physics and battle bodies.
	 */
	private void createPhysicsBodies(Entity e) {
		PhysicsBody physicsBody = physicsBodyMapper.get(e);
		SpawnLifecycleComponent spawnLifecycle = spawnLifecycleMapper.get(e);

		if (physicsBody.body == null) {
			// TODO: Can't decide where to create bodyDefs and fixtureDefs

			switch (entityTypeMapper.get(e).type) {
				case WALL:
					// TODO: Place wall creation code here
					break;
				default:
					physicsBody.body = Common.createBody(box2dSystem.getBox2dWorld(), spawnLifecycle.initX, spawnLifecycle.initY);
					physicsBody.body.setUserData(e);
				break;
			}
		}
	}

}
