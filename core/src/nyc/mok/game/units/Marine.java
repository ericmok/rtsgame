package nyc.mok.game.units;


import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

import nyc.mok.game.components.BattleAttackableComponent;
import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.BattleUnitTypeComponent;
import nyc.mok.game.components.MoveTargetsComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.components.PositionComponent;
import nyc.mok.game.components.SpawnLifecycleComponent;

public class Marine {

    public static final float RADIUS_METERS = 1;

    /**
     * Creates a marine.
     *
     * TODO: Needs recycling.
     *
     * @param ecs Artemis world to create marine for
     */
    public static void create(World ecs, float x, float y) {
        Entity e = ecs.createEntity();
        ecs.getMapper(PositionComponent.class).create(e).position.set(x, y);
        PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).create(e);
        physicsBody.initialX = x;
        physicsBody.initialY = y;
        ecs.getMapper(SpawnLifecycleComponent.class).create(e).lifeCycle = SpawnLifecycleComponent.LifeCycle.SPAWNING_RAW;

        BattleUnitTypeComponent battleUnitTypeComponent = ecs.getMapper(BattleUnitTypeComponent.class).create(e);
        battleUnitTypeComponent.battleUnitType = BattleUnitTypeComponent.BattleUnitType.MARINE;

        BattleAttackableComponent battleAttackableComponent = ecs.getMapper(BattleAttackableComponent.class).create(e);
        battleAttackableComponent.hp = 10;

        MoveTargetsComponent moveTargetsComponent = ecs.getMapper(MoveTargetsComponent.class).create(e);
        moveTargetsComponent.maxSpeed = 0.5f;

        ecs.getMapper(BattleBehaviorComponent.class).create(e);
    }

    /**
     * Create box2d bodies for all the components needed
     */
    public static void createPhysics(World ecs, Entity e, com.badlogic.gdx.physics.box2d.World box2dWorld, float x, float y) {

        PhysicsBody physicsBody = ecs.getMapper(PhysicsBody.class).get(e);
        BattleBehaviorComponent battleBehaviorComponent = ecs.getMapper(BattleBehaviorComponent.class).get(e);

        Gdx.app.log("ASDF", "ASDF");
        // TODO: Check if body already exists
        // TODO: Recycle bodyDef and circles if possible

        physicsBody.bodyDef = new BodyDef();
        physicsBody.bodyDef.type = BodyDef.BodyType.DynamicBody;
        physicsBody.bodyDef.position.set(physicsBody.initialX, physicsBody.initialY);

        CircleShape circle = new CircleShape();
        circle.setRadius(RADIUS_METERS);

        physicsBody.body = box2dWorld.createBody(physicsBody.bodyDef);

        physicsBody.body.createFixture(circle, 1f);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();

        physicsBody.body.setUserData(e);
//
//        battleBehaviorComponent.targetAcquisitionDef = new BodyDef();
//        battleBehaviorComponent.targetAcquisitionDef.battleUnitType = BodyDef.BodyType.DynamicBody;
//        battleBehaviorComponent.targetAcquisitionDef.position.set(physicsBody.initialX, physicsBody.initialY);
//
//        circle = new CircleShape();
//        circle.setRadius(128f * 8);
//
//        battleBehaviorComponent.targetAcquisition = box2dWorld.createBody(battleBehaviorComponent.targetAcquisitionDef);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = circle;
//        fixtureDef.isSensor = true;
//
//        battleBehaviorComponent.targetAcquisition.createFixture(fixtureDef);
//
//        circle.dispose();
    }
}
