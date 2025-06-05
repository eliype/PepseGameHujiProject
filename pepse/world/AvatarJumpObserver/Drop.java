package pepse.world.AvatarJumpObserver;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * class representing a single drop
 * @author Eliyahu & Rom
 * a
 */
public class Drop extends GameObject {
	private static final float GRAVITY = 140;

	/**
	 * Construct a new GameObject instance.
	 *
	 * @param topLeftCorner Position of the object, in window coordinates (pixels).
	 *                      Note that (0,0) is the top-left corner of the window.
	 * @param dimensions    Width and height in window coordinates.
	 * @param renderable    The renderable representing the object. Can be null, in which case
	 *                      the GameObject will not be rendered.
	 */
	public Drop(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
		super(topLeftCorner, dimensions, renderable);
		transform().setAccelerationY(GRAVITY); // make the drop fall to the ground

	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
}
