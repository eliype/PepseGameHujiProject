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
	private static final int ROOT_WIDTH = 30;
	private static final int ROOT_HEIGHT = 200;
	private static final int LEAFS_DENSITY = 7;
	private static final int FRUITS_DENSITY = 2;
	private static final int TEN = 10;
	private static final int TWO = 2;
	private static final int LEAF_SIZE = 30;
	private static final int LEAF_BLOCK = 60;
	private static final Color ROOT_COLOR = new Color(100, 50, 20);
	private static final Color LEEFS_COLOR = new Color(50, 200, 30);
	private static final String ROOT_TAG = "root";
	private static final String FRUIT_TAG = "fruit";
	private static final String LEAF_TAG = "leaf";

	private long mySeed;
	private List<GameObject> tree;
	private int place;
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

	/**
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

	/**
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

	/**
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

	/**
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
