package nyc.mok.game;

/**
 * Created by taco on 12/10/17.
 */

public class Constants {

	public static float PIXEL_TO_METERS = 1.0f/256f;

	// 84
	public static float VIEWPORT_MIN_METERS = 60f;

	public static float RPS_BONUS_DAMAGE_FACTOR = 5f;

	public static short BOX2D_CATEGORY_ALL = 0xFF;
	public static short BOX2D_CATEGORY_UNITS = 0x1 << 1; // Skipping 1 for debugging
	public static short BOX2D_CATEGORY_ENV = 0x1 << 2;
	public static short BOX2D_CATEGORY_SENSORS = 0x1 << 3;
}
