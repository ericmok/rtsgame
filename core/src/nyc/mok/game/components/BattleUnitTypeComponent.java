package nyc.mok.game.components;

import com.artemis.Component;

/**
 * Created by taco on 12/10/17.
 */

public class BattleUnitTypeComponent extends Component {
    public enum BattleUnitType {
        MARINE,
        MEDIC,
        TANK,
        CIRCLE,
        TRIANGLE,
        SQUARE
    }
    public BattleUnitType battleUnitType = BattleUnitType.MARINE;

}
