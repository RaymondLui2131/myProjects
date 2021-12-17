package hw3.datastructures;

import hw3.products.Laptop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is the BinarySearchTree class which could add a node into the BinarySearchTree,
 * remove a node from the BinarySearchTree, and finds the path from the root to an element
 *
 * @author Ritwik Banerjee, Raymond Lui
 */
public class BinarySearchTree<T extends Comparable<T>> implements BinaryTree<T> {

    private BinaryTreeNode<T> root;
    private int               size;

    /**
     * !!DO NOT MODIFY THIS CODE!!
     * Consrtucts a binary search tree consisting of items from the given collection. Duplicates in the collection are
     * ignored, i.e., every item will be considered only once for the tree being constructed.
     *
     * @param collection the given collection
     */
    public BinarySearchTree(Collection<T> collection) {
        this();
        for (T t : collection)
            add(t);
    }

    /**
     * !!DO NOT MODIFY THIS CODE!!
     * Constructs an empty binary search tree.
     */
    private BinarySearchTree() {
        root = null;
        size = 0;
    }

    /**
     * This method adds a node into the BinarySearchTree given the data of the node
     * @param t The element the new node contains
     */
    @Override
    public void add(T t) {
        if (root == null){
            BinaryTreeNode<T> newNode = new BinaryTreeNode<T>(t);
            root = newNode;
        }
        else {
            BinaryTreeNode hold = root;
            boolean run = true;
            while (run){
                if (((Laptop)(hold.element())).compareTo((Laptop)t) == -1){
                    if (hold.right() == null){
                        BinaryTreeNode<T> newNode = new BinaryTreeNode<T>(t);
                        hold.setRight(newNode);
                        newNode.setParent(hold);
                        run = false;
                    }
                    else {
                        hold = hold.right();
                    }
                }
                else if (((Laptop)(hold.element())).compareTo((Laptop)t) == 1){
                    if (hold.left() == null){
                        BinaryTreeNode<T> newNode = new BinaryTreeNode<T>(t);
                        hold.setLeft(newNode);
                        newNode.setParent(hold);
                        run = false;
                    }
                    else {
                        hold = hold.left();
                    }
                }
                else {
                    run = false;
                }
            }
        }
    }
    /**
     * This methods checks if two trees are equal
     * @param tree A given tree
     * @return true if two trees are equal and false if two trees aren't equal
     */
    public boolean equals(BinarySearchTree tree) {
        return tree.root().equals(root);
    }

    /**
     * This method removes a node from the BinarySearchTree with a given element;
     * @param t The element the node we are trying to remove contains
     */
    @Override
    public void remove(T t){
        BinaryTreeNode newNode = findNode(root,(Laptop)t);
        if (newNode.left() == null && newNode.right() == null ){
            if (newNode.parent() == null)
                newNode = null;
            else if (newNode.parent().left() == newNode)
                newNode.parent().setLeft(null);
            else {
                newNode.parent().setRight(null);
            }
            newNode = null;
        }
        else if (newNode.left() != null && newNode.right() != null ){
            BinaryTreeNode successor = findSuccessor(newNode);
            BinaryTreeNode theMin = min(newNode.right());
            newNode.setElement(successor.element());
            theMin.parent().setLeft(null);
            theMin = null;
        }
        else {
            if (newNode.left() != null) {
                if (newNode.parent().right() == newNode)
                    newNode.parent().setRight(newNode.left());
                else
                    newNode.parent().setLeft(newNode.left());
                newNode.left().setParent(newNode.parent());
            }
            else if (newNode.right() != null) {
                if (newNode.parent().right() == newNode)
                    newNode.parent().setRight(newNode.right());
                else
                    newNode.parent().setLeft(newNode.right());
                newNode.right().setParent(newNode.parent());
            }
            newNode = null;
        }
    }

    /**
     * A helper method to find the a Node in the BinarySearchTree with the given element
     * @param root The root of the BinaryTree
     * @param lap The element the node contains
     * @return the node we are trying to find
     */
    private BinaryTreeNode findNode(BinaryTreeNode root,Laptop lap){
        if (root == null || root.element().equals(lap))
            return root;
        if (lap.compareTo((Laptop) root.element()) < 0)
            return findNode(root.left(), lap);
        else
            return findNode(root.right(), lap);
    }

    /**
     * A helper method to find the successor of a given node
     * @param root The node you are trying to find the successor for
     * @return the successor of a given node
     */
    private BinaryTreeNode findSuccessor(BinaryTreeNode root) {
        if (root.right() != null) return min(root.right());
        else {
            boolean run = true;
            while (run) {
                if (root == null)
                    return null;
                if (root.parent().right() != root)
                    return root.parent();
                root = root.parent();
            }
            return null;
        }
    }

    /**
     * A helper method to find the minimum of a subtree
     * @param root The subtree's parent
     * @return The minimum of a subtree
     */
    private BinaryTreeNode min(BinaryTreeNode root){
        boolean run = true;
        while (run){
            if (root.left() != null){
                root = root.left();
            }
            else
                run = false;
        }
        return root;
    }

    /**
     * Returns the search path that starts at the root node of the tree, and ends at the node containing the specified
     * item. If such a node exists in the tree, it is the last object in the returned list. Otherwise, this method will
     * still return the path corresponding to the search for this item, but append a <code>null</code> element at the
     * end of the list.
     *
     * @param t the specified item
     * @return the search path, with a node containing the specified item as the last object in the list if the item is
     * found in the tree, and the <code>null</code> node if item is not found in the tree.
     */
    @Override
    public List<BinaryTreeNode<T>> find(T t) {
        ArrayList<T> path = new ArrayList<T>();
        BinaryTreeNode item = findNode(root,(Laptop)t);
        boolean run = true;
        if (item == null)
            return (List)path;
        path.add((T) item.element());
        while(run) {
            if (item.parent() != null) {
                path.add((T) item.parent().element());
                item = item.parent();
            }
            else {
                run = false;
            }
        }
        Collections.reverse(path);
        return (List)path;
    }

    /**
     * !!DO NOT MODIFY THIS CODE!!
     */
    @Override
    public void print() {
        root.print();
    }

    /**
     * !!DO NOT MODIFY THIS CODE!!
     *
     * @return the root node of this tree
     */
    @Override
    public BinaryTreeNode<T> root() {
        return root;
    }

    /**
     * !!DO NOT MODIFY THIS CODE!!
     *
     * @return the size, i.e., the number of nodes in this tree
     */
    @Override
    public int size() {
        return size;
    }
}
