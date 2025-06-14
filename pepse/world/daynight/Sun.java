package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sun object with circular orbital animation.
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class Sun {
	/*
	 * Constant value 2, used for divisions and positioning calculations
	 */

	private static final float TWO = 2;
	/*
	 * Constant value 3, used for positioning calculations
	 */

	private static final float THREE = 3;
	/*
	 * Constant value 5, used for positioning calculations
	 */

	private static final float FIVE = 5;
	/*
	 * Tag used to identify the sun GameObject
	 */

	private static final String TAG = "sun";
	/*
	 * Diameter (width and height) of the sun object in pixels
	 */

	private static final int SUN_SIZE = 50;
	/*
	 * Starting angle for the sun orbit animation (0 degrees)
	 */

	private static final float INITIAL_VALUE = 0.0f;
	/*
	 * Ending angle for the sun orbit animation (360 degrees for full rotation)
	 */

	private static final float FINAL_VALUE = 360f;

	/**
	 * Creates a sun GameObject that moves in a circular path.
	 *
	 * @param windowDimensions the size of the game window
	 * @param cycleLength      duration of one full orbit
	 * @return the animated sun GameObject
	 */
	public static GameObject create(Vector2 windowDimensions,
									float cycleLength) {
		Renderable circle = new OvalRenderable(Color.YELLOW);

		// Initial sun position (top center-ish)
		GameObject sun = new GameObject(
				new Vector2(windowDimensions.x() / TWO, windowDimensions.y() / FIVE),
				new Vector2(SUN_SIZE, SUN_SIZE), circle);
		Vector2 initialSunCenter = sun.getCenter();
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sun.setTag(TAG);

		// vec for Orbiting center in lower center of screen
		Vector2 vec = new Vector2(windowDimensions.x() / TWO, windowDimensions.mult((float) TWO / THREE).y());

		// Animate rotation around vec
		new Transition<Float>(
				sun,
				(Float angle) -> sun.setCenter
						(initialSunCenter.subtract(vec)
								.rotated(angle)
								.add(vec))
				, INITIAL_VALUE
				, FINAL_VALUE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				cycleLength,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);
		return sun;
	}


}
