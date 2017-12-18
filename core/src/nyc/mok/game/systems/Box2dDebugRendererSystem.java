package nyc.mok.game.systems;


import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;


public class Box2dDebugRendererSystem extends BaseSystem {
	private Box2dSystem box2dSystem;
	private Box2DDebugRenderer box2DDebugRenderer;

	private OrthographicCamera orthographicCamera;
	Matrix4 copy = new Matrix4();

	public Box2dDebugRendererSystem(OrthographicCamera orthographicCamera) {
		super();
		this.orthographicCamera = orthographicCamera;
	}

	@Override
	protected void initialize() {
		super.initialize();
		box2DDebugRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);
	}

	@Override
	protected void processSystem() {
		box2DDebugRenderer.render(box2dSystem.getBox2dWorld(), copy.set(orthographicCamera.combined));
	}

	@Override
	protected void dispose() {
		super.dispose();
		box2DDebugRenderer.dispose();
	}
}
