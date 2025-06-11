package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.AvatarJumpObserver.AvatarJumpObserver;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Represents the main player character in the game, capable of moving, jumping,
 * and displaying different animations based on its state and user input.
 * The avatar's actions consume energy, which regenerates when idle.
 * @author Eliyahu Peretz & Rom Ilany
 */
public class Avatar extends GameObject {
	private static final int SIZE = 30;
	private static final int TEN = 10;
	private static final int ROUND = 16;
	private static final float HALF = 0.5f;
	private static final int HUNDRED = 100;
	private static final String TAG = "avatar";
	private static final String BLOCK_TAG = "block";
	private static final String IMAGE_PATH = "src/assets/run_0.png";
	private static final String ANIMATION_PIC1 = "src/assets/idle_0.png";
	private static final String ANIMATION_PIC2 = "src/assets/idle_1.png";
	private static final String ANIMATION_PIC3 = "src/assets/idle_2.png";
	private static final String ANIMATION_PIC4 = "src/assets/idle_3.png";
	private static final String ANIMATION_PIC5 = "src/assets/jump_0.png";
	private static final String ANIMATION_PIC6 = "src/assets/jump_1.png";
	private static final String ANIMATION_PIC7 = "src/assets/jump_2.png";
	private static final String ANIMATION_PIC8 = "src/assets/jump_3.png";
	private static final String ANIMATION_PIC9 = "src/assets/run_0.png";
	private static final String ANIMATION_PIC10 = "src/assets/run_1.png";
	private static final String ANIMATION_PIC11 = "src/assets/run_2.png";
	private static final String ANIMATION_PIC12 = "src/assets/run_3.png";
	private static final String ANIMATION_PIC13 = "src/assets/run_4.png";
	private static final String ANIMATION_PIC14 = "src/assets/run_5.png";
	private static final float VELOCITY_X = 310;
	private static final float VELOCITY_Y = -300;
	private static final float GRAVITY = 180;

	private final UserInputListener inputListener;
	private float energy;


	private enum Moves {IDLE, RUN, JUMP, UP}

	private Moves mode;
	private AnimationRenderable run;
	private AnimationRenderable jump;
	private AnimationRenderable idle;
	private ArrayList<AvatarJumpObserver> jumpObserver;

	/**
	 * Constructs a new Avatar instance.
	 *
	 * @param topLeftCorner The top-left corner position of the avatar in game coordinates.
	 * @param inputListener Listener for user input events (e.g., keyboard).
	 * @param imageReader   Utility to read images used for avatar animations.
	 */
	public Avatar(Vector2 topLeftCorner,
				  UserInputListener inputListener,
				  ImageReader imageReader) {
		super(new Vector2(topLeftCorner.x(), topLeftCorner.y() - (SIZE + ROUND)),
				new Vector2(SIZE, SIZE), imageReader.readImage(
				IMAGE_PATH, false));
		this.inputListener = inputListener;
		this.setTag(TAG);
		this.mode = Moves.IDLE;
		this.energy = HUNDRED;
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		transform().setAccelerationY(GRAVITY);
		Renderable[] clips = {imageReader.readImage(
				ANIMATION_PIC1, false), imageReader.readImage(
				ANIMATION_PIC2, false),
				imageReader.readImage(
						ANIMATION_PIC3, false),
				imageReader.readImage(
						ANIMATION_PIC4, false)};
		this.idle = new AnimationRenderable(clips, HALF);
		clips = new Renderable[]{imageReader.readImage(
				ANIMATION_PIC5, false), imageReader.readImage(
				ANIMATION_PIC6, false),
				imageReader.readImage(
						ANIMATION_PIC7, false),
				imageReader.readImage(
						ANIMATION_PIC8, false)};
		this.jump = new AnimationRenderable(clips, HALF);
		clips = new Renderable[]{imageReader.readImage(
				ANIMATION_PIC9, false), imageReader.readImage(
				ANIMATION_PIC10, false),
				imageReader.readImage(
						ANIMATION_PIC11, false),
				imageReader.readImage(
						ANIMATION_PIC12, false),
				imageReader.readImage(
						ANIMATION_PIC13, false),
				imageReader.readImage(
						ANIMATION_PIC14, false)};
		this.run = new AnimationRenderable(clips, HALF);
		this.jumpObserver = new ArrayList<>();
	}


