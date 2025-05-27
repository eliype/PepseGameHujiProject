package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Block;
import pepse.world.Terrain;


import java.util.List;

public class PepseGameManager extends GameManager {

	//message for rom
	private WindowController windowController;

	/**
	 * constructor
	 * @param windowTitle string with window title
	 * @param windowDimensions the window dimensions
	 */
	public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
		super(windowTitle, windowDimensions);
	}

	/**
	 *
	 * @param imageReader Contains a single method: readImage, which reads an image from disk.
	 *                 See its documentation for help.
	 * @param soundReader Contains a single method: readSound, which reads a wav file from
	 *                    disk. See its documentation for help.
	 * @param inputListener Contains a single method: isKeyPressed, which returns whether
	 *                      a given key is currently pressed by the user or not. See its
	 *                      documentation.
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
		setGround();

	}

	// adding list of block representing a ground, to the game
	private void setGround() {

		List<Block> groundList =
				new Terrain(windowController.getWindowDimensions(),10).createInRange(0,
						(int) windowController.getWindowDimensions().x());

		//adding each block
		for(Block ground : groundList){
			gameObjects().addGameObject(ground,Layer.STATIC_OBJECTS);
		}

	}


	public static void main(String[] args) {

		new PepseGameManager("pepse",new Vector2(1000,900)).run();

	}


}
