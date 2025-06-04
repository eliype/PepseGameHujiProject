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

public class Flora {


	private static final int ROOT_WIDTH = 30;
	private static final int ROOT_HEIGHT = 200;
	private static final int SCREEN_LENGH = 1000;
	private static final int ROOT_PLANT_RANDOM = 10;
	private static final int LEAFS_DENSITY = 7;
	private static final int FRUITS_DENSITY = 2;
	private static final int TEN = 10;
	private static final int TWO = 2;
	//private static final int LEAF_SIZE = 15;
	//private static final int LEAF_BLOCK = 60;
	private static final int LEAF_SIZE = 30;
	private static final int LEAF_BLOCK = 60;
	private static final float INITIAL_ANGLE_VALUE = 0;
	private static final float FINAL_ANGLE_VALUE = 20;
	private static final float LEAF_CYCLE_LENGTH = 3f;
	private static final float FINAL_WIDTH = 0.85f;
	private static final Color ROOT_COLOR = new Color(100, 50, 20);
	private static final Color LEEFS_COLOR = new Color(50, 200, 30);
	private static final String ROOT_TAG = "root";
	private static final String FRUIT_TAG = "fruit";
	private static final String LEAF_TAG = "leaf";
	private static final long MY_SEED = 10;
	private Function<Float, Float> getHeight;

	public Flora(Function<Float, Float> getHeight) {
		this.getHeight = getHeight;
	}

	public List<GameObject> createInRange(int minX, int maxX) {
		Random rand = new Random(Objects.hash(MY_SEED));
		List<GameObject> forest = new ArrayList<>();
		for (int i = minX; i < maxX - ROOT_WIDTH; i = i + ROOT_WIDTH) {
			if (rand.nextInt(ROOT_PLANT_RANDOM) == 0) {
				this.buildRoot(i, forest);
			}
		}
		return forest;
	}

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


	private void buildLeafs(GameObject root, List<GameObject> forest, int x) {
		Random rand = new Random(Objects.hash(x, MY_SEED));
		Renderable rend = new RectangleRenderable(LEEFS_COLOR);
		GameObject leaf;
		float time = 0;
		int placeLeafStart = ((int) root.getTopLeftCorner().x() - (LEAF_BLOCK));
		for (int i = ((int) root.getTopLeftCorner().y()) - (ROOT_HEIGHT / TWO);
			 i < root.getCenter().y(); i = i + LEAF_SIZE) {
			//time = 0;
			for (int j = placeLeafStart;
				 j < placeLeafStart + LEAF_BLOCK + LEAF_BLOCK + ROOT_WIDTH; j = j + LEAF_SIZE) {
				if (rand.nextInt(TEN) < LEAFS_DENSITY) {
					leaf = new Block(new Vector2(j
							, i)
							, new Vector2(LEAF_SIZE, LEAF_SIZE), rend);
					leaf.setTag(LEAF_TAG);

					time = rand.nextFloat(0f, 1);
					this.putInScheduledTask(leaf, time);
					forest.add(leaf);


				} else {
					this.addFruit(new Vector2(i, j), root, forest);
				}
			}
		}
	}

	private void updateLeaf(GameObject leaf) {
		final float initialDimension = leaf.getDimensions().x();
		final float finalDimension = leaf.getDimensions().x() * FINAL_WIDTH;
		new Transition<Float>(
				leaf,
				(Float angle) -> {
					leaf.renderer().setRenderableAngle(angle);
				},
				INITIAL_ANGLE_VALUE,
				FINAL_ANGLE_VALUE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				LEAF_CYCLE_LENGTH,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null);


		new Transition<Float>(
				leaf,
				(Float dimensionsAsVector2) -> {
					leaf.setDimensions(new Vector2(dimensionsAsVector2, leaf.getDimensions().y()));
				},
				initialDimension,
				finalDimension,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				LEAF_CYCLE_LENGTH,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null);
	}

	public void putInScheduledTask(GameObject leaf, float time) {
		new ScheduledTask(
				leaf,
				time,
				false,
				() -> this.updateLeaf(leaf));
	}

	private void addFruit(Vector2 vec, GameObject root, List<GameObject> forest) {
		GameObject fruit;
		Random rand = new Random(Objects.hash(vec.x(),vec.y(), MY_SEED));
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

	private Boolean inRoot(float i, GameObject root) {
		return i < root.getTopLeftCorner().y();
	}


}


