package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nyc.mok.game.components.BattleUnitComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.SpawnLifecycleComponent;

/**
 * Created by taco on 12/9/17.
 * Handles spawning new units and resource acquisition for the task
 */

public class BattleUnitSystem extends EntityProcessingSystem {
	private World box2dWorld;

	// These are injected
	private ComponentMapper<PositionComponent> positionComponentComponentMapper;
	private ComponentMapper<SpawnLifecycleComponent> spawnLifecycleComponentComponentMapper;
	private ComponentMapper<BattleUnitComponent> battleUnitComponentComponentMapper;
	private ComponentMapper<PhysicsBody> physicsBodyComponentMapper;

	public BattleUnitSystem(World box2dWorld) {
		super(Aspect.all(PositionComponent.class, SpawnLifecycleComponent.class, BattleUnitComponent.class, PhysicsBody.class));
		this.box2dWorld = box2dWorld;
	}

	@Override
	public void removed(Entity e) {
		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);
		box2dWorld.destroyBody(physicsBody.body);
	}

	class Box2dQueryCallbackSortedByClosest implements QueryCallback {
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

		@Override
		public boolean reportFixture(Fixture fixture) {
			if (bodyQuery) {
				// TODO: Go through fixture list
				if (body.getFixtureList().get(0) != fixture) {
					fixtures.add(fixture);
				}
			}
			else {
				fixtures.add(fixture);
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

	Box2dQueryCallbackSortedByClosest box2DQueryCallbackSortedByClosest = new Box2dQueryCallbackSortedByClosest();

	public BattleUnitComponent.BattleState doHasNoTargetBehavior() {

		// Don't change state yet
		return BattleUnitComponent.BattleState.HAS_NO_TARGET;
	}

	@Override
	protected void process(Entity e) {

		BattleUnitComponent battleUnitComponent = battleUnitComponentComponentMapper.get(e);
		PhysicsBody physicsBody = physicsBodyComponentMapper.get(e);

		switch (battleUnitComponent.battleState) {
			case HAS_NO_TARGET:
				doHasNoTargetBehavior();
				break;
		}


		ArrayList<Fixture> fixtures = box2DQueryCallbackSortedByClosest.queryRangeForBody(physicsBody.body, battleUnitComponent.targetAcquisitionRange).finishReport();

		if (fixtures.size() > 0) {
			for (int i = 0; i < fixtures.size(); i++) {
				if (fixtures.get(i) != physicsBody.body.getFixtureList().get(0)) {
					Entity entity = (Entity) fixtures.get(i).getBody().getUserData();
					Vector2 vector2 = fixtures.get(i).getBody().getPosition();
					physicsBody.body.setLinearVelocity(vector2.x - physicsBody.body.getPosition().x, vector2.y - physicsBody.body.getPosition().y);
				}
			}
		}
	}

}
