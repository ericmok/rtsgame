package nyc.mok.game.components;

import com.artemis.Component;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;


public class ControlNode extends Component {
	public Vector2 positionToMoveTowards = new Vector2();
	public boolean isActive = false;

	public IntBag fields = new IntBag(10);
}
