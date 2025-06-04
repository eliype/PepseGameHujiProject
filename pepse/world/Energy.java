package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

public class Energy extends GameObject {
	private Supplier<Integer> energy;
	private TextRenderable renderable;
	/**
	 * Construct a new GameObject instance.
	 *
	 * @param topLeftCorner Position of the object, in window coordinates (pixels).
	 *                      Note that (0,0) is the top-left corner of the window.
	 * @param dimensions    Width and height in window coordinates.
	 * @param renderable    The renderable representing the object. Can be null, in which case
	 *                      the GameObject will not be rendered.
	 */
	public Energy(Vector2 topLeftCorner, Vector2 dimensions,
				  TextRenderable renderable,Supplier<Integer> energy) {
		super(topLeftCorner, dimensions, renderable);
		this.renderable = renderable;
		this.energy = energy;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		//System.out.println(this.energy.get());
		this.renderable.setString(Integer.toString(this.energy.get())+"%");
	}


}
