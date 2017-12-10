package nyc.mok.game.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;


public class BattleUnitComponent extends Component {
    public enum Type {
        MARINE,
        MEDIC,
        TANK
    }

    public Type typeToSpawn = Type.MARINE;

    public BodyDef targetAcquisitionDef = new BodyDef();
    public Body targetAcquisition;

    public BodyDef attackRangeDef = new BodyDef();
    public Body attackRange;
}
