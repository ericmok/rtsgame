package nyc.mok.game.units;


import com.artemis.Entity;
import com.artemis.World;

import nyc.mok.game.components.BattleAttackableComponent;

public class Marine {

    public static final float RADIUS_METERS = 1;

    /**
     * Creates a marine.
     *
     * TODO: Needs recycling.
     */
    public static Entity create(World ecs, float x, float y) {
        Entity e = Common.create(ecs, x, y);

        BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
        battleAttackable.hp = 10;

        return e;
    }

    public static void createPhysics(World ecs, Entity e, com.badlogic.gdx.physics.box2d.World box2dWorld, float x, float y) {
        Common.createPhysics(ecs, e, box2dWorld, 1, x, y);
    }
}
