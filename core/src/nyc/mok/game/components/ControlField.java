package nyc.mok.game.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by taco on 12/14/17.
 */

public class ControlField extends Component {
	public Vector2 initPos = new Vector2();
	public float initAngle = 0;
	public Body body;
}
