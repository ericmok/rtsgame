package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.components.PhysicsBody;

/**
 * Created by taco on 12/9/17.
 */

public class PhysicsSystem extends EntityProcessingSystem {
    private World box2dWorld;

    public PhysicsSystem(World box2dWorld) {
        super(Aspect.all(PhysicsBody.class));

        this.box2dWorld = box2dWorld;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void process(Entity e) {
        ComponentMapper<PhysicsBody> physicsBodyComponentMapper = world.getMapper(PhysicsBody.class);
        PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);

    }
}
