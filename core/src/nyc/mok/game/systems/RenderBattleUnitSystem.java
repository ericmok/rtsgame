package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
    OrthographicCamera orthographicCamera;
    Sprite sprite;
    ScaledSprite scaledSprite;

    public RenderBattleUnitSystem(SpriteBatch spriteBatch, OrthographicCamera orthographicCamera) {
        super(Aspect.all(BattleUnitTypeComponent.class, BattleBehaviorComponent.class, PhysicsBody.class));
        this.spriteBatch = spriteBatch;
        this.orthographicCamera = orthographicCamera;
    }

    @Override
    protected void initialize() {
        super.initialize();

        texture = new Texture(Gdx.files.internal("marine.png"));
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
        scaledSprite.scaledDraw(spriteBatch, physicsBody.body.getPosition().x, physicsBody.body.getPosition().y, physicsBody.body.getLinearVelocity().angle() - 90);
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
