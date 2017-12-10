package nyc.mok.game.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by taco on 12/9/17.
 */

public class PositionComponent extends Component implements Pool.Poolable {
    public Vector2 position = new Vector2();

    @Override
    public void reset() {
        position.setZero();
    }
}
