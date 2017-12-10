package nyc.mok.game.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;


public class PhysicsBody extends Component {
    public float initialX = 0;
    public float initialY = 0;

    public Body body;
    public BodyDef bodyDef;
}
