package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.ControlField;
import nyc.mok.game.components.ControlNode;
import nyc.mok.game.components.MoveTargetsComponent;
import nyc.mok.game.components.PhysicsBody;

/**
 * Created by taco on 12/11/17.
 */

public class MovementSystem extends EntityProcessingSystem {
	private ComponentMapper<PhysicsBody> physicsBodyMapper;
	private ComponentMapper<BattleBehaviorComponent> battleBehaviorMapper;
	private ComponentMapper<MoveTargetsComponent> moveTargetsMapper;
	private ComponentMapper<ControlNode> controlNodeMapper;
	private ComponentMapper<ControlField> controlFieldMapper;

	Vector2 acc = new Vector2();
	Vector2 accTwo = new Vector2();
	Vector2 accThree = new Vector2();

	public MovementSystem() {
		super(Aspect.all(PhysicsBody.class, BattleBehaviorComponent.class, MoveTargetsComponent.class, ControlNode.class));
	}

	/**
	 *
	 * @param body
	 * @param positionToMoveTowards
	 * @param fractionOfSpeedPerTimeStepToGetToMaxSpeed
	 * @param maxSpeed
	 * @param approachDistance If set equal to stop distance, approach won't get slowed
	 * @param stopDistance
	 */
	public void calculateAndSetVelocity(Body body, Vector2 positionToMoveTowards, float fractionOfSpeedPerTimeStepToGetToMaxSpeed, float maxSpeed, float approachDistance, float stopDistance) {

		acc.set(positionToMoveTowards.x - body.getPosition().x,
				positionToMoveTowards.y - body.getPosition().y);

		accTwo.set(acc);

		float dst = positionToMoveTowards.dst(body.getPosition());

		// Was needed before when impulse was applied incorrectly
		// body.setLinearVelocity(0, 0);

		if (stopDistance != approachDistance && dst > stopDistance && dst < approachDistance) {
			float clampedSpeed = maxSpeed * ((dst - stopDistance) / (approachDistance - stopDistance));
			accTwo.nor().scl(clampedSpeed);

			body.setLinearVelocity(accTwo);

//				accTwo.add(-body.getLinearVelocity().x, -body.getLinearVelocity().y);
//				body.applyLinearImpulse(accTwo, body.getPosition(), true);
		}
		else if (dst < stopDistance) {
			body.setLinearVelocity(0, 0);
		} else {
			//body.setLinearVelocity(accTwo.nor().scl(maxSpeed));

			accTwo.nor().scl(maxSpeed).add(-body.getLinearVelocity().x, -body.getLinearVelocity().y).scl(fractionOfSpeedPerTimeStepToGetToMaxSpeed);
			body.applyLinearImpulse(accTwo, body.getWorldCenter(), true);

			// Acceleration test...
//				accTwo.nor().scl(moveTargets.rampUpToMaxSpeedTimeFactor);
//				body.applyForceToCenter(accTwo.x, accTwo.y, true);
//				body.getLinearVelocity().clamp(0, maxSpeed);
		}
	}

	/**
	 *
	 * @param body
	 * @param positionToMoveTowards
	 * @param fractionOfSpeedPerTimeStepToGetToMaxSpeed
	 * @param maxSpeed
	 * @param approachDistance If set equal to stop distance, approach won't get slowed
	 * @param stopDistance
	 */
	public void calculateAndSetAcceleration(Body body, Vector2 positionToMoveTowards, float fractionOfSpeedPerTimeStepToGetToMaxSpeed, float maxSpeed, float approachDistance, float stopDistance) {

		acc.set(positionToMoveTowards.x - body.getPosition().x,
				positionToMoveTowards.y - body.getPosition().y);

		accTwo.set(acc);

		float dst = positionToMoveTowards.dst(body.getPosition());

		accTwo.nor().scl(maxSpeed * 10);
		body.applyForce(accTwo, body.getWorldCenter(), true);
		body.getLinearVelocity().clamp(0, maxSpeed);
	}

