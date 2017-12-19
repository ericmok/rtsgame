package nyc.mok.game.units;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;

import nyc.mok.game.components.DeathAnimation;
import nyc.mok.game.components.EntityType;

/**
 * Created by taco on 12/18/17.
 */

public class DeathFlag {

	private static World previousWorld = null;

	private static final ArchetypeBuilder archetypeBuilder = new ArchetypeBuilder()
			.add(EntityType.class)
			.add(DeathAnimation.class);

	private static Archetype archetype = null;

	public static Entity create(World world, float x, float y) {

		if (archetype == null || world != previousWorld) {
			archetype = archetypeBuilder.build(world);
			previousWorld = world;
		}

		int i = world.create(archetype);
		Entity e = world.getEntity(i);

		//world.getMapper(EntityType.class).create(e);
		DeathAnimation deathAnimation = world.getMapper(DeathAnimation.class).create(e);
		deathAnimation.position.set(x, y);

		return e;
	}
}
