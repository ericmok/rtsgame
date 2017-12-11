package nyc.mok.game.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.PooledWeaver;

/**
 * Created by taco on 12/11/17.
 */

@PooledWeaver
public class DamageInflictionComponent extends Component {
	public BattleBehaviorComponent.AttackType attackType = BattleBehaviorComponent.AttackType.ROCK;
	public float damage = 1;

	public Entity target;
}
