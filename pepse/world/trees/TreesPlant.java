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
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TreesPlant {
	private static final int ROOT_WIDTH = 30;
	private static final int ROOT_HEIGHT = 200;
	private static final int SCREEN_LENGH = 1000;
	private static final int ROOT_PLANT_RANDOM = 10;
	private static final int LEAFS_DENSITY = 7;
	private static final int FRUITS_DENSITY = 1;
	private static final int TEN = 10;
	private static final int TWO = 2;
	//private static final int LEAF_SIZE = 15;
	//private static final int LEAF_BLOCK = 60;
	private static final int LEAF_SIZE = 30;
	private static final int LEAF_BLOCK = 60;
	private static final float INITIAL_ANGLE_VALUE = 0;
	private static final float FINAL_ANGLE_VALUE = 20;
	private static final float LEAF_CYCLE_LENGTH = 4f;
	private static final float FINAL_WIDTH = 0.85f;
	private static final Random rand = new Random();
	private static final Color ROOT_COLOR = new Color(100, 50, 20);
	private static final Color LEEFS_COLOR = new Color(50, 200, 30);
	private static final String ROOT_TAG = "root";
	private static final String FRUIT_TAG = "fruit";
	private BiConsumer<GameObject, Integer> add;
	private Function<Float, Float> getHeight;

	public TreesPlant(BiConsumer<GameObject, Integer> add, Function<Float, Float> getHeight) {
		this.add = add;
		this.getHeight = getHeight;
	}

	public void buildForest() {
		for (int i = 0; i < SCREEN_LENGH - ROOT_WIDTH; i = i + ROOT_WIDTH) {
			if (rand.nextInt(ROOT_PLANT_RANDOM) == 0) {
				this.buildRoot(i);
			}
		}
	}

	private void buildRoot(int x) {
		int height = (ROOT_HEIGHT / TWO) + rand.nextInt(ROOT_HEIGHT / TWO);
		float yPosition = this.getHeight.apply((float) x) - height;
		Renderable rend = new RectangleRenderable(ROOT_COLOR);
		GameObject root = new GameObject(new Vector2(x
				, yPosition)
				, new Vector2(ROOT_WIDTH, height), rend);
		this.setPhysics(root, Vector2.UP);
		this.setPhysics(root, Vector2.LEFT);
		this.setPhysics(root, Vector2.RIGHT);
		this.buildLeafs(root);

	}

	private void setPhysics(GameObject obj, Vector2 vec) {
		GameObject copy = new GameObject(obj.getTopLeftCorner(), obj.getDimensions(),
				obj.renderer().getRenderable());
		copy.setTag(ROOT_TAG);
		copy.physics().preventIntersectionsFromDirection(vec);
		copy.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		this.add.accept(copy, Layer.STATIC_OBJECTS);
	}

	private void buildLeafs(GameObject root) {

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

					time = rand.nextFloat(0f, 1);
					this.putInScheduledTask(leaf, time);
					this.add.accept(leaf, Layer.BACKGROUND);

				} else {
					this.addFruit(i, j, root);
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

	private void addFruit(int i, int j, GameObject root) {
		GameObject fruit;
		if (rand.nextInt(TEN) <= FRUITS_DENSITY
				&& this.inRoot(i, j, root)) {
			Color[] fruitColor = {Color.red, Color.ORANGE};
			fruit = new Fruit(new Vector2(j
					, i)
					, new Vector2(LEAF_SIZE, LEAF_SIZE),
					new OvalRenderable(fruitColor[rand.nextInt(fruitColor.length)]));
			fruit.setTag(FRUIT_TAG);
			this.add.accept(fruit, Layer.STATIC_OBJECTS);
		}
	}

	private Boolean inRoot(int i, int j, GameObject root) {
		/*return !((root.getTopLeftCorner().x()<=i &&
				i<=root.getTopLeftCorner().x()+ROOT_WIDTH)
		&& (root.getTopLeftCorner().y()<=j
				&& j<=root.getTopLeftCorner().y()+(ROOT_HEIGHT/TWO)));*/
		return i < root.getTopLeftCorner().y();
	}


}
