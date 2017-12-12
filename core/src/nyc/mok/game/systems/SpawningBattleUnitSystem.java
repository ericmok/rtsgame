package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.components.BattleUnitTypeComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.SpawnLifecycleComponent;
import nyc.mok.game.units.Marine;

/**
 * Created by taco on 12/10/17.
 */
public class SpawningBattleUnitSystem extends EntityProcessingSystem {
    private World box2dWorld;

	public SpawningBattleUnitSystem(World box2dWorld) {
		super(Aspect.all(SpawnLifecycleComponent.class, PhysicsBody.class, BattleUnitTypeComponent.class));
		this.box2dWorld = box2dWorld;
	}

	@Override
	public void inserted(Entity e) {
		world.getMapper(SpawnLifecycleComponent.class).get(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW;
	}

	@Override
	public void removed(Entity e) {
		world.getMapper(SpawnLifecycleComponent.class).get(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.INACTIVE;
	}

	@Override
	protected void process(Entity e) {
        ComponentMapper<SpawnLifecycleComponent> spawnLifecycleComponentComponentMapper = world.getMapper(SpawnLifecycleComponent.class);
        SpawnLifecycleComponent spawnLifecycleComponent = spawnLifecycleComponentComponentMapper.get(e);

        BattleUnitTypeComponent battleUnitTypeComponent = world.getMapper(BattleUnitTypeComponent.class).get(e);

        if (spawnLifecycleComponent.lifeCycle == SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW) {

            switch (battleUnitTypeComponent.battleUnitType) {
                default:
                    createPhysicsBodies(e);
                    break;
            }

            spawnLifecycleComponent.lifeCycle = SpawnLifecycleComponent.LifeCycle.ALIVE;
        }
    }

    /**
     * Allocate box2d resources for the entity containing physics and battle bodies.
     */
    private void createPhysicsBodies(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        Marine.createPhysics(getWorld(), e, box2dWorld, physicsBody.initialX, physicsBody. initialY);
    }

}
