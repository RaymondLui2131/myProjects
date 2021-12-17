package hw3.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns the inOrderTraversal of a given BinarySearchTree
 * @author Ritwik Banerjee , Raymond Lui
 */
public class InOrderTraversal<E> extends Traversal<E> {
    /**
     * This method takes a given binarySearchTree and returns the inOrderTraversal of the binarySearchTree
     * @param tree A given binarySearchTree
     * @return the inOrderTraversal of the given tree
     */
    @Override
    public List<E> of(BinaryTree tree) {
        List<E> bst = new ArrayList<E>();
        helper(tree.root(), bst);
        return bst;
    }

    /**
     * This is a helper method that transverses the tree inorder
     * @param root The root of the tree
     * @param nodes A list of nodes of a tree
     */
    public void helper(BinaryTreeNode root, List<E> nodes){
        if (root != null) {
            helper(root.left(), nodes);
            nodes.add((E) root.element());
            helper(root.right(), nodes);
        }
    }
}
