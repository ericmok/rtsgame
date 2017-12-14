package nyc.mok.game.systems.utils;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by taco on 12/13/17.
 */

public class BodyCaster<A extends Component> {
	Class<A> type;

	public BodyCaster(Class<A> type) {
		this.type = type;
	}

	/**
	 * Attempts to get the component of a body's entity.
	 * Body's userData should be an Entity having the component.
	 *
	 * @param ecs
	 * @param body
	 * @return Null if neither the entity is not bound nor the component for the entity is not found.
	 */
	public A bodyGetComponent(World ecs, Body body) {
		Entity e = (Entity)body.getUserData();

		if (e != null) {
			ComponentMapper mapper = ecs.getMapper(type);
			A component = (A)mapper.get(e);

			if (component != null) {
				return component;
			}
		}

		return null;
	}
}
