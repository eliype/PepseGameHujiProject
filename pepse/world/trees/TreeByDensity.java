package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A tree implementation that builds trees based on randomized density of leaves and fruits.
 * This class generates a trunk (root), and populates the surrounding area with leaves and fruits
 * according to configurable densities.
 * This implementation allows for natural-looking tree diversity by using a seed and randomization.
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class TreeByDensity implements Tree {

	// The fixed width of the tree trunk (root) in pixels.
	private static final int ROOT_WIDTH = 30;

	// The maximum height of the tree trunk (root) in pixels.
	private static final int ROOT_HEIGHT = 200;

	// Probability threshold (out of 10) to generate a leaf at a given position.
	private static final int LEAFS_DENSITY = 7;

	// Probability threshold (out of 10) to generate a fruit at a given position.


	private static final int FRUITS_DENSITY = 2;

	// Constant value 10, used for probability and random threshold calculations.


	private static final int TEN = 10;

	//  Constant value 2, used for halving or other calculations.


	private static final int TWO = 2;
	// The size (width and height) of a leaf block in pixels.

	private static final int LEAF_SIZE = 30;

	//The horizontal span in pixels around the root where leaves and fruits can be placed.

	private static final int LEAF_BLOCK = 60;

	// The color used to render the tree trunk (brownish).

	private static final Color ROOT_COLOR = new Color(100, 50, 20);

	// The color used to render leaves (greenish).


	private static final Color LEEFS_COLOR = new Color(50, 200, 30);

	// Tag string assigned to the root GameObject for identification.

	private static final String ROOT_TAG = "root";

	// Tag string assigned to fruit GameObjects for identification.


	private static final String FRUIT_TAG = "fruit";

	// Tag string assigned to leaf GameObjects for identification.


	private static final String LEAF_TAG = "leaf";

	// Seed for random number generation to ensure deterministic/random but reproducible trees.

	private long mySeed;

	// List of all GameObjects composing the tree (root, leaves, fruits).
	private List<GameObject> tree;

	// The horizontal position (x-coordinate) where the tree will be placed in the world.

	private int place;

	// The vertical ground height (y-coordinate) at the tree's x position,
	//used to position the root correctly on the ground.
	private float groundAtHeightX;

	/**
	 * Constructs a new TreeByDensity object at a specified horizontal location,
	 * grounded at the given height, and using a seed for deterministic randomness.
	 *
	 * @param mySeed          A seed for random generation.
	 * @param x               The x-coordinate where the tree will be placed.
	 * @param groundAtHeightX The y-coordinate of the ground at position x.
	 */
	public TreeByDensity(long mySeed, int x, float groundAtHeightX) {
		this.mySeed = mySeed;
		this.tree = new ArrayList<>();
		this.place = x;
		this.groundAtHeightX = groundAtHeightX;
	}

	/*
	 * Builds a tree root GameObject at the specified x position and adds it to the forest list.
	 * The height of the root is randomized.
	 * Also initiates the creation of leaves around the root.
	 *
	 * @param x the x-coordinate for the root's top-left corner.
	 * @param groundAtHeightX Y-coordinate of the ground level at position x.
	 */
	private void buildRoot(int x, float groundAtHeightX) {
		Random rand = new Random(Objects.hash(x, this.mySeed));
		int height = (ROOT_HEIGHT / TWO) + rand.nextInt(ROOT_HEIGHT / TWO);
		float yPosition = groundAtHeightX - height;
		Renderable rend = new RectangleRenderable(ROOT_COLOR);
		GameObject root = new GameObject(new Vector2(x
				, yPosition)
				, new Vector2(ROOT_WIDTH, height), rend);
		root.physics().preventIntersectionsFromDirection(Vector2.ZERO);
		root.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		root.setTag(ROOT_TAG);
		this.tree.add(root);
		this.buildLeafs(root, x);

	}

	/*
	 * Builds leaves (and sometimes fruits) around the given root.
	 * Leaves are distributed in a grid-like pattern around the root with some randomness.
	 *
	 * @param root the root GameObject to build leaves around.
	 * @param x    the x-coordinate used as a seed for random generation.
	 */
	private void buildLeafs(GameObject root, int x) {
		Random rand = new Random(Objects.hash(x, this.mySeed));
		Renderable rend = new RectangleRenderable(LEEFS_COLOR);
		Leaf leaf;
		int placeLeafStart = ((int) root.getTopLeftCorner().x() - (LEAF_BLOCK));
		for (int i = ((int) root.getTopLeftCorner().y()) - (ROOT_HEIGHT / TWO);
			 i < root.getCenter().y(); i = i + LEAF_SIZE) {
			for (int j = placeLeafStart;
				 j < placeLeafStart + LEAF_BLOCK + LEAF_BLOCK + ROOT_WIDTH; j = j + LEAF_SIZE) {
				if (rand.nextInt(TEN) < LEAFS_DENSITY) {
					leaf = new Leaf(new Vector2(j
							, i)
							, rend, rand.nextFloat(0f, 1));
					leaf.setTag(LEAF_TAG);

					this.tree.add(leaf);

				} else {
					this.addFruit(new Vector2(i, j), root);
				}
			}
		}
	}

	/*
	 * Adds a fruit GameObject at the given vector position, if conditions meet.
	 * Fruits appear with some randomness and only within the bounds of the root.
	 *
	 * @param vec  the Vector2 representing the candidate position for the fruit.
	 * @param root the root GameObject to check if the fruit is within bounds.
	 */
	private void addFruit(Vector2 vec, GameObject root) {
		GameObject fruit;
		Random rand = new Random(Objects.hash(vec.x(), vec.y(), this.mySeed));
		if (rand.nextInt(TEN) <= FRUITS_DENSITY
				&& this.inRoot(vec.x(), root)) {
			Color[] fruitColor = {Color.red, Color.ORANGE};
			fruit = new Fruit(new Vector2(vec.y()
					, vec.x())
					, new Vector2(LEAF_SIZE, LEAF_SIZE),
					new OvalRenderable(fruitColor[rand.nextInt(fruitColor.length)]));
			fruit.setTag(FRUIT_TAG);
			this.tree.add(fruit);
		}
	}

	/*
	 * Checks whether the given vertical position is above the top of the root.
	 *
	 * @param i    the y-coordinate to check.
	 * @param root the root GameObject.
	 * @return true if the position I is above the top edge of the root, false otherwise.
	 */
	private Boolean inRoot(float i, GameObject root) {

		return i < root.getTopLeftCorner().y();
	}

	@Override
	/**
	 * Builds the full tree and returns the list of all associated GameObjects.
	 *
	 * @return list of GameObjects composing the tree
	 */
	public List<GameObject> build() {
		this.buildRoot(this.place, this.groundAtHeightX);
		return this.tree;
	}
}
