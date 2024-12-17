import java.util.*;
import java.util.regex.*;

public class InterpreterProject {
    
    // Map to hold variable names and their corresponding values
    private static Map<String, Integer> variables = new HashMap<>();
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter program code (end with an empty line):");
        
        // Read all lines of input
        StringBuilder program = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break;
            }
            program.append(line).append("\n");
        }

        // Tokenize the program and process each assignment
        String[] assignments = program.toString().split("\n");
        
        for (String assignment : assignments) {
            if (!processAssignment(assignment.trim())) {
                break;
            }
        }

        // Print out the values of all variables after execution
        System.out.println("\nFinal variable values:");
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    private static boolean processAssignment(String assignment) {
        // Check for the valid assignment format: <Identifier> = <Expression>;
        Pattern assignmentPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)\\s*;");
        Matcher matcher = assignmentPattern.matcher(assignment);

        if (!matcher.matches()) {
            System.out.println("Syntax error: Invalid assignment.");
            return false;
        }

        String identifier = matcher.group(1);
        String expression = matcher.group(2);

        // Evaluate the expression and assign the value to the variable
        if (expression.startsWith("0") && expression.length() > 1 ) {
        	System.out.println("Syntax error: Invalid expression");
        	return false;
        }
        
        
        Integer value = evaluateExpression(expression);
        if (value != null) {
            variables.put(identifier, value);
        }
        return true;
    }

    private static Integer evaluateExpression(String expression) {
        // Try to evaluate the expression
        try {
            return evaluateTerm(parseTerm(expression));
        } catch (Exception e) {
            System.out.println("Syntax error: Invalid expression.");
            return null;
        }
    }

    private static List<String> parseTerm(String expression) {
        // Split the expression into terms based on + and - operators
        List<String> terms = new ArrayList<>();
        int start = 0;
        int balance = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '+' || c == '-') {
                if (balance == 0) {
                    terms.add(expression.substring(start, i).trim());
                    start = i;
                }
            } else if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
        }
        terms.add(expression.substring(start).trim());
        return terms;
    }

    private static Integer evaluateTerm(List<String> terms) {
        // Evaluate each term and apply * operator on them
        int result = 1;
        boolean firstTerm = true;
        for (String term : terms) {
            if (firstTerm) {
                result = evaluateFactor(term);
                firstTerm = false;
            } else {
                result *= evaluateFactor(term);
            }
        }
        return result;
    }

    private static Integer evaluateFactor(String factor) {
        // Process a single factor, which could be an identifier, literal, or an expression in parentheses
        if (factor.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            // If it's an identifier, check if it's initialized
            if (!variables.containsKey(factor)) {
                System.out.println("Error: Uninitialized variable: " + factor);
                return null;
            }
            return variables.get(factor);
        } else if (factor.matches("-?\\d+")) {
            // If it's a literal
            return Integer.parseInt(factor);
        } else if (factor.startsWith("(") && factor.endsWith(")")) {
            // If it's a subexpression in parentheses
            return evaluateExpression(factor.substring(1, factor.length() - 1));
        }
        return null;
    }
}
