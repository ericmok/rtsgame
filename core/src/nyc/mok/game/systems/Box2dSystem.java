package nyc.mok.game.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.systems.utils.Box2dContactListeners;

/**
 * Stub for box2d.
 */

public class Box2dSystem extends BaseSystem {
	private World box2dWorld;
	private Box2dContactListeners box2dContactListeners;

	private float accumulator = 0;

	public Box2dSystem() {
		this.box2dWorld = new World(Vector2.Zero, true);
	}

	@Override
	protected void initialize() {
		super.initialize();
		box2dContactListeners = new Box2dContactListeners();
		box2dWorld.setContactListener(box2dContactListeners);
	}

	public void addBox2dContactListener(ContactListener contactListener) {
		box2dContactListeners.addContactListener(contactListener);
	}

	public void removeBox2ContactListener(ContactListener contactListener) {
		box2dContactListeners.removeContactListener(contactListener);
	}

	public World getBox2dWorld() {
		return box2dWorld;
	}

	@Override
	protected void processSystem() {
		doPhysicsStep(getWorld().getDelta());
	}


	private void doPhysicsStep(float deltaTime) {
		// fixed time step
		// max frame time to avoid spiral of death (on slow devices)
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while (accumulator >= 1f/60) {
			accumulator -= 1f/60;

			box2dWorld.step(1f/60, 6, 2);
		}
	}

	@Override
	protected void dispose() {
		super.dispose();
		box2dWorld.dispose();
	}
}
