package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {
	private static final float TWO = 2;
	private static final float FIVE = 5;
	private static final String TAG = "sun";
	private static final int SUN_SIZE = 50;
	private static final float INITIAL_VALUE = 0.0f;
	private static final float FINAL_VALUE = 360f;

	public static GameObject create(Vector2 windowDimensions,
									float cycleLength) {
		Renderable circle = new OvalRenderable(Color.YELLOW);
		GameObject sun = new GameObject(
				new Vector2(windowDimensions.x() / TWO, windowDimensions.y() / FIVE),
				new Vector2(SUN_SIZE, SUN_SIZE), circle);
		Vector2 initialSunCenter = sun.getCenter();
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sun.setTag(TAG);
		Vector2 vec = new Vector2(windowDimensions.x() / TWO, windowDimensions.mult((float) 2 / 3).y());
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
