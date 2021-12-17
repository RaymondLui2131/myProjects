package applications.arithmetic;

import datastructures.sequential.SNodeStack;

/**
 *  This is class is here in order to evaluate a given PostFix expression
 *  This class could be used by giving it a PostFix expression. The class
 *  will then evaluate it.
 *
 *  @author Raymond Lui
 */
public class PostfixEvaluator implements Evaluator{
    /**
     * Given a postfix expression this method evaluates the expression
     * @param expressionString a given postfix expression
     * @return what the postfix expression equates to
     */
    public double evaluate(String expressionString) {
        SNodeStack stack = new SNodeStack();
        String function = expressionString;
        int start = 1;
        String[] array = function.split(" ");
        for (int i = 0; i < array.length ; i++){
            double eval = 0;
            if (isOperand(array[i])){
                stack.push(array[i]);
            }
            else {
                double pop1 = Double.parseDouble((String)stack.pop());
                double pop2 = Double.parseDouble((String)stack.pop());
                if (array[i].equals("+")){
                    eval = pop2 + pop1;
                }
                else if (array[i].equals("-")){
                    eval = pop2 - pop1;
                }
                else if (array[i].equals("*")){
                    eval = pop2 * pop1;
                }
                else if (array[i].equals("/")){
                    eval = pop2 / pop1;
                }
                stack.push(eval+"");
            }
        }
        return Double.parseDouble((String)stack.peek());
    }

    /**
     * Given a string checks if it's an operand
     * @param s a given token
     * @return true if the token is a operand and false if it isn't
     */
    public boolean isOperand(String s) {
        try {
            double function = Double.parseDouble(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
