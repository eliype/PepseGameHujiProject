package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * class responsible for creating a forest full of trees.
 * creating the root and the plants e.g.
 *
 *
 */
public class Flora {


	private static final int ROOT_WIDTH = 30;
	private static final int ROOT_HEIGHT = 200;
	private static final int ROOT_PLANT_RANDOM = 10;
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
	private static final long MY_SEED = 10;
	private Function<Float, Float> getHeight;


	/**
	 * Constructor for Flora.
	 *
	 * @param getHeight a function that returns the terrain height at a given x-coordinate
	 */
	public Flora(Function<Float, Float> getHeight) {

		this.getHeight = getHeight;
	}

	/**
	 * Creates flora objects randomly within a horizontal range.
	 *
	 * @param minX the starting x-coordinate (inclusive)
	 * @param maxX the ending x-coordinate (exclusive)
	 * @return a list of generated plant GameObjects
	 */
	public List<GameObject> createInRange(int minX, int maxX) {
		Random rand = new Random(Objects.hash(MY_SEED)); // Seeded for reproducibility
		List<GameObject> forest = new ArrayList<>();

		for (int i = minX; i < maxX - ROOT_WIDTH; i = i + ROOT_WIDTH) {
			if (rand.nextInt(ROOT_PLANT_RANDOM) == 0) {
				this.buildRoot(i, forest); // Add root at position i
			}
		}
		return forest;
	}
	/**
	 * Builds a tree root GameObject at the specified x position and adds it to the forest list.
	 * The height of the root is randomized.
	 * Also initiates the creation of leaves around the root.
	 *
	 * @param x      the x-coordinate for the root's top-left corner.
	 * @param forest the list of GameObjects to add the root (and later leaves) to.
	 */
	private void buildRoot(int x, List<GameObject> forest) {
		Random rand = new Random(Objects.hash(x, MY_SEED));
		int height = (ROOT_HEIGHT / TWO) + rand.nextInt(ROOT_HEIGHT / TWO);
		float yPosition = this.getHeight.apply((float) x) - height;
		Renderable rend = new RectangleRenderable(ROOT_COLOR);
		GameObject root = new GameObject(new Vector2(x
				, yPosition)
				, new Vector2(ROOT_WIDTH, height), rend);
		root.physics().preventIntersectionsFromDirection(Vector2.ZERO);
		root.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		root.setTag(ROOT_TAG);
		forest.add(root);
		this.buildLeafs(root, forest, x);

	}
	/**
	 * Builds leaves (and sometimes fruits) around the given root.
	 * Leaves are distributed in a grid-like pattern around the root with some randomness.
	 *
	 * @param root   the root GameObject to build leaves around.
	 * @param forest the list of GameObjects to add the leaves and fruits to.
	 * @param x      the x-coordinate used as a seed for random generation.
	 */
	private void buildLeafs(GameObject root, List<GameObject> forest, int x) {
		Random rand = new Random(Objects.hash(x, MY_SEED));
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

					forest.add(leaf);

				} else {
					this.addFruit(new Vector2(i, j), root, forest);
				}
			}
		}
	}

	/**
	 * Adds a fruit GameObject at the given vector position, if conditions meet.
	 * Fruits appear with some randomness and only within the bounds of the root.
	 *
	 * @param vec    the Vector2 representing the candidate position for the fruit.
	 * @param root   the root GameObject to check if the fruit is within bounds.
	 * @param forest the list of GameObjects to add the fruit to if created.
	 */
	private void addFruit(Vector2 vec, GameObject root, List<GameObject> forest) {
		GameObject fruit;
		Random rand = new Random(Objects.hash(vec.x(), vec.y(), MY_SEED));
		if (rand.nextInt(TEN) <= FRUITS_DENSITY
				&& this.inRoot(vec.x(), root)) {
			Color[] fruitColor = {Color.red, Color.ORANGE};
			fruit = new Fruit(new Vector2(vec.y()
					, vec.x())
					, new Vector2(LEAF_SIZE, LEAF_SIZE),
					new OvalRenderable(fruitColor[rand.nextInt(fruitColor.length)]));
			fruit.setTag(FRUIT_TAG);
			forest.add(fruit);
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


}


