package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.managers.PlayerManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.Targets;
import nyc.mok.game.systems.utils.BodyCaster;
import nyc.mok.game.utils.SortUtils;

/**
 * Created by taco on 12/13/17.
 */

public class TargetsSystem extends EntityProcessingSystem {
	//private World box2dWorld;
	private Box2dSystem box2dSystem;

	private ComponentMapper<Targets> targetsComponentMapper;
	private ComponentMapper<PhysicsBody> physicsBodyMapper;
	private ComponentMapper<BattleBehaviorComponent> battleBehaviorMapper;
	private ComponentMapper<BattleAttackableComponent> battleAttackableMapper;

	private BodyCaster<BattleBehaviorComponent> battleBehaviorBodyCaster = new BodyCaster<BattleBehaviorComponent>(BattleBehaviorComponent.class);
	private BodyCaster<BattleAttackableComponent> battleAttackableBodyCaster = new BodyCaster<BattleAttackableComponent>(BattleAttackableComponent.class);
	private BodyCaster<Targets> targetsBodyCaster = new BodyCaster<Targets>(Targets.class);

	private EntitySubscription entitySubscription;
	private PlayerManager playerManager;

	public TargetsSystem(World box2dWorld) {
		super(Aspect.all(Targets.class, PhysicsBody.class, BattleBehaviorComponent.class, BattleAttackableComponent.class));
		//this.box2dWorld = box2dWorld;
	}


	@Override
	protected void initialize() {
		entitySubscription = getWorld().getAspectSubscriptionManager().get(Aspect.all(BattleAttackableComponent.class));

		// This approach doesn't solve the fact we have to go through each battle unit to update their targets
		// regardless of whether or not battle units have that target on the list...
//		entitySubscription.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
//			@Override
//			public void inserted(IntBag entities) {
//
//			}
//
//			@Override
//			public void removed(IntBag entities) {
//
//			}
//		});

		// This listener gets called at the END of the ECS step. Refer to the containing game loop for possible changes
		box2dSystem.addBox2dContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Gdx.app.log("Contact", "contact");
				cacheValidTargets(contact.getFixtureA(), contact.getFixtureB());
				cacheValidTargets(contact.getFixtureB(), contact.getFixtureA());
			}

			@Override
			public void endContact(Contact contact) {
				removeCachedTargets(contact.getFixtureA(), contact.getFixtureB());
				removeCachedTargets(contact.getFixtureB(), contact.getFixtureA());
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});
	}
	public void cacheValidTargets(Fixture fixtureA, Fixture fixtureB) {

		if (fixtureA.getBody() == fixtureB.getBody()) {
			return;
		}

		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorBodyCaster
				.bodyGetComponent(getWorld(), fixtureA.getBody());

		if (battleBehaviorComponent != null) {

			BattleAttackableComponent battleAttackable = battleAttackableBodyCaster
					.bodyGetComponent(getWorld(),fixtureB.getBody());

			if (battleAttackable != null) {

				if (battleAttackable.isAttackable &&
						battleAttackable.hp > 0 &&
						!playerManager.getPlayer((Entity)fixtureB.getBody().getUserData()).equals(
						playerManager.getPlayer((Entity)fixtureA.getBody().getUserData()))
					) {
					//battleBehaviorComponent.target = ((Entity)(fixtureB.getBody().getUserData())).getId();
					//battleBehaviorComponent.battleState = BattleBehaviorComponent.BattleState.MOVING_TOWARDS_TARGET;

					// A is the attacker so store B in A's targets
					Targets targets = targetsBodyCaster.bodyGetComponent(getWorld(), fixtureA.getBody());
					if (targets != null) {
						targets.targets.add((Entity) (fixtureB.getBody().getUserData()));

						// Sort for closest to A
						SortUtils.sortBasedOnDist(targets.targets, getWorld(), fixtureA.getBody().getPosition());
					}
				}
			}
		}
	}

	public void removeCachedTargets(Fixture fixtureA, Fixture fixtureB) {
		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorBodyCaster
				.bodyGetComponent(getWorld(), fixtureA.getBody());

		if (battleBehaviorComponent != null) {
			BattleAttackableComponent battleAttackable = battleAttackableBodyCaster
					.bodyGetComponent(getWorld(),fixtureB.getBody());

			if (battleAttackable != null) {
				Targets targets = targetsBodyCaster.bodyGetComponent(getWorld(), fixtureA.getBody());
				if (targets != null) {
					// Remove B from A
					targets.targets.remove(fixtureB.getBody().getUserData());
				}
			}
		}
	}

	@Override
	protected void process(Entity e) {
//		BattleBehaviorComponent battleBehaviorComponent = battleBehaviorMapper.get(e);
//		Targets targets = targetsComponentMapper.get(e);
//		PhysicsBody physicsBody = physicsBodyMapper.get(e);
//
//		if (battleBehaviorComponent.battleState == BattleBehaviorComponent.BattleState.HAS_NO_TARGET) {
////			// Get rid of stale targets
////			for (int i = 0; i < targets.targets.size(); i++) {
////				Entity entity = targets.targets.get(i);
////
////				BattleAttackableComponent battleAttackableComponent =battleAttackableMapper.get(entity);
////
////				if (battleAttackableComponent.hp <= 0) {
////					Gdx.app.log("targets", "less than hp stale target");
////				}
////
////				// Cases:
////				// Entity was removed, no end contact was called because it gets called after ECS runs
////				// Entity was set for removal, box2d hasn't removed it yet, battle system gets called
////			}
//		}
	}
}
