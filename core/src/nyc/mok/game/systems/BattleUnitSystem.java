package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import nyc.mok.game.Constants;
import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.MoveTargetsComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.SpawnLifecycleComponent;
import nyc.mok.game.components.Targets;
import nyc.mok.game.utils.Box2dQueries;

/**
 * Created by taco on 12/9/17.
 * Handles spawning new units and resource acquisition for the task
 */

public class BattleUnitSystem extends EntityProcessingSystem {
	private World box2dWorld;

	// These are injected
	private ComponentMapper<PositionComponent> positionComponentComponentMapper;
	private ComponentMapper<SpawnLifecycleComponent> spawnLifecycleComponentComponentMapper;
	private ComponentMapper<Targets> targetsComponentMapper;
	private ComponentMapper<BattleBehaviorComponent> battleBehaviorComponentMapper;
	private ComponentMapper<BattleAttackableComponent> battleAttackableComponentComponentMapper;
	private ComponentMapper<MoveTargetsComponent> moveTargetsMapper;
	private ComponentMapper<PhysicsBody> physicsBodyComponentMapper;

	private Vector2 acc = new Vector2();

	public BattleUnitSystem(World box2dWorld) {
		super(Aspect.all(
				PositionComponent.class,
				SpawnLifecycleComponent.class,
				Targets.class,
				BattleBehaviorComponent.class,
				BattleAttackableComponent.class,
				MoveTargetsComponent.class,
				PhysicsBody.class));

		this.box2dWorld = box2dWorld;
	}

	@Override
	protected void begin() {
	}

	@Override
	public void removed(Entity e) {
	}

	public int getTargetUsingSensors(PhysicsBody physicsBody, BattleBehaviorComponent battleBehaviorComponent) {
		// TODO: Is this slower?
		return  -1;
	}

	/**
	 * @return The entityId of the target. -1 if no target.
	 */
	public int getTargetUsingWorldQuery(PhysicsBody physicsBody, BattleBehaviorComponent battleBehaviorComponent) {
		// TODO: FILTER FOR PHYSICS BODIES THAT HAVE THE RIGHT COMPONENTS
		ArrayList<Fixture> fixtures = 	Box2dQueries.instance(box2dWorld).closest(Constants.BOX2D_CATEGORY_UNITS, Constants.BOX2D_CATEGORY_UNITS, (short)0).queryRangeForBody(physicsBody.body, battleBehaviorComponent.targetAcquisitionRange).finishReport();

		boolean fixtureForBattleUnitFound = false;
		if (fixtures.size() > 0) {

			if (!physicsBody.body.getFixtureList().contains(fixtures.get(0), true)) {
				Entity otherEntity = (Entity) fixtures.get(0).getBody().getUserData();
				BattleBehaviorComponent otherBattleBehaviorComponent = battleBehaviorComponentMapper.get(otherEntity);
				BattleAttackableComponent otherBattleAttackableComponent = battleAttackableComponentComponentMapper.get(otherEntity);

				// Test if the bodies are actually battle units as opposed to walls / doodads
				if (otherBattleBehaviorComponent != null || otherBattleAttackableComponent != null) {

					if (otherBattleAttackableComponent.isAttackable && otherBattleAttackableComponent.hp > 0) {
						battleBehaviorComponent.target = otherEntity.getId();

						return battleBehaviorComponent.target;
						//return BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;
					}

					// Debugging
					//Vector2 vector2 = fixtures.get(0).getBody().getPosition();
					//physicsBody.body.setLinearVelocity(vector2.x - physicsBody.body.getPosition().x, vector2.y - physicsBody.body.getPosition().y);
				}
			}

		}

		return -1;
	}


	public BattleBehaviorComponent.BattleState doHasNoTargetBehavior(Entity e) {
		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);
		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);
		Targets targets = targetsComponentMapper.get(e);

		// World query method
