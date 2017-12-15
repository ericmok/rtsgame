package nyc.mok.game.systems.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.ArrayList;


/**
 * Holds a list of contact listeners and invokes them
 */
public class Box2dContactListeners implements ContactListener {
	private ArrayList<ContactListener> contactListeners = new ArrayList<ContactListener>(4);

	public void addContactListener(ContactListener contactListener) {
		contactListeners.add(contactListener);
	}

	public void removeContactListener(ContactListener contactListener) {
		contactListeners.remove(contactListener);
	}

	@Override
	public void beginContact(Contact contact) {
		for (int i = 0; i < contactListeners.size(); i++) {
			contactListeners.get(i).beginContact(contact);
		}
	}

	@Override
	public void endContact(Contact contact) {
		for (int i = 0; i < contactListeners.size(); i++) {
			contactListeners.get(i).endContact(contact);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		for (int i = 0; i < contactListeners.size(); i++) {
			contactListeners.get(i).preSolve(contact, oldManifold);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		for (int i = 0; i < contactListeners.size(); i++) {
			contactListeners.get(i).postSolve(contact, impulse);
		}
	}
}
