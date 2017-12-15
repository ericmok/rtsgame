package nyc.mok.game.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;

import nyc.mok.game.systems.utils.Box2dContactListeners;

/**
 * Stub for box2d.
 */

public class Box2dSystem extends BaseSystem {
	private World box2dWorld;
	private Box2dContactListeners box2dContactListeners;

	public Box2dSystem(World box2dWorld) {
		this.box2dWorld = box2dWorld;
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

	}
}
