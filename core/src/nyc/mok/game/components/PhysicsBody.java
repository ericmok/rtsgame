package nyc.mok.game.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;


public class PhysicsBody extends Component {
    public Body body;

    // TODO:
    // These are used to define the body at its creation time
    // Slight variations will require new defs for each component...
    // Instead, expected variations should probably be placed here.
    // Generic configuration should be handled elsewhere
    // Generic creation should be handled in a system
    //public BodyDef bodyDef;
    //public FixtureDef fixtureDef;
}