	public void calculateAndSetRotation(Body body, Vector2 targetDirection, float torqueFactor) {
		body.setAngularVelocity(0);

		Vector2 direction = acc;
		direction.set(targetDirection.x - body.getPosition().x,
				targetDirection.y - body.getPosition().y);


		float desiredAngle = direction.angle();
		float currentAngle = body.getAngle();
		currentAngle = (float) Math.toDegrees(currentAngle);

		float angleDiff = desiredAngle - currentAngle;

		if (angleDiff > 2 || angleDiff < -2) {
			float directionToSpin = angleDiff > 0 ? 1 : -1;

			if (angleDiff < -180) {
				angleDiff += 360;
				directionToSpin = 1;
			}
			if (angleDiff > 180) {
				angleDiff -= 360;
				directionToSpin = -1;
			}

			body.applyTorque(MathUtils.radDeg * angleDiff / torqueFactor, true);
			//body.setAngularVelocity((float) Math.toRadians(angleDiff));
		}
	}

		@Override
	protected void process(Entity e) {
		PhysicsBody physicsBody = physicsBodyMapper.get(e);
		MoveTargetsComponent moveTargets = moveTargetsMapper.get(e);
		BattleBehaviorComponent battleBehavior = battleBehaviorMapper.get(e);
		ControlNode controlNode = controlNodeMapper.get(e);

		float maxSpeed = moveTargetsMapper.get(e).maxSpeed;


		if (moveTargets.entityToMoveTowards != -1 && controlNode.fields.size() == 0) {
			PhysicsBody targetPhysicsBody = physicsBodyMapper.get(moveTargets.entityToMoveTowards);

			calculateAndSetVelocity(physicsBody.body, targetPhysicsBody.body.getPosition(), moveTargets.rampUpToMaxSpeedTimeFactor, maxSpeed, battleBehavior.maxAttackRange, battleBehavior.rangeToBeginAttacking);

			// Use this branch if engaging a target could mean not moving towards it
			//if (battleBehavior.target != -1) {

			calculateAndSetRotation(physicsBody.body, targetPhysicsBody.body.getPosition(), moveTargets.torqueFactor);

		} else if (controlNode.fields.size() > 0) {
			Vector2 summation = accTwo;

			summation.setZero();

			for (int i = 0; i< controlNode.fields.size(); i++) {
				acc.set(Vector2.X);

				ControlField controlField = controlFieldMapper.get(controlNode.fields.get(i));

				acc.setAngleRad(controlField.body.getAngle());

				float dst = accThree.set(
						controlField.body.getPosition().x - physicsBody.body.getPosition().x,
						controlField.body.getPosition().y - physicsBody.body.getPosition().y
				).dst(Vector2.Zero);

				summation.add(acc.scl(1 + dst));
			}

			summation.nor();
			summation.scl(maxSpeed);
			summation.add(physicsBody.body.getPosition());

			//calculateAndSetVelocity(physicsBody.body, summation, moveTargets.rampUpToMaxSpeedTimeFactor, maxSpeed, 0, 0);
			calculateAndSetAcceleration(physicsBody.body, summation, moveTargets.rampUpToMaxSpeedTimeFactor, maxSpeed, 0, 0);
			calculateAndSetRotation(physicsBody.body, summation, moveTargets.torqueFactor);

			// Important: If the unit moves out of range from the enemy target, then stop locking on to target
			// Not sure if this should be somewhere else...
			// One alternative is to readjust battling behavior so that units automatically lose lock
			// on targets after their swing (by transitioning COOLDOWN to HAS_NO_TARGET)
			// Also entityToMoveTowards is potentially redundant...
			if (moveTargets.entityToMoveTowards != -1) {
				PhysicsBody targetPhysicsBody = physicsBodyMapper.get(moveTargets.entityToMoveTowards);
				if (targetPhysicsBody.body.getPosition().dst2(physicsBody.body.getPosition()) > battleBehavior.targetAcquisitionRange) {
					battleBehavior.battleState = BattleBehaviorComponent.BattleState.HAS_NO_TARGET;
					moveTargets.entityToMoveTowards = -1;
				}
			}
		} else {
			physicsBody.body.setLinearDamping(1f / moveTargets.rampUpToMaxSpeedTimeFactor);
			physicsBody.body.setAngularVelocity(0);
		}
	}
}
