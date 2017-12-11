package nyc.mok.game.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nyc.mok.game.components.BattleUnitComponent;
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
        super(Aspect.all(BattleUnitComponent.class, PhysicsBody.class));
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

    @Override
    protected void process(Entity e) {
        PhysicsBody physicsBody = getWorld().getMapper(PhysicsBody.class).get(e);
        BattleUnitComponent battleUnitComponent = getWorld().getMapper(BattleUnitComponent.class).get(e);


        float radius = physicsBody.body.getFixtureList().get(0).getShape().getRadius();

//        sprite.setTexture(texture);
//        sprite.setSize(2 *texture.getWidth() * MyGame.PIXEL_TO_METERS, 2 * texture.getHeight() * MyGame.PIXEL_TO_METERS);
//        sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
//
//        //sprite.setPosition(physicsBody.body.getPosition().x - radius, physicsBody.body.getPosition().y - radius);
//        sprite.setPosition(physicsBody.body.getPosition().x - radius, physicsBody.body.getPosition().y - radius);
//
//        sprite.setRotation(physicsBody.body.getLinearVelocity().angle() - 90);
//        sprite.draw(spriteBatch);
        scaledSprite.setTexture(texture);
        scaledSprite.setPosition(physicsBody.body.getPosition().x - radius, physicsBody.body.getPosition().y - radius);
        scaledSprite.setRotation(physicsBody.body.getLinearVelocity().angle() - 90);
        scaledSprite.scaledDraw(spriteBatch, physicsBody.body.getPosition().x, physicsBody.body.getPosition().y, physicsBody.body.getLinearVelocity().angle() - 90);
    }
}
