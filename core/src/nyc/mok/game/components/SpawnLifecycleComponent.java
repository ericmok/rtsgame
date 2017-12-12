package nyc.mok.game.components;

import com.artemis.Component;



public class SpawnLifecycleComponent extends Component {

    public enum LifeCycle {
        SPAWNING_RAW,
        ALIVE,
        TO_RECYCLE,
        INACTIVE
    }

    public LifeCycle lifeCycle = LifeCycle.SPAWNING_RAW;

    public float initX;
    public float initY;
}
