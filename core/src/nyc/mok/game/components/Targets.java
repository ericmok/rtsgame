package nyc.mok.game.components;

import com.artemis.Component;
import com.artemis.Entity;

import java.util.ArrayList;

/**
 * Created by taco on 12/13/17.
 */

public class Targets extends Component {
	public ArrayList<Entity> targets = new ArrayList<Entity>(10);
}
