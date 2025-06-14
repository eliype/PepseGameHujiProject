package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class representing night.
 * created by a black block which fade through a specific
 * time.
 *
 * @author Eliyahu & Rom
 */
public class Night {
	// Tag prefix used to identify night overlay GameObjects
	private static final String TAG = "night";
	// Static counter to differentiate multiple night objects,
	//ensuring unique tags for each instance created.
	private static int diff = 0;
	/* Initial opacity value (fully transparent) for the night overlay */
	private static final float INITIAL_VALUE = 0.0f;
	/* Constant 2, used for halving the cycle length in opacity transition */

	private static final float TWO = 2;
	/*
	 * The maximum opacity value ("midnight" opacity),
	 * defining how dark the overlay becomes at night peak.
	 */
	private static final Float MIDNIGHT_OPACITY = 0.5f;

	/**
	 * Creates and returns a new Night GameObject covering the entire screen.
	 * The opacity of the overlay will continuously transition between transparent and midnight opacity,
	 * creating a night-day fading effect.
	 *
	 * @param windowDimensions the dimensions of the window the overlay should cover.
	 * @param cycleLength      the duration  of a full opacity cycle .
	 * @return a GameObject representing the night overlay with the opacity transition applied.
	 */
	public static GameObject create(Vector2 windowDimensions,
									float cycleLength) {
		Renderable rectangle = new RectangleRenderable(Color.BLACK);
		diff++; // Used to differentiate multiple night objects
		GameObject night = new GameObject(Vector2.ZERO, windowDimensions, rectangle);

		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); // Overlay follows camera
		night.setTag(TAG + diff); // Unique tag for identification

		// Animate opaqueness between INITIAL_VALUE and MIDNIGHT_OPACITY
		new Transition<Float>(night, night.renderer()::setOpaqueness, INITIAL_VALUE, MIDNIGHT_OPACITY
				, Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength / TWO,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null);

		return night;
	}
}
