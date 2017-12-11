package nyc.mok.game.components;

import com.artemis.Component;

/**
 * Created by taco on 12/11/17.
 */

public class BattleAttackableComponent extends Component {
	public enum ArmorType {
		ROCK,
		PAPER,
		SCISSORS
	}
	public ArmorType armorType = ArmorType.ROCK;

	public int hp = 20;

	public boolean isAttackable = true;
	public int lastAttacker = -1;
}
