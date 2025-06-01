package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.Component;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {
	private static final int HALO_SIZE = 70;
	private static final int TEN = 0;
	private static final String TAG = "sun halo";
	private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

	public static GameObject create(GameObject sun) {
		Renderable circle = new OvalRenderable(HALO_COLOR);
		GameObject halo = new GameObject(
				Vector2.ZERO,
				new Vector2(HALO_SIZE, HALO_SIZE), circle);
		halo.setCenter(sun.getCenter());
		halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		halo.setTag(TAG);
		halo.addComponent((float deltaTime) -> halo.setCenter(sun.getCenter()));
		return halo;
	}


}
