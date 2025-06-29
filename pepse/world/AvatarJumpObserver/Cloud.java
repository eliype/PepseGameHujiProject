package pepse.world.AvatarJumpObserver;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * class representing a cloud.
 * when avatar jump the cloud is raining
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class Cloud implements AvatarJumpObserver {
	/*
	 * Size of each cloud block in pixels.
	 */
	private static final int SIZE_OF_BLOCK = 30;
	/*
	 * Size of raindrops in pixels (width and height).
	 */
	private static final int SIZE_OF_DROPS = 8;
	/*
	 * Numeric constant used in animation duration calculations.
	 */
	private static final int TWENTY_FIVE = 25;
	/*
	 * Numeric constant used in animation and positioning calculations.
	 */
	private static final int SEVEN = 7;
	/*
	 * Base color of cloud blocks (white).
	 */
	private static final Color BASE_CLOUD_COLOR =
			new Color(255, 255, 255);
	/*
	 * Density of raindrops spawned when the
	 * cloud rains.
	 * Higher values increase the chance of
	 * raindrop creation.
	 */
	private static final int DROPS_DENSITY = 3;
	/*
	 * Upper bound for random number generation
	 * used to determine
	 * whether a raindrop will be created for
	 * a given cloud block.
	 */
	private static final int RANDOM_BOUND = 10;
	/*
	 * Duration of the raindrop fade-out animation in seconds.
	 */
	private static final float DROP_CYCLE = 3f;
	/*
	 * Final opacity of raindrops after fade-out
	 */
	private static final float DROP_FINALE = 0;
	/*
	 * Initial opacity of raindrops at creation
	 */
	private static final float INITIAL_DROP = 1;
	/*
	 * Width of the screen in pixels,
	 * used for cloud movement boundaries.
	 */
	private static final int SCREEN_WIDTH = 1000;
	// Pattern of blocks forming the cloud (1 = block present, 0 = empty)
	private static final List<List<Integer>> CLOUD = List.of(
			List.of(0, 1, 1, 0, 0, 0),
			List.of(1, 1, 1, 0, 1, 0),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(0, 1, 1, 1, 0, 0),
			List.of(0, 0, 0, 0, 0, 0)

	);

	private List<List<GameObject>> drops;// Tracks blocks for raindrop spawning
	private Random rand;
	private BiConsumer<GameObject, Integer> removeGameObject;
	private BiConsumer<GameObject, Integer> addGameObject;

	/**
	 * Constructs a Cloud object.
	 *
	 * @param removeGameObject a function to remove GameObjects from the game (used for drops)
	 * @param addGameObject    a function to add GameObjects to the game (used for drops)
	 */
	public Cloud(BiConsumer<GameObject, Integer> removeGameObject,
				 BiConsumer<GameObject, Integer> addGameObject) {
		this.drops = new ArrayList<>();
		this.rand = new Random();
		this.addGameObject = addGameObject;
		this.removeGameObject = removeGameObject;
	}

	/**
	 * Creates a cloud of blocks starting from the given top-left corner.
	 *
	 * @param topLeftCorner the starting position of the cloud
	 * @param cycleLength   the animation cycle length for each block
	 * @return list of all blocks in the created cloud
	 */
	public List<Block> create(Vector2 topLeftCorner, float cycleLength) {
		List<Block> cloud = new ArrayList<>();
		float row = topLeftCorner.y();
		float col = topLeftCorner.x();
		Block cloudBlock;
		ArrayList<GameObject> saveRow = new ArrayList<>();

		// Iterate through cloud pattern rows
		for (List<Integer> r : CLOUD) {
			for (int i : r) {
				if (i == 1) {
					// Create a block at current (col, row)
					cloudBlock = new Block(new Vector2(col, row),

							new RectangleRenderable(ColorSupplier.approximateMonoColor(
									BASE_CLOUD_COLOR)));
					addCloudeBlock(cloudBlock); // Apply animation/timing
					cloud.add(cloudBlock); // Add to return list
					saveRow.add(cloudBlock); // Save for drop tracking
				}
				col = col + SIZE_OF_BLOCK; // Move to next column
			}

			//move forward
			this.drops.add(saveRow);
			saveRow = new ArrayList<>();
			col = topLeftCorner.x();
			row = row + SIZE_OF_BLOCK;
		}
		return cloud;
	}

	//Adds animation and sets coordinate space for the given cloud block.
	private void addCloudeBlock(Block cloudBlock) {
		float start = cloudBlock.getTopLeftCorner().x() - Block.SIZE * SEVEN;
		new Transition<Float>(cloudBlock,
				cloudBlock.transform()::setTopLeftCornerX,
				start, SCREEN_WIDTH + Block.SIZE * SEVEN + start,
				Transition.LINEAR_INTERPOLATOR_FLOAT, (SCREEN_WIDTH + Block.SIZE) / TWENTY_FIVE,
				Transition.TransitionType.TRANSITION_LOOP,
				null);
		cloudBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
	}


	@Override
	/**
	 * Called when the avatar jumps.
	 * Randomly generates raindrop objects beneath cloud blocks.
	 * Drops are added to the background layer and fade out over time.
	 */
	public void updateWhenJump() {
		for (List<GameObject> row : this.drops) {
			for (GameObject col : row) {

				//creating the rain
				if (this.rand.nextInt(RANDOM_BOUND) <= DROPS_DENSITY) {
					Drop drop = new Drop(col.getTopLeftCorner(),
							new Vector2(SIZE_OF_DROPS, SIZE_OF_DROPS)
							, new RectangleRenderable(Color.blue));
					this.addDrop(drop, Layer.BACKGROUND);
					drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);////
					this.addGameObject.accept(drop, Layer.BACKGROUND);
				}
			}
		}
	}

	//creating transition for each drop
	private void addDrop(Drop drop, int layer) {
		new Transition<Float>(drop, drop.renderer()::setOpaqueness, INITIAL_DROP, DROP_FINALE
				, Transition.CUBIC_INTERPOLATOR_FLOAT, DROP_CYCLE, Transition.TransitionType.TRANSITION_ONCE,
				() -> this.removeGameObject.accept(drop, layer));
	}


}
