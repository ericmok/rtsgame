package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.MoveTargetsComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.SpawnLifecycleComponent;

/**
 * Created by taco on 12/9/17.
 * Handles spawning new units and resource acquisition for the task
 */

public class BattleUnitSystem extends EntityProcessingSystem {
	private World box2dWorld;

	// These are injected
	private ComponentMapper<PositionComponent> positionComponentComponentMapper;
	private ComponentMapper<SpawnLifecycleComponent> spawnLifecycleComponentComponentMapper;
	private ComponentMapper<BattleBehaviorComponent> battleBehaviorComponentMapper;
	private ComponentMapper<BattleAttackableComponent> battleAttackableComponentComponentMapper;
	private ComponentMapper<MoveTargetsComponent> moveTargetsMapper;
	private ComponentMapper<PhysicsBody> physicsBodyComponentMapper;

	private Vector2 acc = new Vector2();

	public BattleUnitSystem(World box2dWorld) {
		super(Aspect.all(
				PositionComponent.class,
				SpawnLifecycleComponent.class,
				BattleBehaviorComponent.class,
				BattleAttackableComponent.class,
				MoveTargetsComponent.class,
				PhysicsBody.class));

		this.box2dWorld = box2dWorld;
	}

	@Override
	public void removed(Entity e) {
		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);
		box2dWorld.destroyBody(physicsBody.body);
	}

	class Box2dQueryCallbackSortedByClosest implements QueryCallback {
		private boolean bodyQuery = true;

		public Body body;
		private Vector2 pos = new Vector2();

		private ArrayList<Fixture> fixtures = new ArrayList<Fixture>(32);

		public Box2dQueryCallbackSortedByClosest queryRangeForBody(Body body, float range) {
			bodyQuery = true;
			this.body = body;

			fixtures.clear();

            pos.set(body.getPosition());

			box2dWorld.QueryAABB(this,
					pos.x - range,
					pos.y - range,
					pos.x + range,
					pos.y + range);

			return this;
		}

		public Box2dQueryCallbackSortedByClosest queryAABB(float x, float y, float range) {
			bodyQuery = false;
            fixtures.clear();

            pos.set(x, y);
			box2dWorld.QueryAABB(this,
					x - range,
					y - range,
					x + range,
					y + range);

			return this;
		}

		@Override
		public boolean reportFixture(Fixture fixture) {
			if (bodyQuery) {
				// TODO: Go through fixture list
				if (!body.getFixtureList().contains(fixture, true)) {
					fixtures.add(fixture);
				}
			}
			else {
				fixtures.add(fixture);
			}
			return true;
		}

		public ArrayList<Fixture> finishReport() {
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

	Box2dQueryCallbackSortedByClosest box2DQueryCallbackSortedByClosest = new Box2dQueryCallbackSortedByClosest();

	public BattleBehaviorComponent.BattleState doHasNoTargetBehavior(Entity e) {
		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);
		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);

		// TODO: Handle field forces
		physicsBody.body.setLinearVelocity(0,0);

		// TODO: FILTER FOR PHYSICS BODIES THAT HAVE THE RIGHT COMPONENTS
		ArrayList<Fixture> fixtures = box2DQueryCallbackSortedByClosest.queryRangeForBody(physicsBody.body, battleBehaviorComponent.targetAcquisitionRange).finishReport();

		if (fixtures.size() > 0) {

			if (!physicsBody.body.getFixtureList().contains(fixtures.get(0), true)) {
				Entity otherEntity = (Entity) fixtures.get(0).getBody().getUserData();
				BattleBehaviorComponent otherBattleBehaviorComponent = battleBehaviorComponentMapper.get(otherEntity);

				// TODO: Test this for non battle physics objects
				BattleAttackableComponent otherBattleAttackableComponent = battleAttackableComponentComponentMapper.get(otherEntity);

				if (otherBattleAttackableComponent.isAttackable && otherBattleAttackableComponent.hp > 0) {
					battleBehaviorComponent.target = otherEntity.getId();

					return BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;
				}

				// Debugging
				//Vector2 vector2 = fixtures.get(0).getBody().getPosition();
				//physicsBody.body.setLinearVelocity(vector2.x - physicsBody.body.getPosition().x, vector2.y - physicsBody.body.getPosition().y);
			}

		}

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

		if (physicsBody.body.getPosition().dst(targetPhysicsBody.body.getPosition()) <= battleBehaviorComponent.rangeToBeginAttacking) {
			battleBehaviorComponent.battleProgress = 0;
			moveTargetsMapper.get(e).entityToMoveTowards = -1;

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

		return BattleBehaviorComponent.BattleState.CASTING;
	}

	@Override
	protected void process(Entity e) {
		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorComponentMapper.get(e);

		// Test HP <= 0 and remove with every hp mutation

		// Only in any state, field forces can interrupt movement?
		// or... add a new battle state "DirectedBattleMovement"

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
		}

	}

}
