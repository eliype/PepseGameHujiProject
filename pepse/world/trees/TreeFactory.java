package pepse.world.trees;

/**
 * A factory class responsible for creating Tree objects in the game world.
 *
 * @author Eliyahu Peretz & Rom Ilany
 */
public class TreeFactory {
	/**
	 * Constructs and returns a Tree object at a given X coordinate and ground height.
	 *
	 * @param mySeed          the seed used for generating consistent tree behavior
	 * @param x               the x-coordinate at which to place the tree
	 * @param groundAtHeightX the height of the ground at position x
	 * @return a Tree object configured for the given parameters
	 */
	public Tree buildTree(long mySeed, int x, float groundAtHeightX) {
		return new TreeByDensity(mySeed, x, groundAtHeightX);
	}
}
