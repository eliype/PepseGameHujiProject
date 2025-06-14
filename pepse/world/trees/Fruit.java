package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

/**
 * Represents a fruit object in the game world that can be collected by the avatar.
 * When the avatar collides with the fruit, it increases the avatar's energy by a fixed amount.
 * After being collected, the fruit temporarily disappears and then reappears after a delay.
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class Fruit extends GameObject {
	// Tag to identify the avatar object in collisions
	private static final String AVATAR_TAG = "avatar";
	// Time  the fruit remains invisible
	// after being collected before reappearing
	private static final int WAITING_TIME = 30;
	// Amount of energy added to the avatar upon collecting the fruit
	private static final int ADD_ENERGY_AVATAR = 10;
	// Maximum energy the avatar can have
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
	/**
	 * Handles the collision event with another GameObject.
	 * If the other object is the avatar and the fruit is currently visible,
	 * increases the avatar's energy (up to a maximum), makes the fruit temporarily invisible,
	 * and schedules the fruit to reappear after a waiting period.
	 *
	 * @param other     The other GameObject involved in the collision.
	 * @param collision The collision information.
	 */
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (other.getTag().equals(AVATAR_TAG) &&
				this.renderer().getRenderable() != null) {
			Avatar avatar = (Avatar) other;
			avatar.setEnergy(Math.min(
					avatar.getEnergy() + ADD_ENERGY_AVATAR, MAX_ENERGY));
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
