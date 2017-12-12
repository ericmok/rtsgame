package nyc.mok.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Useful callbacks to pass to box2d world queries
 */

public class Box2dQueries {

	private static Box2dQueries box2dQueries;
	private World box2dWorld;

	private Filter filter = new Filter();
	private Box2dQueryCallbackSortedByClosest box2dQueryCallbackSortedByClosest = new Box2dQueryCallbackSortedByClosest();

	private Box2dQueries() {
	}

	public static Box2dQueries instance(World box2dWorld) {
		if (box2dQueries == null) {
			box2dQueries = new Box2dQueries();
		}
		box2dQueries.box2dWorld = box2dWorld;
		box2dQueries.filter.maskBits = 0;
		box2dQueries.filter.categoryBits = 0;
		box2dQueries.filter.groupIndex = 0;

		return box2dQueries;
	}

	public Box2dQueryCallbackSortedByClosest closest(short maskBits, short categoryBits, short groupIndex) {
		this.filter.maskBits = maskBits;
		this.filter.categoryBits = categoryBits;
		this.filter.groupIndex = groupIndex;

		return box2dQueryCallbackSortedByClosest;
	}

	public class Box2dQueryCallbackSortedByClosest implements QueryCallback {
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

		public boolean matchFixture(Fixture fixture) {
			short fixtureCategoryBits = fixture.getFilterData().categoryBits;
			if ((fixtureCategoryBits & filter.categoryBits) != 0) {
				return true;
			}
			return false;
		}

		@Override
		public boolean reportFixture(Fixture fixture) {
			if (bodyQuery) {
				// Check if the fixture belongs to the body which is not useful
				if (!body.getFixtureList().contains(fixture, true)) {
					if (matchFixture(fixture)) fixtures.add(fixture);
				}
			}
			else {
				if (matchFixture(fixture) )fixtures.add(fixture);
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
}
