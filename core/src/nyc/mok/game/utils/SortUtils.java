package nyc.mok.game.utils;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nyc.mok.game.components.PhysicsBody;

/**
 * Created by taco on 12/13/17.
 */

public class SortUtils {
	public static void sortBasedOnDist(final ArrayList<Fixture> fixtures, final Vector2 pos) {
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
	}

	public static void sortBasedOnDist(final ArrayList<Entity> entities, final World ecs, final Vector2 pos) {
		Collections.sort(entities, new Comparator<Entity>() {
			@Override
			public int compare(Entity entity, Entity t1) {
				PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).get(entity);
				PhysicsBody otherPhysicsBody = ecs.getMapper(PhysicsBody.class).get(t1);

				float res1 = physicsBody.body.getPosition().dst(pos);
				float res2 = otherPhysicsBody.body.getPosition().dst(pos);

				if (res1 > res2) return 1;
				if (res1 == res2) return 0;
				return -1;
			}
		});
	}
}
