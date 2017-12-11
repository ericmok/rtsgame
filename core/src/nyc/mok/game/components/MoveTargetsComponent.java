package nyc.mok.game.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class MoveTargetsComponent extends Component{
	public float maxSpeed = 10f;

	/// Approx how many seconds to reach max speed
	public float rampUpToMaxSpeedTimeFactor = 0.3f;

	// Lower is faster
	public float torqueFactor = 8f;

	@EntityId public int entityToMoveTowards = -1;
	// To add player paths / fields
}
