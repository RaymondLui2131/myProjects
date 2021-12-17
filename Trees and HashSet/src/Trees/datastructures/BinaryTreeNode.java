package hw3.datastructures;

import java.util.Arrays;
import java.util.List;

/**
 * This is the BinaryTreeNode class to create nodes for the BinarySearchTree
 * and has a equals method to check if two nodes are the same and if two trees
 * are the same
 * @author Ritwik Banerjee , Raymond Lui
 */
public class BinaryTreeNode<E> {

    private E element;

    private BinaryTreeNode<E> left   = null;
    private BinaryTreeNode<E> right  = null;
    private BinaryTreeNode<E> parent = null;

    public BinaryTreeNode(E element) {
        this.element = element;
    }

    public E element() { return element; }

    public BinaryTreeNode<E> left() { return left; }

    public BinaryTreeNode<E> right() { return right; }

    public BinaryTreeNode<E> parent() { return parent; }

    public void setElement(E element) { this.element = element; }

    public void setLeft(BinaryTreeNode<E> node) { this.left = node; }

    public void setRight(BinaryTreeNode<E> node) { this.right = node; }

    public void setParent(BinaryTreeNode<E> node) { this.parent = node; }

    /**
     * TODO: check equality between two nodes such that it extends to checking for equality between two trees rooted at these nodes.
     * This methods checks if two nodes are equals and checks if two trees rooted at the two nodes are equal
     * @param o The element of the node you are trying to check equality
     * @return if two trees rooted at the two nodes are equal and if the two nodes are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BinaryTreeNode )) return false;
        BinaryTreeNode<E> that = (BinaryTreeNode<E>) o;
        boolean elementEquality = (this.element.equals(that.element));
        boolean childrenEquality = (this.left.equals(that.left) && this.right.equals(that.right));
        return elementEquality && childrenEquality;
    }

    /**
     * !!DO NOT MODIFY THIS CODE!!
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = element.hashCode();
        result = 31 * result + (left != null ? left.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    /** !!DO NOT MODIFY THIS CODE!! */
    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "'-- " : "|-- ") + element.toString());
        List<BinaryTreeNode<E>> children = Arrays.asList(left, right);
        for (int i = 0; i < children.size() - 1; i++) {
            if (children.get(i) != null)
                children.get(i).print(prefix + (isTail ? "    " : "|   "), false);
        }
        if (children.size() > 0 && children.get(children.size() - 1) != null) {
            children.get(children.size() - 1).print(prefix + (isTail ? "    " : "|    "), true);
        }
    }

    /** !!DO NOT MODIFY THIS CODE!! */
    public void print() {
        print("", true);
    }
}
