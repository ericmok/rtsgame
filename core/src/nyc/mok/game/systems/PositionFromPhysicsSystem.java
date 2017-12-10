package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;

/**
 * Copies physics position to position
 */

public class PositionFromPhysicsSystem extends EntityProcessingSystem {
    public PositionFromPhysicsSystem() {
        super(Aspect.all(PositionComponent.class, PhysicsBody.class));
    }

    @Override
    protected void process(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        PositionComponent positionComponent = getWorld().getMapper(PositionComponent.class).get(e);

        if (physicsBody.body != null) {
            positionComponent.position.set(physicsBody.body.getPosition().x, physicsBody.body.getPosition().y);
        }
    }
}
