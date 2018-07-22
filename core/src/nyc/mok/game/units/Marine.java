package nyc.mok.game.units;


import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.PlayerManager;

import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.EntityType;
import nyc.mok.game.components.MoveTargetsComponent;

public class Marine {

	public static final int HP = 10;
	public static final float SWING_TIME = 0.2f;
	public static final float COOLDOWN_TIME = 0.5f;

	/**
	 * Creates a marine.
	 *
	 * TODO: Needs recycling.
	 */
	public static Entity create(World ecs, PlayerManager playerManager, String player, float x, float y) {
		//Entity e = Common.create(ecs, x, y, Common.createDynamicBodyDef(x, y), Common.createCircleFixtureDef(1));
		Entity e = Common.create(ecs, playerManager, player, x, y);

		ecs.getMapper(EntityType.class).get(e).type = EntityType.Type.MARINE;

		BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
		battleAttackable.hp = HP;
		battleAttackable.armorType = BattleAttackableComponent.ArmorType.ROCK;

		BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);
		battleBehaviorComponent.swingTime = SWING_TIME;
		battleBehaviorComponent.cooldownTime = COOLDOWN_TIME;
		battleBehaviorComponent.attackType = BattleBehaviorComponent.AttackType.ROCK;

		return e;
	}

	public static Entity createTriangle(World ecs, PlayerManager playerManager, String player, float x, float y) {
		Entity e = Common.create(ecs, playerManager, player, x, y);

		ecs.getMapper(EntityType.class).get(e).type = EntityType.Type.TRIANGLE;

		BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);
		battleBehaviorComponent.swingTime = SWING_TIME;
		battleBehaviorComponent.cooldownTime = COOLDOWN_TIME;
		battleBehaviorComponent.attackType = BattleBehaviorComponent.AttackType.PAPER;

		BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
		battleAttackable.hp = HP;
		battleAttackable.armorType = BattleAttackableComponent.ArmorType.PAPER;

		return e;
	}

	public static Entity createSquare(World ecs, PlayerManager playerManager, String player, float x, float y) {
		Entity e = Common.create(ecs, playerManager, player, x, y);

		ecs.getMapper(EntityType.class).get(e).type = EntityType.Type.SQUARE;

		BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);
		battleBehaviorComponent.swingTime = SWING_TIME;
		battleBehaviorComponent.cooldownTime = COOLDOWN_TIME;;
		battleBehaviorComponent.attackType = BattleBehaviorComponent.AttackType.SCISSORS;

		BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
		battleAttackable.hp = HP;
		battleAttackable.armorType = BattleAttackableComponent.ArmorType.SCISSORS;

		return e;
	}

	public static Entity createZug(World ecs, PlayerManager playerManager, String player, float x, float y) {
		Entity e = Common.create(ecs, playerManager, player, x, y);

		ecs.getMapper(EntityType.class).get(e).type = EntityType.Type.ZUG;

		BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);
		battleBehaviorComponent.swingTime = SWING_TIME;
		battleBehaviorComponent.cooldownTime = COOLDOWN_TIME;;
		battleBehaviorComponent.attackType = BattleBehaviorComponent.AttackType.ROCK;
		battleBehaviorComponent.maxAttackRange = 2.2f;
		// Chosen so that it is less than 2.0 because default radius of marine is 1.0 (so it has 2.0 diameter)
		battleBehaviorComponent.rangeToBeginAttacking = 1.9f;

		BattleAttackableComponent battleAttackable = ecs.getMapper(BattleAttackableComponent.class).get(e);
		battleAttackable.hp = HP;
		battleAttackable.armorType = BattleAttackableComponent.ArmorType.SCISSORS;

		ecs.getMapper(MoveTargetsComponent.class).get(e).maxSpeed = 17;

		return e;
	}
//    public static void createPhysics(World ecs, Entity e, com.badlogic.gdx.physics.box2d.World box2dWorld, float x, float y) {
//        Common.createCircleBodyForPhysicsBody(ecs, e, box2dWorld, 1, x, y);
//    }
}
