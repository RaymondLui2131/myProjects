package datastructures.sequential;

/**
 * This is a SNode Class. This class is here to help create
 * a LIFO Queue or a stack. This class can be used by a given
 * node and the node's data.
 * @author Raymond Lui
 */
public class SNode {
    private SNode next;
    private String data;

    /**
     * Constructs an empty SNode
     */
    public SNode(){
        next = null;
        data = null;
    }

    /**
     * Constructs an SNode with a given node and string
     * @param n the next SNode
     * @param d the SNode's data
     */
    public SNode(SNode n, String d){
        next = n;
        data = d;
    }

    /**
     * Gets the next SNode
     * @return the next SNode
     */
    public SNode getNext() {
        return next;
    }

    /**
     * Sets the next SNode
     * @param next the next SNode
     */
    public void setNext(SNode next) {
        this.next = next;
    }

    /**
     * Gets the current SNode's data
     * @return SNode's data
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the current SNode's data
     * @param data SNode's new data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Makes Snode's data a string
     * @return SNode's data
     */
    public String toString(){
        return data;
    }
}

