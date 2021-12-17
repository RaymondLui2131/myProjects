package applications.arithmetic;

/**
 * @author Raymond Lui, Ritwik Banerjee
 *  This interface is for the PostfixEvaluator class.
 */
public interface Evaluator {
    /**
     * Given a string expression, this method evaluates the expression
     * @param expressionString
     * @return the evaluation of the expression
     */
    double evaluate(String expressionString);
    /**
     * Determines whether or not a string is a valid operand.
     *
     * @param s the given string
     * @return <code>true</code> if the given string is a valid operand, and <code>false</code> otherwise
     */
    boolean isOperand(String s);
}
