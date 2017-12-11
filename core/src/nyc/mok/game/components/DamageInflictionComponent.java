package nyc.mok.game.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.PooledWeaver;

/**
 * Created by taco on 12/11/17.
 */

@PooledWeaver
public class DamageInflictionComponent extends Component {
	public BattleBehaviorComponent.AttackType attackType = BattleBehaviorComponent.AttackType.ROCK;
	public float damage = 1;

	@EntityId public int target = -1;
}
