package pepse.world.AvatarJumpObserver;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Cloud implements AvatarJumpObserver {
	private static final int SIZE_OF_BLOCK = 30;
	private static final int SIZE_OF_DROPS = 8;
	private static final Color BASE_CLOUD_COLOR =
			new Color(255, 255, 255);
	private static final int DROPS_DENSITY = 3;
	private static final int RANDOM_BOUND = 10;
	private static final float DROP_CYCLE = 3f;
	private static final float DROP_FINALE = 0;
	private static final float INITIAL_DROP = 1;

	private static final List<List<Integer>> CLOUD = List.of(
			List.of(0, 1, 1, 0, 0, 0),
			List.of(1, 1, 1, 0, 1, 0),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(0, 1, 1, 1, 0, 0),
			List.of(0, 0, 0, 0, 0, 0)

	);

	private List<List<GameObject>> drops;
	private Random rand;
	private BiConsumer<GameObject,Integer> removeGameObject;
	private BiConsumer<GameObject,Integer> addGameObject;
	public Cloud(BiConsumer<GameObject,Integer> removeGameObject,
	 BiConsumer<GameObject,Integer> addGameObject) {
		this.drops = new ArrayList<>();
		this.rand = new Random();
		this.addGameObject = addGameObject;
		this.removeGameObject = removeGameObject;
	}

	public List<Block> create(Vector2 topLeftCorner, float cycleLength) {
		List<Block> cloud = new ArrayList<>();
		float row = topLeftCorner.y();
		float col = topLeftCorner.x();
		Block cloudBlock;
		ArrayList<GameObject> saveRow = new ArrayList<>();
		for (List<Integer> r : CLOUD) {
			for (int i : r) {
				if (i == 1) {
					cloudBlock = new Block(new Vector2(col, row),
							Vector2.ZERO,
							new RectangleRenderable(ColorSupplier.approximateMonoColor(
									BASE_CLOUD_COLOR)));
					addCloudeBlock(cloudBlock, cycleLength);
					cloud.add(cloudBlock);
					saveRow.add(cloudBlock);
				}
				col = col + SIZE_OF_BLOCK;
			}
			this.drops.add(saveRow);
			saveRow = new ArrayList<>();
			col = topLeftCorner.x();
			row = row + SIZE_OF_BLOCK;
		}
		return cloud;
	}

	private void addCloudeBlock(Block cloudBlock, float cycleLength) {
		new Transition<Float>(
				cloudBlock, // the game object being changed
				(Float i) -> cloudBlock.setTopLeftCorner(new Vector2(cloudBlock.getTopLeftCorner().x() + 1, cloudBlock.getTopLeftCorner().y())), // the method to call
				0f,
				1000f,
				Transition.LINEAR_INTERPOLATOR_FLOAT,// use a cubic interpolator
				cycleLength, // transition fully over half a day
				Transition.TransitionType.TRANSITION_LOOP, ///////////////////
				null);
		cloudBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
	}


	@Override
	public void update() {
		for (List<GameObject> row : this.drops) {
			for (GameObject col:row){
				if(this.rand.nextInt(RANDOM_BOUND)<= DROPS_DENSITY){
					Drop drop = new Drop(col.getTopLeftCorner(),
							new Vector2(SIZE_OF_DROPS,SIZE_OF_DROPS)
					, new RectangleRenderable(Color.blue));
					this.addDrop(drop,Layer.BACKGROUND);
					this.addGameObject.accept(drop,Layer.BACKGROUND);
				}
			}
		}
	}
	private void addDrop(Drop drop,int layer){
		/*new Transition<Float>(
				drop, // the game object being changed
				(Float i) -> drop.setTopLeftCorner(new Vector2(drop.getTopLeftCorner().x() ,drop.getTopLeftCorner().y()+1)), // the method to call
				INITIAL,
				DROP_FINALE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,// use a cubic interpolator
				DROP_CYCLE, // transition fully over half a day
				Transition.TransitionType.TRANSITION_ONCE, ///////////////////
				null);*/
		new Transition<Float>(drop, drop.renderer()::setOpaqueness, INITIAL_DROP, DROP_FINALE
				, Transition.CUBIC_INTERPOLATOR_FLOAT, DROP_CYCLE, Transition.TransitionType.TRANSITION_ONCE,
				()->this.removeGameObject.accept(drop, layer));
	}


}
