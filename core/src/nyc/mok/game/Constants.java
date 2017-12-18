package nyc.mok.game;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

/**
 * Created by taco on 12/10/17.
 */

public class Constants {

	public static float PIXEL_TO_METERS = 1.0f/256f;

	// 84
	public static float VIEWPORT_HEIGHT_METERS = 84f;
	public static float MAP_WIDTH = (3f/4) * 80f;
	public static float MAP_HEIGHT = 80f;

	public static float RPS_BONUS_DAMAGE_FACTOR = 5f;

	public static short BOX2D_CATEGORY_ALL = 0xFF;
	public static short BOX2D_CATEGORY_UNITS = 0x1 << 1; // Skipping 1 for debugging
	public static short BOX2D_CATEGORY_ENV = 0x1 << 2;
	public static short BOX2D_CATEGORY_SENSORS = 0x1 << 3;
	public static short BOX2D_CATEGORY_FIELDS = 0x1 << 4;

	public static final String PLAYER_ONE = "1";
	public static final String PLAYER_TWO = "2";
	public static final String PLAYER_THREE = "3";


	public static final ArrayList<Float> TeamColors = new ArrayList<Float>() {{
		//this.add(Color.toFloatBits(250, 0, 191, 255));
		this.add(Color.toFloatBits(0, 191, 255, 255));
		this.add(Color.toFloatBits(208, 71, 132, 255));
		this.add(Color.toFloatBits(46, 204, 103, 255)); // Emerald

		// From flatcolors
		this.add(Color.toFloatBits(230, 126, 34, 255)); // Carrot
		this.add(Color.toFloatBits(155, 89, 182, 255)); // Amethyst
		this.add(Color.toFloatBits(241, 196,15, 255)); // Sunflower
		this.add(Color.toFloatBits(189, 195, 199, 255)); // Silver
	}};

	public static float getColorForPlayer(String player) {
		if (player.equals(PLAYER_ONE)) {
			return TeamColors.get(0);
		} else if (player.equals(PLAYER_TWO)) {
			return TeamColors.get(1);
		} else {
			return TeamColors.get(2);
		}
	}
}
