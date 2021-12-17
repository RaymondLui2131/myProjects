package applications.arithmetic;

import datastructures.sequential.SNodeStack;

/**
 * This class converts an infix expression to a postfix expression for the PostfixEvaluator class
 * to evaluate the postfix expression. This class could be used by inputting an infix expression and
 * it would produce an postfix expression. This class also checks if a given String is an operand by
 * a given string and this class can find the next token of an expression by a given string expression
 * and a starting index.
 *
 * @author Raymond Lui
 */
public class ToPostfixConverter implements Converter {
    /**
     * Converts an arithmetic expression to a postfix expression
     * @param expression  the given arithmetic expression
     * @return the given arithmetic expression as a postfix expression
     */
    public String convert(ArithmeticExpression expression) {
        SNodeStack stack = new SNodeStack();
        String function = expression.getExpression();
        String postfix = "";
        int i = 0;
        while (i < function.length()){
            String token = nextToken(function,i);
            if (isOperand(token)){
                postfix += token+" ";
            }
            else if (Brackets.isLeftBracket(token)){
                stack.push(token);
            }
            else if (!Brackets.isRightBracket(token)){
                Operator incoming = Operator.of(token);
                if (!stack.isEmpty()) {
                    Operator topOfStack = Operator.of((String)(stack.peek()));
                    boolean vaild = true;
                    while (vaild) {
                        if (topOfStack.getRank() <= incoming.getRank()){
                            postfix += (String) stack.pop()+" ";
                            if (stack.isEmpty()) {
                                vaild = false;
                            }
                            else {
                                topOfStack = Operator.of((String)(stack.peek()));
                            }
                        }
                        else {
                            vaild = false;
                        }
                    }
                }
                stack.push(token);
            }
            else {
                while (!Brackets.isLeftBracket((String)stack.peek())){
                    postfix += stack.pop()+" ";
                }
                stack.pop();
            }
            i = function.indexOf(token,i) + token.length();
        }
        while (!stack.isEmpty()){
            postfix += stack.pop()+" ";
        }
        return postfix;
    }

    /**
     * Identifies the next token of an expression
     * @param s     a given expression
     * @param start an index to begin
     * @return the string that represents a single token
     */
    public String nextToken(String s, int start) {
        if (start >= s.length() || start < 0){
            return "";
        }
        TokenBuilder token = new TokenBuilder();
        int end = start;
        boolean run = true;
        while (run){
            if (s.length() == end){
                break;
            }
            if (isOperand(s.substring(start,end+1))){
                token.append(s.charAt(end));
                end += 1;
            }
            else {
                run = false;
            }
        }
        if (start == end){
            return s.substring(end,end+1);
        }
        return token.build();
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
