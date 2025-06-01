package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Night {
	private static final String TAG = "night";
	private static int diff = 0;
	private static final float INITIAL_VALUE = 0.0f;

	private static final Float MIDNIGHT_OPACITY = 0.5f;

	public static GameObject create(Vector2 windowDimensions,
									float cycleLength) {
		Renderable rectangle = new RectangleRenderable(Color.BLACK);
		diff++;
		GameObject night = new GameObject(Vector2.ZERO, windowDimensions, rectangle);
		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		night.setTag(TAG + diff);
		new Transition<Float>(night, night.renderer()::setOpaqueness, INITIAL_VALUE, MIDNIGHT_OPACITY
				, Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength, Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null);

		return night;
	}
}
