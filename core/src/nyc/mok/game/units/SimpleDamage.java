package nyc.mok.game.units;

import com.artemis.Entity;
import com.artemis.World;

import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.DamageInflictionComponent;

/**
 * Created by taco on 12/11/17.
 */

public class SimpleDamage {

	public static final Entity create(World ecs, BattleBehaviorComponent.AttackType attackType, float damage, int target) {
		Entity entity = ecs.createEntity();
		DamageInflictionComponent damageInfliction = ecs.getMapper(DamageInflictionComponent.class).create(entity);

		damageInfliction.attackType = attackType;
		damageInfliction.damage = damage;
		damageInfliction.target = target;

		return entity;
	}
}
