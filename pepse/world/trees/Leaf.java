package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
/**
 * Represents a leaf block in the game world that animates by gently
 * swaying (rotating) and shrinking horizontally over a repeating cycle.
 * The leaf is implemented as a subclass of Block.
 */
public class Leaf extends Block {
	private static final float FINAL_WIDTH = 0.85f;
	private static final float INITIAL_ANGLE_VALUE = 0;
	private static final float FINAL_ANGLE_VALUE = 20;
	private static final float LEAF_CYCLE_LENGTH = 3f;
	private static final String LEAF_TAG = "leaf";

	/**
	 * Construct a new GameObject instance.
	 *
	 * @param topLeftCorner Position of the object, in window coordinates (pixels).
	 *                      Note that (0,0) is the top-left corner of the window.
	 * @param renderable    The renderable representing the object. Can be null, in which case
	 *                      the GameObject will not be rendered.
	 */
	public Leaf(Vector2 topLeftCorner, Renderable renderable,float time) {
		super(topLeftCorner, renderable);
		this.putInScheduledTask(time);
		this.setTag(LEAF_TAG);
	}
	/*
	 * Updates the leaf's animation by starting two transitions:
	 * 1. Rotates the leaf back and forth between INITIAL_ANGLE_VALUE and FINAL_ANGLE_VALUE degrees.
	 * 2. Shrinks and expands the leaf's width horizontally between full width and FINAL_WIDTH.
	 * Both transitions repeat continuously with the duration of LEAF_CYCLE_LENGTH .
	 */
	private void updateLeaf() {
		final float initialDimension = this.getDimensions().x();
		final float finalDimension = this.getDimensions().x() * FINAL_WIDTH;
		new Transition<Float>(
				this,
				(Float angle) -> {
					this.renderer().setRenderableAngle(angle);
				},
				INITIAL_ANGLE_VALUE,
				FINAL_ANGLE_VALUE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				LEAF_CYCLE_LENGTH,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null);


		new Transition<Float>(
				this,
				(Float dimensionsAsVector2) -> {
					this.setDimensions(new Vector2(dimensionsAsVector2, this.getDimensions().y()));
				},
				initialDimension,
				finalDimension,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				LEAF_CYCLE_LENGTH,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null);
	}
	/*
	 * Schedules a one-time task to start the leaf animation after the specified delay.
	 *
	 * @param time Delay in seconds before starting the animation.
	 */
	private void putInScheduledTask( float time) {
		new ScheduledTask(
				this,
				time,
				false,
				() -> this.updateLeaf());
	}
}
