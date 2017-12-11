package nyc.mok.game.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class BattleBehaviorComponent extends Component {
	public static final int NO_ENTITY = -1;

	public enum BattleState {
		HAS_NO_TARGET,
		MOVING_TOWARDS_TARGET,
		SWINGING,
		CASTING,
		WAITING_FOR_COOLDOWN
	}

	public float battleProgress = 0;

	public BattleState battleState = BattleState.HAS_NO_TARGET;

	public float targetAcquisitionRange = 20;

	/// The fudge factor for node to walk within attack range to deal with
	/// various race conditions (attack swing time, round-off error)
	public float rangeToBeginAttacking = 5;

	public float maxAttackRange = 7;

	public enum WeaponType {
		SIMPLE,
		PROJECTILE_AOE,
		LINE_AOE,
		SUICIDE_AOE
	}

	public enum AttackType {
		ROCK,
		PAPER,
		SCISSORS
	}

	public WeaponType weaponType = WeaponType.SIMPLE;
	public AttackType attackType = AttackType.ROCK;

	public int attackDamage = 1;
	public float swingTime = 1;
	public float cooldownTime = 1;

	@EntityId public int target = NO_ENTITY;

	/// If the attack is sticky, the attack target is obtained at target acquisition time
	/// but not re-evaluated during attack swing
	public boolean nonCancellableSwing = false;

	/// If true, the node keeps attacking the previous target until it dies
	/// as opposed to just attacking the next closest target on every attack.
	public boolean lockOnAttack = false;

	public boolean attackSwingEvenWhenNotInRange = false;
}
