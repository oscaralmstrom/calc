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
        return 0; // result;
    }

    // ------  Evaluate RPN expression -------------------

    // TODO Eval methods

    public double evalPostfix(List<String> list) {
        ArrayDeque<String> stack = new ArrayDeque<>();
        for (String element : list) {
            stack.push(element);
            String s = stack.peek();
            if (s != null && isOperator(s)) {
                String operator = stack.pop();
                String a = stack.pop();
                String b = stack.pop();
                if (isNumber(a) && isNumber(b)) {
                    stack.push(String.valueOf(applyOperator(operator, Double.valueOf(a), Double.valueOf(b))));
                } else {
                    throw new RuntimeException(MISSING_OPERAND);
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
            if (readingParentheses) { //reading stuff in a parentheses
                if (symbol.equals("(")) {
                    parenthesesStack.push(symbol);
                    parenthesesList.add(symbol);
                } else if (symbol.equals(")")) {
                    if (parenthesesStack.isEmpty()) {
                        //The content in a parentheses is handled separately
                        List<String> parResult = infixToPostfix(parenthesesList);
                        result.addAll(parResult);
                        readingParentheses = false;
                    } else {
                        parenthesesStack.pop();
                        parenthesesList.add(symbol);
                    }
                } else {
                    parenthesesList.add(symbol);
                }
            } else {
                if (symbol.equals("(")) { //TODO throw exceptions
                    readingParentheses = true;
                } else if (isNumber(symbol)) { //if any of the chars is a digit, then the entire string is a number
                    result.add(symbol);
                } else {
                    //If the stack operator is of higher or equal value than the read operator, pop the stack operator to the result
//                    if (operators.peek() != null && getPrecedence(operators.peek()) >= getPrecedence(symbol)) {
                    //If symbol has associativity to the right (eg. if symbol is equal to "^"), the push symbol the the operator stack
                    if (operators.peek() != null && getAssociativity(operators.peek()) == Assoc.RIGHT && getAssociativity(symbol) == Assoc.RIGHT) {
                        operators.push(symbol);
                        continue;
                    } else { // 2-2^2*2 22^*2-
                        //If symbol has lower, or equal, precedence than the stack, the stack will pop till the new symbol is of higher precedence
                        while (operators.peek() != null && getPrecedence(operators.peek()) >= getPrecedence(symbol)) {
                            result.add(operators.pop());
                        }
                    }

//                    }
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

    private boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '.') {
                return false;
            }
        }
        return true;
    }

    private boolean isOperator(String s) {
        switch (s) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "^":
                return true;
            default:
                return false;
        }
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

    // TODO Methods to tokenize
    public List<String> tokenize(String s) {
        List<Character> chars = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                continue;
            }
            chars.add(s.charAt(i));
        }
        List<String> result = combineDigits(chars);
//        System.out.println(result.toString());
        return result;
    }

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
