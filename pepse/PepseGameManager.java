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
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.AvatarJumpObserver.Cloud;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PepseGameManager extends GameManager {
	private static final float dayCycle = 30;
	private static final float BLOCK_SIZE = 30;
	private static final int AVATER_X_PLACE = 450;
	private static final int ENERGY_SIZE = 30;
	private static final String BLOCK_TAG = "block";
	private static final int CLOUD_HEIGHT = 10;
	private static final int SCREEN_HEIGHT = 900;
	private static final int SCREEN_WIDTH = 1000;
	private static final String LEAF_TAG = "leaf";
	private static final String ROOT_TAG = "root";
	private static final String FRUIT_TAG = "fruit";
	private UserInputListener inputListener;
	private WindowController windowController;
	private List<GameObject> removeObjects;
	private Vector2 leftGround;
	private Vector2 middleGround;
	private Vector2 rightGround;
	private Avatar avatar;

	/**
	 * constructor
	 *
	 * @param windowTitle      string with window title
	 * @param windowDimensions the window dimensions
	 */
	public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
		super(windowTitle, windowDimensions);
		this.removeObjects = new ArrayList<>();
		this.rightGround = null;
		this.leftGround = null;
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
		this.inputListener = inputListener;
		//set the sky background
		GameObject sky = Sky.create(windowController.getWindowDimensions());
		gameObjects().addGameObject(sky, Layer.BACKGROUND);

		//set the ground
		Terrain terrain = new Terrain(windowController.getWindowDimensions(), 10);
		List<Block> groundList = terrain.createInRange(0,
				(int) windowController.getWindowDimensions().x());
		setGround(groundList);
		this.addToRemoveObjectList(groundList);
		this.middleGround = new Vector2(0,
				(int) windowController.getWindowDimensions().x());

		// add Night
		gameObjects().addGameObject(Night.create(windowController.
				getWindowDimensions(), dayCycle), Layer.BACKGROUND);
		//add sun
		GameObject sun = Sun.create(windowController.
				getWindowDimensions(), dayCycle);
		gameObjects().addGameObject(SunHalo.create(sun), Layer.BACKGROUND);
		gameObjects().addGameObject(sun, Layer.BACKGROUND);

		//add avatar
		this.avatar = new Avatar(new Vector2(AVATER_X_PLACE,
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
		Flora treesPlant = new Flora(terrain::groundHeightAt);
		List<GameObject> forest = treesPlant.createInRange(0, SCREEN_WIDTH);
		this.addForestObjects(forest);
		this.addGameObjectToRemoveGameObjectList(forest);

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
	private void setGround(List<Block> groundList) {
		//List<Block> groundList = terrain.createInRange(0,
		//	(int) windowController.getWindowDimensions().x());
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

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)
		) {
			//	System.out.println("right");
			if (this.rightGround == null) {
				System.out.println("build");
				this.rightGround = new Vector2((int)(this.middleGround.y()),(int)(this.middleGround.y()+SCREEN_WIDTH));
				this.expandScreen((int)this.rightGround.x(),(int)this.rightGround.y());
			}

			//this.removeObjects(this.camera().getTopLeftCorner().x()
			//		, this.camera().getTopLeftCorner().x() + BLOCK_SIZE);

			//this.expandScreen((int) (this.camera().getTopLeftCorner().x() + SCREEN_WIDTH - BLOCK_SIZE)
			//		, (int) this.camera().getTopLeftCorner().x() + (int) SCREEN_WIDTH);
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
			//	System.out.println("left");

			//this.removeObjects(
			//		this.camera().getTopLeftCorner().x() + SCREEN_WIDTH - BLOCK_SIZE
			//		, this.camera().getTopLeftCorner().x() + SCREEN_WIDTH);
			//this.expandScreen((int) this.camera().getTopLeftCorner().x()
				//	, (int) this.camera().getTopLeftCorner().x() + (int) BLOCK_SIZE);
			if (this.leftGround == null) {
				System.out.println("build");

				this.leftGround = new Vector2((int)(this.middleGround.x()-SCREEN_WIDTH),(int)(this.middleGround.x()));
				this.expandScreen((int)this.leftGround.x(),(int)this.leftGround.y());
			}

		}
		if(this.avatar.getCenter().x()<this.middleGround.x()){
			if (this.rightGround!=null){
				System.out.println("remove right"+this.rightGround);
				this.removeObjects(this.rightGround);
			}
			this.rightGround = this.middleGround;
			this.middleGround = this.leftGround;
			this.leftGround = null;
		}
		if (this.middleGround.y()<this.avatar.getCenter().x()){
			if (this.leftGround!=null){
				System.out.println("remove left"+this.leftGround);
				this.removeObjects(this.leftGround);
			}
			this.leftGround = this.middleGround;
			this.middleGround = this.rightGround;
			this.rightGround = null;

		}
	}

	private void addToRemoveObjectList(List<Block> list) {
		for (Block obj : list) {
			this.removeObjects.add(obj);
		}
	}

	private void addGameObjectToRemoveGameObjectList(List<GameObject> list) {
		for (GameObject obj : list) {
			//System.out.println(obj.getTag());
			this.removeObjects.add(obj);
		}
	}

	private void removeObjects(Vector2 vec) {
		Boolean inRange;
		for (GameObject obj : this.removeObjects) {
			inRange = (vec.x() <= obj.getCenter().x()) && (obj.getCenter().x() <= vec.y());
			if (obj.getTag().equals(BLOCK_TAG) && inRange) {
				gameObjects().removeGameObject(obj, Layer.STATIC_OBJECTS);

			}
			if (obj.getTag().equals(LEAF_TAG) && inRange) {
				gameObjects().removeGameObject(obj, Layer.BACKGROUND);
			}
			if (obj.getTag().equals(ROOT_TAG) && inRange) {
				gameObjects().removeGameObject(obj, Layer.STATIC_OBJECTS);
			}
			if (obj.getTag().equals(FRUIT_TAG) && inRange) {
				gameObjects().removeGameObject(obj, Layer.STATIC_OBJECTS);
			}
		}
	}

	private void expandScreen(int minX, int maxX) {
		Terrain terrain = new Terrain(windowController.getWindowDimensions(), 10);
		List<Block> groundList = terrain.createInRange(minX, maxX);
		setGround(groundList);
		this.addToRemoveObjectList(groundList);
		Flora treesPlant = new Flora(terrain::groundHeightAt);
		List<GameObject> forest = treesPlant.createInRange(minX, maxX);
		this.addForestObjects(forest);
		this.addGameObjectToRemoveGameObjectList(forest);
	}



	public static void main(String[] args) {

		new PepseGameManager("pepse", new Vector2(SCREEN_WIDTH, SCREEN_HEIGHT)).run();
	}


}
