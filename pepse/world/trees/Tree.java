package pepse.world.trees;

import danogl.GameObject;

import java.util.List;

/**
 * Represents a generic tree structure in the game world.
 * Any class implementing this interface must provide logic for building a tree
 * The purpose of this interface is to allow multiple tree implementations
 * with different visual styles, behaviors, or building algorithms.
 */
public interface Tree {
	/**
	 * Builds and returns a list of GameObjects that together represent a tree.
	 *
	 * @return A list of GameObjects composing the tree.
	 */
	public List<GameObject> build();
}
