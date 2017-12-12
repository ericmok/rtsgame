package nyc.mok.game.units;


import com.artemis.Entity;
import com.artemis.World;

import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.BattleUnitTypeComponent;
import nyc.mok.game.components.PhysicsBody;

public class Marine {

    public static final float RADIUS_METERS = 1;
    public static final int HP = 10;
    public static final float SWING_TIME = 0.5f;
    public static final float COOLDOWN_TIME = 0.5f;

    /**
     * Creates a marine.
     *
     * TODO: Needs recycling.
     */
    public static Entity create(World ecs, float x, float y) {
        //Entity e = Common.create(ecs, x, y, Common.createDynamicBodyDef(x, y), Common.createCircleFixtureDef(1));
        Entity e = Common.createSimple(ecs, x, y);

        ecs.getMapper(BattleUnitTypeComponent.class).get(e).battleUnitType = BattleUnitTypeComponent.BattleUnitType.MARINE;

        BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
        battleAttackable.hp = HP;
        battleAttackable.armorType = BattleAttackableComponent.ArmorType.ROCK;

        BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);
        battleBehaviorComponent.swingTime = SWING_TIME;
        battleBehaviorComponent.cooldownTime = COOLDOWN_TIME;
        battleBehaviorComponent.attackType = BattleBehaviorComponent.AttackType.ROCK;

        PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).get(e);
        //physicsBody.initCircleBodyDefFixtureDef(x, y, 1);

        return e;
    }

    public static Entity createTriangle(World ecs, float x, float y) {
        Entity e = Common.createSimple(ecs, x, y);

        ecs.getMapper(BattleUnitTypeComponent.class).get(e).battleUnitType = BattleUnitTypeComponent.BattleUnitType.TRIANGLE;

        BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);
        battleBehaviorComponent.swingTime = SWING_TIME;
        battleBehaviorComponent.cooldownTime = COOLDOWN_TIME;
        battleBehaviorComponent.attackType = BattleBehaviorComponent.AttackType.PAPER;

        BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
        battleAttackable.hp = HP;
        battleAttackable.armorType = BattleAttackableComponent.ArmorType.PAPER;

        return e;
    }

    public static Entity createSquare(World ecs, float x, float y) {
        Entity e = Common.createSimple(ecs, x, y);

        ecs.getMapper(BattleUnitTypeComponent.class).get(e).battleUnitType = BattleUnitTypeComponent.BattleUnitType.SQUARE;

        BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);
        battleBehaviorComponent.swingTime = SWING_TIME;
        battleBehaviorComponent.cooldownTime = COOLDOWN_TIME;;
        battleBehaviorComponent.attackType = BattleBehaviorComponent.AttackType.SCISSORS;

        BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
        battleAttackable.hp = HP;
        battleAttackable.armorType = BattleAttackableComponent.ArmorType.SCISSORS;

        return e;
    }

//    public static void createPhysics(World ecs, Entity e, com.badlogic.gdx.physics.box2d.World box2dWorld, float x, float y) {
//        Common.createCircleBodyForPhysicsBody(ecs, e, box2dWorld, 1, x, y);
//    }
}
