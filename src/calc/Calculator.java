package calc;

import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;

/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        // TODO List<String> tokens = tokenize(expr);
        // TODO List<String> postfix = infix2Postfix(tokens);
        // TODO double result = evalPostfix(postfix);
        List<String> tokens = tokenize(expr);
        List<String> postfix = infixToPostfix(tokens);
        double result = evalPostfix(postfix);
        return result;
    }

    // ------  Evaluate RPN expression -------------------

    private double evalPostfix(List<String> list) {
        ArrayDeque<String> stack = new ArrayDeque<>();
        for (String element : list) {
            stack.push(element);
            String s = stack.peek();
            if (s != null && isOperator(s)) {
                if (stack.size() < 3) throw new IllegalArgumentException(MISSING_OPERAND);
                String operator = stack.pop();
                String a = stack.pop();
                String b = stack.pop();
                if (isNumber(a) && isNumber(b)) {
                    stack.push(String.valueOf(applyOperator(operator, Double.valueOf(a), Double.valueOf(b))));
                } else {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }
            }
        }
        return Double.parseDouble(stack.pop());
    }

    double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    public List<String> infixToPostfix(List<String> infix) {
        ArrayDeque<String> operators = new ArrayDeque<>(), parenthesesStack = new ArrayDeque<>();
        ArrayList<String> result = new ArrayList<>(), parenthesesList = new ArrayList<>();
        boolean readingParentheses = false;
        for (String symbol : infix) {
            if (readingParentheses) {
                readingParentheses = readParentheses(symbol, parenthesesStack, parenthesesList, result);
            } else {
                if (symbol.equals("(")) { //TODO throw exceptions
                    readingParentheses = true;
                } else if (isNumber(symbol)) { //if any of the chars is a digit, then the entire string is a number
                    result.add(symbol);
                } else {
                    //If the stack operator is of higher or equal value than the read operator, pop the stack operator to the result
                    if (!isOperatorInStackAndSymbolPow(symbol, operators)) {
                        //If symbol has lower, or equal, precedence than the stack, the stack will pop till the new symbol is of higher precedence
                        popHigherPrecedenceInStack(result, symbol, operators);
                    }
                    operators.push(symbol);
                }
            }
        }

        while (!operators.isEmpty()) {
            result.add(operators.pop());
        }
//        System.out.println(result);
        return result;
    }

    private boolean readParentheses(String s, ArrayDeque<String> parStack, List<String> parList, List<String> result) {
        switch (s) {
            case "(":
                parStack.push(s);
                parList.add(s);
                return true;
            case ")":
                if (parStack.isEmpty()) {
                    List<String> parResult = infixToPostfix(parList);
                    result.addAll(parResult);
                    return false;
                }
                parStack.pop();
                parList.add(s);
                return true;
            default:
                parList.add(s);
                return true;
        }
    }

    private boolean isNumber(String s) {
        if (s == null || s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '.') {
                return false;
            }
        }
        return true;
    }

    private boolean isOperator(String s) {
        if (s == null) return false;
        return OPERATORS.contains(s);
    }

    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    enum Assoc {
        LEFT,
        RIGHT
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    // ---------- Tokenize -----------------------

    public List<String> tokenize(String s) {
        List<String> tokens = new ArrayList<>();
        ArrayDeque<Character> digits = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                digits.push(s.charAt(i)); //pushes the digit to the digit stack, as it might be part of a number
            } else if (isOperator(String.valueOf(s.charAt(i))) || s.charAt(i) == '(' || s.charAt(i) == ')') {
                if (digits.isEmpty()) {
                    tokens.add(String.valueOf(s.charAt(i))); //simply add the value to the tokens
                } else {
                    //the digit stack isn't empty -> the digit stack values are reversed and merged into one string (a number)
                    tokens.add(reverseStackToString(digits));
                    digits.clear();
                    tokens.add(String.valueOf(s.charAt(i))); //adds the operator to the tokens
                }
            }
        }
        //Adds any remaining digits to the tokens, by merging them into one string (representing a number)
        if (!digits.isEmpty()) {
            tokens.add(reverseStackToString(digits));
        }
        return tokens;
    }

    private String reverseStackToString(ArrayDeque stack) {
        if (stack == null || stack.isEmpty()) throw new IllegalArgumentException("Invalid stack!");
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }
        return sb.reverse().toString();
    }

    /*
    private List<String> combineDigits(List<Character> chars) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.size(); i++) {
            if (Character.isDigit(chars.get(i))) {
                sb.append(chars.get(i));
            } else {
                if (!sb.toString().isEmpty()) {
                    result.add(sb.toString());
                }
                result.add(String.valueOf(chars.get(i)));
                sb = new StringBuilder();
            }
        }
        if (!sb.toString().isEmpty()) {
            result.add(sb.toString());
        }
        return result;
    }
    */
}
// TODO Methods
//    while there are tokens to be read:
//    read a token.
//    if the token is a number, then:
//    push it to the output queue.
//            if the token is a function then:
//    push it onto the operator stack
//    if the token is an operator, then:
//            while ((there is a function at the top of the operator stack)
//    or (there is an operator at the top of the operator stack with greater precedence)
//    or (the operator at the top of the operator stack has equal precedence and is left associative))
//    and (the operator at the top of the operator stack is not a left bracket):
//    pop operators from the operator stack onto the output queue.
//    push it onto the operator stack.
//            if the token is a left bracket (i.e. "("), then:
//    push it onto the operator stack.
//            if the token is a right bracket (i.e. ")"), then:
//            while the operator at the top of the operator stack is not a left bracket:
//    pop the operator from the operator stack onto the output queue.
//    pop the left bracket from the stack.
//    /* if the stack runs out without finding a left bracket, then there are mismatched parentheses. */
//if there are no more tokens to read:
//            while there are still operator tokens on the stack:
//    /* if the operator token on the top of the stack is a bracket, then there are mismatched parentheses. */
//    pop the operator from the operator stack onto the output queue.
//            exit.
