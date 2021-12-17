package datastructures.sequential;

import applications.arithmetic.ArithmeticExpression;
import applications.arithmetic.Brackets;
import applications.arithmetic.Operator;

import java.util.EmptyStackException;

/**
 * This is a stack class that implements LIFOQueue. This class is here
 * to help the classes DyckWord, PostfixEvaluator , ToPostfixConverter ,
 * and etc. This class could be used by a given element.
 * @author Raymond Lui
 */
public class SNodeStack implements LIFOQueue{
    private SNode head;
    private int size;

    /**
     * Constructs an empty SNodeStack
     */
    public SNodeStack(){
        head = null;
        size = 0;
    }

    /**
     * Returns and removes the top of the stack
     * @return the top of the stack
     * @throw EmptyStackException() if stack is empty
     */
    public Object pop() {
        if (head == null){
            throw new EmptyStackException();
        }
        Object temp = head.getData();
        head = head.getNext();
        size--;
        return temp;
    }

    /**
     * Adds a new node to the top of the stack
     * @param element the element to be pushed onto the top of this stack.
     */
    public void push(Object element) {
        if (element instanceof String){
            SNode newNode = new SNode(head, (String)element);
            newNode.setNext(head);
            head = newNode;
            size++;
        }
    }

    /**
     * Shows the top of the stack's data
     * @return the top of the stack's data
     * @throw EmptyStackException() if stack is empty
     */
    public Object peek() {
        if (head == null){
            throw new EmptyStackException();
        }
        return head.getData();
    }

    /**
     * Returns size of the stack
     * @return size of the stack
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the stack is empty or not
     * @return true if stack is empty and false if stack is not empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

}

