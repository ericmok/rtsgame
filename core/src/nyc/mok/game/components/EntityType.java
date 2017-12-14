package nyc.mok.game.components;

import com.artemis.Component;

/**
 * Created by taco on 12/10/17.
 */

public class EntityType extends Component {
    public enum Type {
        MARINE,
        MEDIC,
        TANK,
        CIRCLE,
        TRIANGLE,
        SQUARE,
        WALL
    }
    public Type type = Type.MARINE;

}
