package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nyc.mok.game.components.BattleUnitComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.SpawnLifecycleComponent;
import nyc.mok.game.units.Marine;

/**
 * Created by taco on 12/9/17.
 * Handles spawning new units and resource acquisition for the task
 */

public class BattleUnitSystem extends EntityProcessingSystem {
    private World box2dWorld;

    public BattleUnitSystem(World box2dWorld) {
        super(Aspect.all(PositionComponent.class, SpawnLifecycleComponent.class, BattleUnitComponent.class, PhysicsBody.class));
        this.box2dWorld = box2dWorld;
    }

    @Override
    public void removed(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        box2dWorld.destroyBody(physicsBody.body);
    }

    class TargetAcquisitionCallback implements QueryCallback {
        public Entity e;
        public PhysicsBody physicsBody;

        public ArrayList<Fixture> fixtures = new ArrayList<Fixture>(32);

        public TargetAcquisitionCallback init(Entity e) {
            this.e = e;
            this.physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
            fixtures.clear();
            return this;
        }

        @Override
        public boolean reportFixture(Fixture fixture) {
            if (physicsBody.body.getFixtureList().get(0) != fixture) {
                fixtures.add(fixture);
                Gdx.app.log("PHYS:", fixture.toString());
            }
            return true;
        }

        public ArrayList<Fixture> finishReport() {
            // PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
            final Vector2 pos = physicsBody.body.getPosition();

            Collections.sort(fixtures, new Comparator<Fixture>() {
                @Override
                public int compare(Fixture fixture, Fixture t1) {
                    float res1 = fixture.getBody().getPosition().dst(pos);
                    float res2 = t1.getBody().getPosition().dst(pos);

                    if (res1 > res2) return 1;
                    if (res1 == res2) return 0;
                    return -1;
                }
            });
            return fixtures;
        }
    }

    TargetAcquisitionCallback targetAcquisitionCallback = new TargetAcquisitionCallback();

    @Override
    protected void process(Entity e) {
        ComponentMapper<BattleUnitComponent> battleUnitComponentComponentMapper = world.getMapper(BattleUnitComponent.class);
        BattleUnitComponent battleUnitComponent = battleUnitComponentComponentMapper.get(e);

        ComponentMapper<PhysicsBody> physicsBodyComponentMapper = world.getMapper(PhysicsBody.class);
        PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);

        ComponentMapper<SpawnLifecycleComponent> spawnLifecycleComponentComponentMapper = world.getMapper(SpawnLifecycleComponent.class);
        SpawnLifecycleComponent spawnLifecycleComponent = spawnLifecycleComponentComponentMapper.get(e);

        if (spawnLifecycleComponent.lifeCycle == SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW) {
            switch (battleUnitComponent.typeToSpawn) {
                default:
                    createMarinePhysics(e);
                    break;
            }

            physicsBody.body.setLinearVelocity(10, 10);
            spawnLifecycleComponent.lifeCycle = SpawnLifecycleComponent.LifeCycle.ALIVE;
        }

        box2dWorld.QueryAABB(targetAcquisitionCallback.init(e),
                physicsBody.body.getPosition().x - 128f * 4,
                physicsBody.body.getPosition().y - 128f * 4,
                physicsBody.body.getPosition().x + 128f * 4,
                physicsBody.body.getPosition().y + 128f * 4);

        ArrayList<Fixture> fixtures = targetAcquisitionCallback.finishReport();
        if (fixtures.size() > 0) {
            for (int i = 0; i < fixtures.size(); i++) {
                if (fixtures.get(i) != physicsBody.body.getFixtureList().get(0)) {
                    Entity entity = (Entity) fixtures.get(i).getBody().getUserData();
                    Vector2 vector2 = fixtures.get(i).getBody().getPosition();
                    physicsBody.body.setLinearVelocity(vector2.x - physicsBody.body.getPosition().x, vector2.y - physicsBody.body.getPosition().y);
                }
            }
        }
    }

    /**
     * Allocate box2d resources for the entity containing physics and battle bodies.
     */
    private void createMarinePhysics(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        Marine.createPhysics(getWorld(), e, box2dWorld, physicsBody.initialX, physicsBody. initialY);
    }

}
