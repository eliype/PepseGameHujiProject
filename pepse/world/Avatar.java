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
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class Avatar extends GameObject {
	// The avatar's width and height in pixels.
	private static final int SIZE = 30;
	// A general-purpose constant used for
	// energy-related calculations.
	private static final int TEN = 10;
	// A general-purpose constant used
	// for energy-related calculations.
	private static final int ROUND = 16;
	//Used for animation durations and energy adjustments.
	private static final float HALF = 0.5f;
	// Represents the full energy level.
	private static final int HUNDRED = 100;
	// Used for input checks, like detecting
	// if both left/right are pressed.
	private static final int TWO = 2;
	// A tag identifying the avatar GameObject.
	private static final String TAG = "avatar";
	// Used to identify collision with blocks.
	private static final String BLOCK_TAG = "block";
	//animation
	private static final String IMAGE_PATH = "assets/run_0.png";
	private static final String ANIMATION_PIC1 = "assets/idle_0.png";
	private static final String ANIMATION_PIC2 = "assets/idle_1.png";
	private static final String ANIMATION_PIC3 = "assets/idle_2.png";
	private static final String ANIMATION_PIC4 = "assets/idle_3.png";
	private static final String ANIMATION_PIC5 = "assets/jump_0.png";
	private static final String ANIMATION_PIC6 = "assets/jump_1.png";
	private static final String ANIMATION_PIC7 = "assets/jump_2.png";
	private static final String ANIMATION_PIC8 = "assets/jump_3.png";
	private static final String ANIMATION_PIC9 = "assets/run_0.png";
	private static final String ANIMATION_PIC10 = "assets/run_1.png";
	private static final String ANIMATION_PIC11 = "assets/run_2.png";
	private static final String ANIMATION_PIC12 = "assets/run_3.png";
	private static final String ANIMATION_PIC13 = "assets/run_4.png";
	private static final String ANIMATION_PIC14 = "assets/run_5.png";
	//velocity and gravity constants
	private static final float VELOCITY_X = 310;
	private static final float VELOCITY_Y = -300;
	private static final float GRAVITY = 180;
	// Listens to keyboard input for controlling the avatar.
	private final UserInputListener inputListener;
	// Tracks the avatar's current energy
	// level, ranging from 0 to 100.
	private float energy;

	// An enum representing the current
	// state of the avatar.
	private enum Moves {IDLE, RUN, JUMP, UP}

	// Current movement mode/state of the avatar.
	private Moves mode;
	// Animation sequence used on the avatar.
	private AnimationRenderable run;
	private AnimationRenderable jump;
	private AnimationRenderable idle;
	// A list of observers that react when the avatar jumps.
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
				imageReader.readImage(ANIMATION_PIC8,false)};
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
		boolean isIdle = true;boolean isNotIdle = false;
		int bothRightLeft = 0;
		if (this.getVelocity().y() != 0) {
			isIdle = false;
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
			bothRightLeft++;
			if (this.energy > 0) {
				xVel -= VELOCITY_X;
				this.left();
			}
			this.mode = Moves.RUN;
			isIdle = false;
			isNotIdle = true;
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
			bothRightLeft++;
			if (this.energy > 0) {
				xVel += VELOCITY_X;
				this.right();
			}
			this.mode = Moves.RUN;
			isNotIdle = true;
			isIdle = false;
		}
		transform().setVelocityX(xVel);
		if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
			if (this.energy > 0 && this.energy >= TEN && getVelocity().y() == 0) {
				this.up();
				isNotIdle = true;
			} else {
				this.mode = Moves.UP;
			}
			isIdle = false;
		}
		this.otherSituationCheck(isIdle, isNotIdle, bothRightLeft);
	}

	/*
	 *do part of the update when we move up
	 */
	private void up() {
		transform().setVelocityY(VELOCITY_Y);
		renderer().setRenderable(this.jump);
		this.mode = Moves.JUMP;
		this.updateAvatarJumpObserver();
	}

	/*
	 *do part of the update when we move right
	 */
	private void right() {
		if (this.getVelocity().y() == 0) {
			renderer().setRenderable(this.run);
			renderer().setIsFlippedHorizontally(false);
		} else {
			renderer().setRenderable(this.jump);
			renderer().setIsFlippedHorizontally(false);
		}
	}

	/*
	 *do part of the update when we move left
	 */
	private void left() {
		if (this.getVelocity().y() == 0) {
			renderer().setRenderable(run);
			renderer().setIsFlippedHorizontally(true);
		} else {
			renderer().setRenderable(this.jump);
			renderer().setIsFlippedHorizontally(true);
		}
	}

	/*
	 *do part of the update after we move
	 */
	private void otherSituationCheck(boolean isIdle, boolean isNotIdle, int bothRightLeft) {
		if (isIdle) {
			renderer().setRenderable(this.idle);
			this.mode = Moves.IDLE;
		}

		if (this.getVelocity().equals(Vector2.ZERO)) {
			renderer().setRenderable(this.idle);
		}
		if (bothRightLeft == TWO && this.getVelocity().y() == 0) {
			this.mode = Moves.IDLE;
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
				this.energy = Math.max(0, this.energy - HALF);
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
