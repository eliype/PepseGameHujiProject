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
 * @author Eliyahu Peretz & Rom Ilany
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
		TreeFactory treeFactory = new TreeFactory();
		for (int i = minX; i < maxX - ROOT_WIDTH; i = i + ROOT_WIDTH) {
			if (rand.nextInt(ROOT_PLANT_RANDOM) == 0) {
				//this.buildRoot(i, forest); // Add root at position i
				//treeFactory.buildTree(MY_SEED,i,this.getHeight.apply((float) i)).build();
				this.addToForestList(forest,
						treeFactory.buildTree(
								MY_SEED, i, this.getHeight.apply((float) i)).build());
			}
		}
		return forest;
	}

	/*
	copy the trees objects to the current GameObject list
	*/
	private void addToForestList(List<GameObject> forest, List<GameObject> tree) {
		for (GameObject gameObject : tree) {
			forest.add(gameObject);
		}
	}


}


