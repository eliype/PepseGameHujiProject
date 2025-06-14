package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.Component;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents a transparent glowing halo around the sun.
 *
 * @author Eliyahu & Rom
 */
public class SunHalo {
	/*Diameter (width and height) of the halo in pixels */

	private static final int HALO_SIZE = 70;
	/* Tag used to identify the halo GameObject */

	private static final String TAG = "sun halo";
	/* The color of the halo: yellow with low opacity (alpha=20 out of 255) */

	private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

	/**
	 * Creates a sun halo that follows the sun's position.
	 *
	 * @param sun the sun GameObject to follow
	 * @return the halo GameObject
	 */
	public static GameObject create(GameObject sun) {
		Renderable circle = new OvalRenderable(HALO_COLOR);

		GameObject halo = new GameObject(
				Vector2.ZERO,
				new Vector2(HALO_SIZE, HALO_SIZE), circle);

		halo.setCenter(sun.getCenter()); // Initial alignment with sun
		halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		halo.setTag(TAG);

		// Continuously follow the sun's center
		halo.addComponent((float deltaTime) -> halo.setCenter(sun.getCenter()));
		return halo;
	}
}