	@Override
	/**
	 * Updates the avatar state every frame.
	 * Handles movement, jumping, energy consumption and regeneration,
	 * and updates the animation according to the current state and inputs.
	 *
	 * @param deltaTime Time elapsed since the last frame (in seconds).
	 */
	public void update(float deltaTime) {
		super.update(deltaTime);
		float xVel = 0;
		//this.mode = Moves.IDLE;
		boolean isIdle = true;
		boolean isNotIdle = false;
		if (this.getVelocity().y() != 0) {
			isIdle = false;
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)
		) {
			if (this.energy > 0) {
				xVel -= VELOCITY_X;
				if (this.getVelocity().y() == 0) {
					renderer().setRenderable(run);
					renderer().setIsFlippedHorizontally(true);////////
				} else {
					renderer().setRenderable(this.jump);
					renderer().setIsFlippedHorizontally(true);
				}
			}
			this.mode = Moves.RUN;
			isIdle = false;
			isNotIdle = true;
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
			if (this.energy > 0) {
				xVel += VELOCITY_X;

				if (this.getVelocity().y() == 0) {
					renderer().setRenderable(this.run);
					renderer().setIsFlippedHorizontally(false);////////
				} else {
					renderer().setRenderable(this.jump);
					renderer().setIsFlippedHorizontally(false);
				}
			}
			this.mode = Moves.RUN;
			isNotIdle = true;
			isIdle = false;

		}
		transform().setVelocityX(xVel);
		if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)
		) {
			if (this.energy > 0 && this.energy >= TEN && getVelocity().y() == 0) {
				transform().setVelocityY(VELOCITY_Y);
				renderer().setRenderable(this.jump);
				this.mode = Moves.JUMP;
				isNotIdle = true;
				this.updateAvatarJumpObserver();
			} else {
				this.mode = Moves.UP;
			}
			isIdle = false;

		}
		if (isIdle) {
			renderer().setRenderable(this.idle);
			this.mode = Moves.IDLE;
		}

		if (this.getVelocity().equals(Vector2.ZERO)) {
			renderer().setRenderable(this.idle);
		}
		if (this.getVelocity().y() != 0 && !isNotIdle) {
			return;
		}
		this.movesSetter();

	}

	/*
	 *switch between the energy modes
	 */
	private void movesSetter() {
		switch (this.mode) {
			case RUN:
				// code block
				if(this.getVelocity().x()!=0) {
					this.energy = Math.max(0, this.energy - HALF);
				}else{
					this.energy = Math.min(TEN * TEN, this.energy + 1);
				}
				break;
			case JUMP:
				this.energy = Math.max(0, this.energy - TEN);
				this.mode = Moves.UP;
				break;
			case IDLE:
				this.energy = Math.min(TEN * TEN, this.energy + 1);
				break;

		}

	}

	@Override
	/**
	 * Handles collision with other game objects.
	 * Specifically, stops vertical velocity when colliding with blocks to simulate landing.
	 *
	 * @param other     The other game object involved in the collision.
	 * @param collision Information about the collision event.
	 */
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (other.getTag().equals(BLOCK_TAG)) {
			this.transform().setVelocityY(0);
		}
	}

	/**
	 * Sets the avatar's energy level.
	 *
	 * @param energy The new energy value to set.
	 */
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	/**
	 * Gets the current energy level of the avatar.
	 *
	 * @return The avatar's current energy as an integer.
	 */
	public int getEnergy() {
		return (int) this.energy;
	}

	/**
	 * Registers an observer that will be notified whenever the avatar jumps.
	 *
	 * @param jumpObserver The observer to register.
	 */
	public void registerObserverToLocation(AvatarJumpObserver jumpObserver) {
		this.jumpObserver.add(jumpObserver);
	}

	/*
	 * update the avatar observer when the avatar jump
	 */
	private void updateAvatarJumpObserver() {
		for (AvatarJumpObserver observer : this.jumpObserver) {
			observer.updateWhenJump();
		}
	}
}
