package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the terrain of the world,
 * including generating ground height and blocks.
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class Terrain {

	// color for ground block
	private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
	//private static final Color BASE_GROUND_COLOR = Color.BLACK;
	// amount of block beneath the top
	private static final int TERRAIN_DEPTH = 20;
	// Used to scale the Y value
	// in the noise function
	private static final int SEVEN = 7;
	// Used to calculate initial
	// ground height as 2/3 of window height.
	private static final int THREE = 3;
	// Used in the ratio 2/3 for
	// initial ground height.
	private static final int TWO = 2;
	//The base height of the ground at x=0
	private final float groundHeightAtX0;

	//parameters
	// The dimensions of the game window
	private final Vector2 windowDimensions;
	// A noise generator used to create
	// pseudo-random but smooth terrain variations.
	private final NoiseGenerator noiseGenerator;


	/**
	 * constructor.
	 * initialize all parameters
	 *
	 * @param windowDimensions the window dimensions
	 * @param seed             int for generating random numbers
	 */
	public Terrain(Vector2 windowDimensions, int seed) {

		this.windowDimensions = windowDimensions;
		this.groundHeightAtX0 = windowDimensions.mult((float) TWO / THREE).y();
		this.noiseGenerator = new NoiseGenerator(seed, (int) groundHeightAtX0);

	}

	/**
	 * Calculates the ground height at a given x-coordinate.
	 * <p>
	 * If x is not 0, it uses a noise generator to add a variation based on the noise
	 * function evaluated at (x, Block.SIZE * 7). This introduces fluent randomness to the height.
	 * If x is 0, it returns the base ground height without noise.
	 *
	 * @param x the x-coordinate to compute the ground height for
	 * @return the computed ground height at the given x-coordinate
	 */
	public float groundHeightAt(float x) {

		if (x != 0) {
			// Generate noise value at the given x and a fixed y-coordinate (Block.SIZE * 7)
			float noise = (float) this.noiseGenerator.noise(x, Block.SIZE * SEVEN);
			// Add the noise value to the base ground height and return it
			return groundHeightAtX0 + noise;
			//return groundHeightAtX0-noise;
		}
		return this.groundHeightAtX0;
	}


	/**
	 * Creates a list of ground blocks within a specified horizontal range.
	 * <p>
	 * For each horizontal position in the specified range [minX, maxX), it calculates
	 * the ground height using {@code groundHeightAt}, and then creates a column of
	 * blocks downwards from that height to a certain depth.
	 *
	 * @param minX the minimum x-coordinate (inclusive) of the range
	 * @param maxX the maximum x-coordinate (exclusive) of the range
	 * @return a list of objects representing the ground in the specified range
	 */
	public List<Block> createInRange(int minX, int maxX) {
		// Adjust minX and maxX to align with block boundaries
		int newMinX = (int) Math.floor((double) minX / Block.SIZE) * Block.SIZE;
		int newMaxX = (int) Math.ceil((double) maxX / Block.SIZE) * Block.SIZE;

		//RectangleRenderable groundRenderable =
		//		new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
		RectangleRenderable groundRenderable;
		List<Block> groundList = new ArrayList<>();

		// Loop over x positions in the adjusted range, step by block size
		for (int i = newMinX; i < newMaxX; i += Block.SIZE) {
			groundRenderable =
					new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
			int blockHeight = (int) (Math.floor(groundHeightAt(i) / Block.SIZE) * Block.SIZE);

			// Create a column of blocks downwards from blockHeight to TERRAIN_DEPTH
			for (int j = 0; j < TERRAIN_DEPTH; j += 1) {
				groundRenderable =
						new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
				groundList.add(new Block(new Vector2(i, j * Block.SIZE + blockHeight),
						groundRenderable));

			}

		}

		return groundList;

	}

}
