package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.SpawnLifecycleComponent;

/**
 * Created by taco on 12/10/17.
 */
public class SpawningBattleUnitSystem extends EntityProcessingSystem {
	private World box2dWorld;

	ComponentMapper<SpawnLifecycleComponent> spawnLifecycleMapper;
	ComponentMapper<PhysicsBody> physicsBodyMapper;

	public SpawningBattleUnitSystem(World box2dWorld) {
		super(Aspect.all(SpawnLifecycleComponent.class, PhysicsBody.class));
		this.box2dWorld = box2dWorld;
	}

	@Override
	public void inserted(Entity e) {
		spawnLifecycleMapper.get(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW;

	}

	@Override
	public void removed(Entity e) {
		world.getMapper(SpawnLifecycleComponent.class).get(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.INACTIVE;

		// TODO: Recycle box2d resources?
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

		if (physicsBody.body == null) {
			// TODO: Can't decide where to create bodyDefs and fixtureDefs

			physicsBody.body = box2dWorld.createBody(physicsBody.bodyDef);
			physicsBody.body.createFixture(physicsBody.fixtureDef);
			physicsBody.body.setUserData(e);
		}
	}

}
