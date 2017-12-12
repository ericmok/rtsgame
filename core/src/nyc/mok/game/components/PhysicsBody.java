package nyc.mok.game.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;


public class PhysicsBody extends Component {
    public Body body;
    public BodyDef bodyDef;
    public FixtureDef fixtureDef;
}
