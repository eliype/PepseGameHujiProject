package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

public class Fruit extends GameObject {
	private static final String AVATAR_TAG = "avatar";
	private static final int WAITING_TIME = 30;
	private static final int ADD_ENERGY_AVATAR = 10;
	private static final int MAX_ENERGY = 100;



	/**
	 * Construct a new GameObject instance.
	 *
	 * @param topLeftCorner Position of the object, in window coordinates (pixels).
	 *                      Note that (0,0) is the top-left corner of the window.
	 * @param dimensions    Width and height in window coordinates.
	 * @param renderable    The renderable representing the object. Can be null, in which case
	 *                      the GameObject will not be rendered.
	 */
	public Fruit(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
		super(topLeftCorner, dimensions, renderable);
	}

	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (other.getTag().equals(AVATAR_TAG) &&
				this.renderer().getRenderable()!=null) {
			Avatar avatar = (Avatar)other;
			avatar.setEnergy(Math.min(
					avatar.getEnergy()+ADD_ENERGY_AVATAR,MAX_ENERGY));
			Renderable originalRenderable = this.renderer().getRenderable();
			this.renderer().setRenderable(null);
			new ScheduledTask(
					this,
					WAITING_TIME,
					false,
					() -> {
						this.renderer().setRenderable(originalRenderable);
					}
			);
		}

	}
}
