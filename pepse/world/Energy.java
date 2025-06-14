package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

/**
 * Represents an energy display UI element in the game.
 * The energy value is dynamically updated and shown
 * as a percentage on the screen.
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class Energy extends GameObject {
	// A suffix used to indicate percentage
	// in the energy display
	private static final String PRECENT = "%";
	//A callback function that returns
	// the current energy level of the avatar.
	// Used to dynamically update the displayed
	// energy percentage.
	private Supplier<Integer> energy;
	// The text renderable that displays
	// the energy percentage on screen.
	private TextRenderable renderable;

	/**
	 * Construct a new GameObject instance.
	 *
	 * @param topLeftCorner Position of the object, in window coordinates (pixels).
	 *                      Note that (0,0) is the top-left corner of the window.
	 * @param dimensions    Width and height in window coordinates.
	 * @param renderable    The renderable representing the object. Can be null, in which case
	 *                      the GameObject will not be rendered.
	 * @param energy        callback that return avatar energy
	 */
	public Energy(Vector2 topLeftCorner, Vector2 dimensions,
				  TextRenderable renderable, Supplier<Integer> energy) {
		super(topLeftCorner, dimensions, renderable);
		this.renderable = renderable;
		this.energy = energy;
	}

	@Override
	/**
	 * Updates the displayed energy percentage based on the current energy value.
	 *
	 * @param deltaTime Time passed since the last frame (in seconds).
	 */
	public void update(float deltaTime) {
		super.update(deltaTime);
		this.renderable.setString(Integer.toString(this.energy.get()) + PRECENT);
	}


}
