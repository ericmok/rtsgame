package nyc.mok.game.components;

import com.artemis.Component;


public class BattleUnitComponent extends Component {
    public enum Type {
        MARINE,
        MEDIC,
        TANK
    }

    public Type typeToSpawn = Type.MARINE;

    public float targetAcquisitionRange = 20;
    public float attackRange = 4;
}
