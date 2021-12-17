package hw3.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns the preOrderTraversal of a given BinarySearchTree
 * @author Ritwik Banerjee , Raymond Lui
 */
public class PreOrderTraversal<E> extends Traversal<E> {
    /**
     * This method takes a given binarySearchTree and returns the preOrderTraversal of the binarySearchTree
     * @param tree A given binarySearchTree
     * @return the preOrderTraversal of the given tree
     */
    @Override
    public List<E> of(BinaryTree tree) {
        List<E> bst = new ArrayList<E>();
        helper(tree.root(), bst);
        return bst;
    }

    /**
     * This is a helper method that transverses the tree preOrder
     * @param root The root of the tree
     * @param nodes A list of nodes of a tree
     */
    public void helper(BinaryTreeNode root, List<E> nodes){
        if (root != null) {
            nodes.add((E) root.element());
            helper(root.left(), nodes);
            helper(root.right(), nodes);
        }
    }
}
