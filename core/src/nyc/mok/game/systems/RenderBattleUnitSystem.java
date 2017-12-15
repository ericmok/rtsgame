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
import nyc.mok.game.components.EntityType;
import nyc.mok.game.components.PhysicsBody;
import nyc.mok.game.utils.ScaledSprite;

/**
 * Created by taco on 12/9/17.
 */

public class RenderBattleUnitSystem extends EntityProcessingSystem {
    SpriteBatch spriteBatch;
    Texture texture;
    Texture triangleTexture;
    Texture squareTexture;
    Texture simple_attack;
    OrthographicCamera orthographicCamera;
    Sprite sprite;

    Vector2 accOne = new Vector2();
    Vector2 accTwo = new Vector2();

    public RenderBattleUnitSystem(SpriteBatch spriteBatch, OrthographicCamera orthographicCamera) {
        super(Aspect.all(EntityType.class, BattleBehaviorComponent.class, PhysicsBody.class));
        this.spriteBatch = spriteBatch;
        this.orthographicCamera = orthographicCamera;
    }

    @Override
    protected void initialize() {
        super.initialize();

        texture = new Texture(Gdx.files.internal("marine.png"));
        triangleTexture = new Texture(Gdx.files.internal("triangle.png"));
        squareTexture = new Texture(Gdx.files.internal("square.png"));
        simple_attack = new Texture(Gdx.files.internal("simple_attack.png"));
        sprite = new Sprite(texture);
    }

    @Override
    protected void begin() {
        super.begin();
    }

    @Override
    protected void end() {
        super.end();
    }

    private void drawSimpleBattleUnit(PhysicsBody physicsBody, BattleBehaviorComponent battleBehaviorComponent, Texture texture, float radius) {
        //float radius = physicsBody.body.getFixtureList().get(0).getShape().getRadius();

//        scaledSprite.setTexture(texture);
//        scaledSprite.scaledDraw(spriteBatch,
//                physicsBody.body.getPosition().x, physicsBody.body.getPosition().y,
//                radius, radius,
//                MathUtils.radiansToDegrees * physicsBody.body.getAngle() - 90);
        spriteBatch.draw(texture,
                physicsBody.body.getPosition().x - radius,
                physicsBody.body.getPosition().y - radius,
                radius, radius,
                2 * radius, 2 * radius,
                1, 1,
                MathUtils.radiansToDegrees * physicsBody.body.getAngle() - 90,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);

        drawSimpleAttack(battleBehaviorComponent, physicsBody);
    }

    private void drawSimpleAttack(BattleBehaviorComponent battleBehaviorComponent, PhysicsBody physicsBody) {

        // Not sure if we need to check target again, swinging necessarily implies target...
        if (battleBehaviorComponent.target != -1 && battleBehaviorComponent.battleState == BattleBehaviorComponent.BattleState.SWINGING) {
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

            spriteBatch.draw(texture,
                    accOne.x - 0.5f,
                    accOne.y - 0.5f,
                    0.5f, 0.5f,
                    1, 1,
                    1, 1,
                    direction.angle(),
                    0, 0,
                    texture.getWidth(), texture.getHeight(),
                    false, false);
//            scaledSprite.scaledDraw(spriteBatch,
//                    accOne.x, accOne.y, 0.5f, 0.5f,
//                    direction.angle() - 90);
        }
    }

    @Override
    protected void process(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        EntityType entityType = getWorld().getMapper(EntityType.class).get(e);
        BattleBehaviorComponent battleBehaviorComponent = getWorld().getMapper(BattleBehaviorComponent.class).get(e);

        switch (entityType.type) {
            case TRIANGLE:
                drawSimpleBattleUnit(physicsBody, battleBehaviorComponent, triangleTexture, 1f);
                break;
            case SQUARE:
                drawSimpleBattleUnit(physicsBody, battleBehaviorComponent, squareTexture, 1f);
                break;
            default:
                drawSimpleBattleUnit(physicsBody, battleBehaviorComponent, texture, 1f);
                break;
        }
    }
}
