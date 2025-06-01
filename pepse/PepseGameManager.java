package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.AvatarJumpObserver.Cloud;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;


import java.util.List;
import java.util.function.BiConsumer;

public class PepseGameManager extends GameManager {
	private static final float dayCycle = 30;
	private static final int AVATER_X_PLACE = 50;
	private static final int ENERGY_SIZE = 30;
	private static final String BLOCK_TAG = "block";
	private static final int CLOUD_HEIGHT = 10;
	private WindowController windowController;
	private static final int SCREEN_HEIGHT = 900;
	private static final int SCREEN_WIDTH = 1000;
	private static final String LEAF_TAG = "leaf";

	/**
	 * constructor
	 *
	 * @param windowTitle      string with window title
	 * @param windowDimensions the window dimensions
	 */
	public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
		super(windowTitle, windowDimensions);
	}

	/**
	 * @param imageReader      Contains a single method: readImage, which reads an image from disk.
	 *                         See its documentation for help.
	 * @param soundReader      Contains a single method: readSound, which reads a wav file from
	 *                         disk. See its documentation for help.
	 * @param inputListener    Contains a single method: isKeyPressed, which returns whether
	 *                         a given key is currently pressed by the user or not. See its
	 *                         documentation.
	 * @param windowController Contains an array of helpful, self explanatory methods
	 *                         concerning the window.
	 */
	@Override
	public void initializeGame(ImageReader imageReader,
							   SoundReader soundReader,
							   UserInputListener inputListener,
							   WindowController windowController) {
		super.initializeGame(imageReader, soundReader, inputListener, windowController);

		this.windowController = windowController;

		//set the sky background
		GameObject sky = Sky.create(windowController.getWindowDimensions());
		gameObjects().addGameObject(sky, Layer.BACKGROUND);

		//set the ground
		Terrain terrain = new Terrain(windowController.getWindowDimensions(), 10);
		setGround(terrain);

		// add Night
		gameObjects().addGameObject(Night.create(windowController.
				getWindowDimensions(), dayCycle), Layer.BACKGROUND);
		//add sun
		GameObject sun = Sun.create(windowController.
				getWindowDimensions(), dayCycle);
		gameObjects().addGameObject(SunHalo.create(sun), Layer.BACKGROUND);
		gameObjects().addGameObject(sun, Layer.BACKGROUND);

		//add avatar
		Avatar avatar = new Avatar(new Vector2(AVATER_X_PLACE,
				terrain.groundHeightAt(AVATER_X_PLACE)), inputListener, imageReader);
		gameObjects().addGameObject(avatar, Layer.DEFAULT);

		setCamera(new Camera(avatar, windowController.getWindowDimensions().mult(0.5f).subtract(avatar.getTopLeftCorner()),
				windowController.getWindowDimensions(),
				windowController.getWindowDimensions()));

		//add energy
		Energy energy = new Energy(Vector2.ZERO, new Vector2(ENERGY_SIZE, ENERGY_SIZE), new TextRenderable(
				Integer.toString(avatar.getEnergy())), avatar::getEnergy);
		gameObjects().addGameObject(energy);
		energy.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);


		//Building forest
		//TreesPlant treesPlant = new TreesPlant((GameObject obj,Integer l ) ->
		//		gameObjects().addGameObject(obj,l), terrain::groundHeightAt);
		//treesPlant.buildForest();
		Flora treesPlant = new Flora(terrain::groundHeightAt);
		this.addForestObjects(treesPlant.createInRange(0, SCREEN_WIDTH));

		//add cloud
		BiConsumer<GameObject, Integer> removeObject =
				(GameObject obj, Integer layer) ->
						gameObjects().removeGameObject(obj, layer);
		BiConsumer<GameObject, Integer> addObject =
				(GameObject obj, Integer layer) ->
						gameObjects().addGameObject(obj, layer);
		Cloud cloud = new Cloud(removeObject, addObject);
		avatar.registerObserverToLocation(cloud);
		this.addCloude(cloud);
	}

	// adding list of block representing a ground, to the game
	private void setGround(Terrain terrain) {
		List<Block> groundList = terrain.createInRange(0,
				(int) windowController.getWindowDimensions().x());
		//adding each block
		for (Block ground : groundList) {
			ground.setTag(BLOCK_TAG);
			gameObjects().addGameObject(ground, Layer.STATIC_OBJECTS);

		}

	}

	private void addForestObjects(List<GameObject> gameObjects) {
		for (GameObject obj : gameObjects) {
			if (obj.getTag().equals(LEAF_TAG)) {
				gameObjects().addGameObject(obj, Layer.BACKGROUND);

			} else {
				gameObjects().addGameObject(obj, Layer.STATIC_OBJECTS);

			}
		}
	}

	private void addCloude(Cloud cloud) {
		List<Block> blocks = cloud.create(new Vector2(0, CLOUD_HEIGHT), dayCycle);
		for (Block cloudPart : blocks) {
			gameObjects().addGameObject(cloudPart, Layer.BACKGROUND);
		}
	}


	public static void main(String[] args) {

		new PepseGameManager("pepse", new Vector2(SCREEN_WIDTH, SCREEN_HEIGHT)).run();
	}


}
