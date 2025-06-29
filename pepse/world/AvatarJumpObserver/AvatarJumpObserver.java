package pepse.world.AvatarJumpObserver;

/**
 * interface for all classes that interact with avatar jump
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public interface AvatarJumpObserver {
	/**
	 * Called to notify the observer that the avatar has jumped.
	 * Implementing classes should define the response to this event.
	 */
	public void updateWhenJump();
}
