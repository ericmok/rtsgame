package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import nyc.mok.game.components.BattleBehaviorComponent;
import nyc.mok.game.components.BattleUnitTypeComponent;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.utils.ScaledSprite;

/**
 * Created by taco on 12/9/17.
 */

public class RenderBattleUnitSystem extends EntityProcessingSystem {
    SpriteBatch spriteBatch;
    Texture texture;
    Texture simple_attack;
    OrthographicCamera orthographicCamera;
    Sprite sprite;
    ScaledSprite scaledSprite;

    Vector2 accOne = new Vector2();
    Vector2 accTwo = new Vector2();

    public RenderBattleUnitSystem(SpriteBatch spriteBatch, OrthographicCamera orthographicCamera) {
        super(Aspect.all(BattleUnitTypeComponent.class, BattleBehaviorComponent.class, PhysicsBody.class));
        this.spriteBatch = spriteBatch;
        this.orthographicCamera = orthographicCamera;
    }

    @Override
    protected void initialize() {
        super.initialize();

        texture = new Texture(Gdx.files.internal("marine.png"));
        simple_attack = new Texture(Gdx.files.internal("simple_attack.png"));
        sprite = new Sprite(texture);
        scaledSprite = new ScaledSprite(texture);
    }

    @Override
    protected void begin() {
        super.begin();
        spriteBatch.setProjectionMatrix(orthographicCamera.combined);
        spriteBatch.begin();
    }

    @Override
    protected void end() {
        super.end();
        spriteBatch.end();
    }

    private void drawMarine(PhysicsBody physicsBody, BattleBehaviorComponent battleBehaviorComponent) {
        float radius = physicsBody.body.getFixtureList().get(0).getShape().getRadius();

        scaledSprite.setTexture(texture);
        scaledSprite.scaledDraw(spriteBatch,
                physicsBody.body.getPosition().x, physicsBody.body.getPosition().y,
                MathUtils.radiansToDegrees * physicsBody.body.getAngle() - 90);
                //physicsBody.body.getLinearVelocity().angle() - 90);

        scaledSprite.setTexture(simple_attack);

        if (battleBehaviorComponent.target != -1) {
            PhysicsBody targetPhysicsBody = getWorld().getMapper(PhysicsBody.class).get(battleBehaviorComponent.target);

            accOne.set(
                    targetPhysicsBody.body.getPosition().x -
                            physicsBody.body.getPosition().x,
                    targetPhysicsBody.body.getPosition().y -
                            physicsBody.body.getPosition().y
            );

            Vector2 direction = accTwo.set(accOne);

            // Projectile "illusion" as a function of swingTime
            // Note: If you add a random component to the projectile, it looks like spears stabbing!
            accOne.scl(battleBehaviorComponent.battleProgress / battleBehaviorComponent.swingTime).add(
                    physicsBody.body.getPosition().x,
                    physicsBody.body.getPosition().y
            );

            scaledSprite.scaledDraw(spriteBatch,
                    accOne.x, accOne.y,
                    direction.angle() - 90);
        }
    }

    @Override
    protected void process(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        BattleUnitTypeComponent battleUnitTypeComponent = getWorld().getMapper(BattleUnitTypeComponent.class).get(e);
        BattleBehaviorComponent battleBehaviorComponent = getWorld().getMapper(BattleBehaviorComponent.class).get(e);

        switch (battleUnitTypeComponent.battleUnitType) {
            default:
                drawMarine(physicsBody, battleBehaviorComponent);
                break;
        }
    }
}
