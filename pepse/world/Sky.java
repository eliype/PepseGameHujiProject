package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * represent the sky class of the game
 *  @author Eliyahu Peretz & Rom Ilany
 */
public class Sky extends GameObject {
	private static final String SKY_TAG = "sky";
	private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

	/**
	 * Construct a new GameObject instance.
	 *
	 * @param topLeftCorner Position of the object, in window coordinates (pixels).
	 *                      Note that (0,0) is the top-left corner of the window.
	 * @param dimensions    Width and height in window coordinates.
	 * @param renderable    The renderable representing the object. Can be null, in which case
	 *                      the GameObject will not be rendered.
	 */
	public Sky(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
		super(topLeftCorner, dimensions, renderable);
	}

	/**
	 * Creates a sky GameObject that fills the entire window with a sky-blue color
	 * and moves with the camera.
	 *
	 * @param windowDimensions The dimensions of the game window.
	 * @return A GameObject representing the sky.
	 */
	public static GameObject create(Vector2 windowDimensions) {

		GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
				new RectangleRenderable(BASIC_SKY_COLOR));

		// object moves with the camera
		sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sky.setTag(SKY_TAG);
		return sky;
	}

}
