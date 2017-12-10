package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

import nyc.mok.game.components.BattleUnitComponent;
import nyc.mok.game.components.PhysicsBody;

/**
 * Created by taco on 12/9/17.
 */

public class RenderBattleUnitSystem extends EntityProcessingSystem {
    public RenderBattleUnitSystem() {
        super(Aspect.all(BattleUnitComponent.class, PhysicsBody.class));
    }

    @Override
    protected void process(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        BattleUnitComponent battleUnitComponent = getWorld().getMapper(BattleUnitComponent.class).get(e);


    }
}
