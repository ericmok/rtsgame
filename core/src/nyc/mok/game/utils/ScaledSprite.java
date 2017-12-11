package nyc.mok.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nyc.mok.game.Constants;

public class ScaledSprite extends Sprite {

	public ScaledSprite() {
		super();
	}

	public ScaledSprite(Texture texture) {
		super(texture);
	}

	public void scaledDraw(SpriteBatch spriteBatch, float x, float y, float angleInDegrees) {
		setSize(2 * getTexture().getWidth() * Constants.PIXEL_TO_METERS, 2 * getTexture().getHeight() * Constants.PIXEL_TO_METERS);
		setOrigin(getWidth() / 2, getHeight() / 2);

		setPosition(x - getWidth() / 2, y - getHeight() / 2);
		setRotation(angleInDegrees);

		super.draw(spriteBatch);
	}

	public void scaledDraw(SpriteBatch spriteBatch, float x, float y, float dx, float dh, float angleInDegrees) {
		setSize(2 * dx * getTexture().getWidth() * Constants.PIXEL_TO_METERS, 2 * dh * getTexture().getHeight() * Constants.PIXEL_TO_METERS);
		setOrigin(getWidth() / 2, getHeight() / 2);

		setPosition(x - getWidth() / 2, y - getHeight() / 2);
		setRotation(angleInDegrees);

		super.draw(spriteBatch);
	}
}
