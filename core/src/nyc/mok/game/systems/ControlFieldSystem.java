package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.managers.PlayerManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.Constants;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.ControlField;
import nyc.mok.game.components.ControlNode;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.systems.utils.BodyCaster;

/**
 * Created by taco on 12/14/17.
 */

public class ControlFieldSystem extends EntityProcessingSystem {
	//private World box2dWorld;

	private Box2dSystem box2dSystem;

	private ComponentMapper<PhysicsBody> physicsBodyMapper;
	private ComponentMapper<ControlNode> controlNodeMapper;

	private EntitySubscription entitySubscription;

	private PlayerManager playerManager;

	private BodyCaster<BattleBehaviorComponent> battleBehaviorComponentBodyCaster = new BodyCaster<BattleBehaviorComponent>(BattleBehaviorComponent.class);
	private BodyCaster<ControlField> controlFieldBodyCaster = new BodyCaster<ControlField>(ControlField.class);
	private BodyCaster<ControlNode> controlNodeBodyCaster = new BodyCaster<ControlNode>(ControlNode.class);

	private MovementSystem movementSystem;

	private Vector2 accOne = new Vector2();

	public ControlFieldSystem(World box2dWorld) {
		super(Aspect.all(PhysicsBody.class, ControlNode.class));
		//this.box2dWorld = box2dWorld;
	}

	public void processBeginContact(Fixture fixture, Fixture otherFixture) {
		Gdx.app.log("Begin", "Begin Contact");

		if (fixture.getBody() == otherFixture.getBody()) return;

		Entity supposedControlFieldEntity = (Entity)fixture.getBody().getUserData();
		Entity supposedControlNodeEntity = (Entity)otherFixture.getBody().getUserData();

		ControlField controlField = controlFieldBodyCaster.bodyGetComponent(getWorld(), fixture.getBody());

		// If fixture is a control field, we want to know if otherFixture is a moveTarget
		if (controlField != null) {

			BattleBehaviorComponent battleBehaviorComponent =
					battleBehaviorComponentBodyCaster.bodyGetComponent(getWorld(), otherFixture.getBody());

			ControlNode controlNode = controlNodeBodyCaster.bodyGetComponent(getWorld(), otherFixture.getBody());

			if (controlNode != null) {

				// Demo: move units towards the field
				//controlNode.positionToMoveTowards.set(controlField.body.getPosition());
				//controlNode.isActive = true;
				controlNode.fields.add(supposedControlFieldEntity.getId());
			}
		}
	}

	public void processEndContact(Fixture fixture, Fixture otherFixture) {
		Gdx.app.log("Begin", "End Contact");

		if (fixture.getBody() == otherFixture.getBody()) return;

		Entity supposedControlFieldEntity = (Entity)fixture.getBody().getUserData();
		Entity supposedControlNodeEntity = (Entity)otherFixture.getBody().getUserData();

		ControlField controlField = controlFieldBodyCaster.bodyGetComponent(getWorld(), fixture.getBody());

		// If fixture is a control field, we want to know if otherFixture is a moveTarget
		if (controlField != null) {

			BattleBehaviorComponent battleBehaviorComponent =
					battleBehaviorComponentBodyCaster.bodyGetComponent(getWorld(), otherFixture.getBody());

			ControlNode controlNode = controlNodeBodyCaster.bodyGetComponent(getWorld(), otherFixture.getBody());

			if (controlNode != null) {

				// Remove field units
				controlNode.fields.removeValue(supposedControlFieldEntity.getId());
			}
		}
	}

	public class AABBAwake implements QueryCallback {
		@Override
		public boolean reportFixture(Fixture fixture) {
			fixture.getBody().setAwake(true);
			return true;
		}
	}

	public AABBAwake aabbAwake = new AABBAwake();

	@Override
	protected void initialize() {
		entitySubscription = getWorld().getAspectSubscriptionManager().get(Aspect.all(ControlField.class));

		entitySubscription.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
			@Override
			public void inserted(IntBag entities) {
				for (int i = 0; i < entities.size(); i++) {
					ControlField controlField = getWorld().getMapper(ControlField.class).get(entities.get(i));

					BodyDef bodyDef = new BodyDef();
					bodyDef.type = BodyDef.BodyType.StaticBody;
					bodyDef.position.set(controlField.initPos.x, controlField.initPos.y);
					bodyDef.awake = true;
					bodyDef.allowSleep = false;
					bodyDef.active = true;
					bodyDef.angle = MathUtils.degRad * controlField.initAngle;

					CircleShape circleShape = new CircleShape();
					circleShape.setRadius(10);
					Body body = box2dSystem.getBox2dWorld().createBody(bodyDef);
					FixtureDef fixtureDef = new FixtureDef();
					fixtureDef.shape = circleShape;
					fixtureDef.isSensor = true;
					fixtureDef.filter.categoryBits = Constants.BOX2D_CATEGORY_FIELDS;
					fixtureDef.filter.maskBits = Constants.BOX2D_CATEGORY_UNITS;
					body.createFixture(fixtureDef);

					Entity entity = getWorld().getEntity(entities.get(i));
					body.setUserData(entity);

					controlField.body = body;

					box2dSystem.getBox2dWorld().QueryAABB(aabbAwake, body.getPosition().x - 10, body.getPosition().y - 10,
							body.getPosition().x + 10, body.getPosition().y + 10);
				}
			}

			@Override
			public void removed(IntBag entities) {
				for (int i = 0; i < entities.size(); i++) {
					ControlField controlField = getWorld().getMapper(ControlField.class).get(entities.get(i));

					if (controlField.body != null) {
						box2dSystem.getBox2dWorld().destroyBody(controlField.body);
					}
				}
			}
		});

		box2dSystem.addBox2dContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				processBeginContact(contact.getFixtureA(), contact.getFixtureB());
				processBeginContact(contact.getFixtureB(), contact.getFixtureA());
			}

			@Override
			public void endContact(Contact contact) {
				processEndContact(contact.getFixtureA(), contact.getFixtureB());
				processEndContact(contact.getFixtureB(), contact.getFixtureA());
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});
	}

	@Override
	public void inserted(Entity e) {

	}

	@Override
	protected void process(Entity e) {
		ControlNode controlNode = controlNodeMapper.get(e);
		PhysicsBody physicsBody = physicsBodyMapper.get(e);

		// Move physics body towards control
		if (controlNode.fields.size() > 0) {
			ControlField controlField = getWorld().getMapper(ControlField.class).get(controlNode.fields.get(0));

//			accOne.set(
//					(controlField.body.getPosition().x - physicsBody.body.getPosition().x),
//					(controlField.body.getPosition().y - physicsBody.body.getPosition().y)
//			);

			accOne.setAngle(controlField.body.getAngle() * MathUtils.radDeg);
			accOne.add(physicsBody.body.getPosition().x, physicsBody.body.getPosition().y);


			physicsBody.body.applyLinearImpulse(accOne.x, accOne.y, 0, 0, true);
		}
	}
}