//		if (getTargetUsingWorldQuery(physicsBody, battleBehaviorComponent) != -1) {
//			return BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;
//		}


		if (targets.targets.size() > 0) {

//			// Test for dead entities due to staleness of the list
//			boolean validTarget = false;
//			int i = 0;
//			while (!validTarget && i <= battleBehaviorComponent.targets.size()) {
//				battleBehaviorComponent.target = battleBehaviorComponent.targets.get(i).getId();
//				if (battleBehaviorComponent.target != -1) {
//					validTarget = true;
//					battleBehaviorComponent.battleState = BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;
//				}
//				i += 1;
//			}

			// Some of these ids might be stale?
			battleBehaviorComponent.target = targets.targets.get(0).getId();
			return BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;
		}

		// Sensor method is to use the contact listener...

		// Don't change state yet
		return BattleBehaviorComponent.BattleState.HAS_NO_TARGET;
	}

	public BattleBehaviorComponent.BattleState doMoveTowardsTarget(Entity e) {
		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);

		// Hopefully EntityLinkSystem will manage this
		if (battleBehaviorComponent.target == BattleBehaviorComponent.NO_ENTITY) {
			return BattleBehaviorComponent.BattleState.HAS_NO_TARGET;
		}

		BattleAttackableComponent targetBattleAttackableComponent = battleAttackableComponentComponentMapper.get(battleBehaviorComponent.target);

		// Maybe it got attacked earlier - but component needs to removed when hp <= 0 asap
		// to ignore this case...
		if (targetBattleAttackableComponent.hp <= 0) {
			return BattleBehaviorComponent.BattleState.HAS_NO_TARGET;
		}

		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);
		PhysicsBody targetPhysicsBody = physicsBodyComponentMapper.get(battleBehaviorComponent.target);

		// TODO: rangeToBeginAttacking is target for movement, but its never reached...
		if (physicsBody.body.getPosition().dst(targetPhysicsBody.body.getPosition()) <= (battleBehaviorComponent.maxAttackRange) ) {
			battleBehaviorComponent.battleProgress = 0;

			//moveTargetsMapper.get(e).entityToMoveTowards = -1;

			return BattleBehaviorComponent.BattleState.SWINGING;
		}

		moveTargetsMapper.get(e).entityToMoveTowards = battleBehaviorComponent.target;

		return BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;
	}

	public BattleBehaviorComponent.BattleState doSwinging(Entity e) {
		// Slow the unit down gradually if it is swinging
		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);
		physicsBody.body.getLinearVelocity().scl(0.001f);

		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);

		if (battleBehaviorComponent.target == BattleBehaviorComponent.NO_ENTITY) {
			return BattleBehaviorComponent.BattleState.HAS_NO_TARGET;
		}

		battleBehaviorComponent.battleProgress += Gdx.graphics.getDeltaTime();

		if (battleBehaviorComponent.battleProgress >= battleBehaviorComponent.swingTime) {
			// SWING means managing attack types like AOE, missiles, instant
			return BattleBehaviorComponent.BattleState.CASTING;
		}

		return BattleBehaviorComponent.BattleState.SWINGING;
	}

	public BattleBehaviorComponent.BattleState doCasting(Entity e) {

		// Test if target still valid and within range
		// Create damage event: instant, missile launch, aoe launch, aoeLinear launch
		// Handle damage elsewhere..?
		// Go to cooldown

		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);
		battleBehaviorComponent.battleProgress = 0;

		if (battleBehaviorComponent.target != BattleBehaviorComponent.NO_ENTITY) {
			BattleBehaviorComponent targetBattleBehavior = battleBehaviorComponentMapper.get(battleBehaviorComponent.target);
			BattleAttackableComponent targetBattleAttackable = battleAttackableComponentComponentMapper.get(battleBehaviorComponent.target);

			boolean hasDied = BattleUnitSystem.inflictDamage(battleBehaviorComponent, targetBattleAttackable);

			if (hasDied) {
				getWorld().delete(battleBehaviorComponent.target);
				battleBehaviorComponent.target = -1; // Should be managed, but doing it anyway to be explicit
				targetBattleAttackable.lastAttacker = -1; // Can we do this? Not sure if this is recycled correctly
			}
			else {
				targetBattleAttackable.lastAttacker = e.getId();
			}
		} else {
			// If it never casted... why wait for cooldown?
			return BattleBehaviorComponent.BattleState.HAS_NO_TARGET;
		}

		battleBehaviorComponent.battleProgress = 0;
		return BattleBehaviorComponent.BattleState.WAITING_FOR_COOLDOWN;
	}

	public BattleBehaviorComponent.BattleState doWaitingForCooldown(Entity e) {
		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);

		battleBehaviorComponent.battleProgress += Gdx.graphics.getDeltaTime();

		if (battleBehaviorComponent.battleProgress >= battleBehaviorComponent.cooldownTime) {
			battleBehaviorComponent.battleProgress = 0;
			return BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;
		}

		return BattleBehaviorComponent.BattleState.WAITING_FOR_COOLDOWN;
	}

	/**
	 * @param battleBehaviorComponent The attacker
	 * @param battleAttackableComponent The one receiving the damage
	 * @return Returns true if the attackable component has died
	 */
	public static final boolean inflictDamage(BattleBehaviorComponent battleBehaviorComponent, BattleAttackableComponent battleAttackableComponent) {

		BattleBehaviorComponent.AttackType attackType = battleBehaviorComponent.attackType;
		BattleAttackableComponent.ArmorType armorType = battleAttackableComponent.armorType;

		float damage = battleBehaviorComponent.attackDamage;
		float bonus = Constants.RPS_BONUS_DAMAGE_FACTOR;

		// TODO: Apply RCS sytsem here
		if (attackType == BattleBehaviorComponent.AttackType.ROCK) {
			if (armorType == BattleAttackableComponent.ArmorType.SCISSORS) {
				damage *= bonus;
			}
		} else if (attackType == BattleBehaviorComponent.AttackType.PAPER) {
			if (armorType == BattleAttackableComponent.ArmorType.ROCK) {
				damage *= bonus;
			}
		} else { // Scissors
			if (armorType == BattleAttackableComponent.ArmorType.PAPER) {
				damage *= bonus;
			}
		}

		battleAttackableComponent.hp -= damage;
		return battleAttackableComponent.hp <= 0;
	}

	@Override
	protected void process(Entity e) {
		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);

		// Should Test if target valid, otherwise always has no target...unless during cooldown...
		// The target might die during casting or cooldown
		// Test HP <= 0 and remove with every hp mutation

		// Only in any state, field forces can interrupt movement?
		// or... add a new battle state "DirectedBattleMovement"

		// TODO: have transition function helpers to deal with changing battle progress

		switch (battleBehaviorComponent.battleState) {
			case HAS_NO_TARGET:
				battleBehaviorComponent.battleState = doHasNoTargetBehavior(e);
				break;
			case MOVING_TOWARDS_TARGET:
				battleBehaviorComponent.battleState = doMoveTowardsTarget(e);
				break;
			case SWINGING:
				battleBehaviorComponent.battleState = doSwinging(e);
				break;
			case CASTING:
				battleBehaviorComponent.battleState = doCasting(e);
				break;
			case WAITING_FOR_COOLDOWN:
				battleBehaviorComponent.battleState = doWaitingForCooldown(e);
				break;
		}

	}

}
