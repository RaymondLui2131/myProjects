package applications.arithmetic;

import datastructures.sequential.SNodeStack;

/**
 * This class is here to check if the given string is a dyckWord
 * to be used in the ToPostfixConverter and PostfixEvaluator classes.
 * This class could be used by a given string and it will check if
 * it's a valid dyckWord.
 *
 * @author Ritwik Banerjee, Raymond Lui
 */
    public class DyckWord {

    private final String word;

    /**
     * Constructs a DyckWord if word is a DyckWord
     * @param word the word to be checked if it's a DyckWord
     * @throws IllegalArgumentException if the given expression is not a valid Dyck word
     */
    public DyckWord(String word) {
        if (isDyckWord(word))
            this.word = word;
        else
            throw new IllegalArgumentException(String.format("%s is not a valid Dyck word.", word));
    }

    /**
     * Checks if word is a DyckWord
     * @param word the word to be checked if it's a DyckWord
     * @return true if word is DyckWord and false if it isn't
     */
    private static boolean isDyckWord(String word) {
        SNodeStack stacks = new SNodeStack();
        for (int i = 0 ; i < word.length() ; i++){
            if (Brackets.isLeftBracket(word.charAt(i))){
                stacks.push(""+word.charAt(i));
            }
            if (Brackets.isRightBracket(word.charAt(i))){
                if(stacks.isEmpty()){
                    return false;
                }
                if (!Brackets.correspond(((String)stacks.peek()).charAt(0),word.charAt(i))){
                    return false;
                }
                stacks.pop();
            }
        }
        if (stacks.isEmpty()){
            return true;
        }
        else {
            return false;
        }
    }

    public String getWord() {
        return word;
    }
}