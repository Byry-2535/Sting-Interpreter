import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Token {
    public enum Type {
        KEYWORD, IDENTIFIER, STRING, NUMBER, SYMBOL, COMMENT
    }

    public final Type type;
    public final String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + type + ": " + value + "]";
    }
}

class Lexer {
    private static final String TOKEN_REGEX =
        "#.*" +
        "|\\b(show|roi|if|elseif|else|loop|str|num|bln|stop)\\b" +
        "|[():,|]" +
        "|[=!<>]=?|\\+|-|\\*|/|%|\\^" +
        "|'[^']*'" +
        "|\\d+(\\.\\d+)?|\\w+";

    private final Pattern pattern = Pattern.compile(TOKEN_REGEX);

    public List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            String match = matcher.group();
            Token.Type type = classify(match);
            if (type != Token.Type.COMMENT) {
                tokens.add(new Token(type, match));
            }
        }
        return tokens;
    }

    private Token.Type classify(String token) {
        if (token.startsWith("#")) return Token.Type.COMMENT;
        if (token.matches("'[^']*'")) return Token.Type.STRING;
        if (token.matches("\\d+(\\.\\d+)?")) return Token.Type.NUMBER;
        if (token.matches("\\b(show|roi|if|elseif|else|loop|str|num|bln)\\b")) return Token.Type.KEYWORD;
        if (token.matches("[=!<>]=?|\\+|-|\\*|/|%|\\^|[():,|]")) return Token.Type.SYMBOL;
        return Token.Type.IDENTIFIER;
    }
}

abstract class Node {
    public abstract void execute(Context context);
}

class Context {
    public final Map<String, Object> variables = new HashMap<>();
    public final Scanner scanner = new Scanner(System.in);

    public Object get(String name) {
        return variables.get(name);
    }

    public void set(String name, Object value) {
        variables.put(name, value);
    }
}

class PrintNode extends Node {
    private final List<String> args;

    public PrintNode(List<String> args) {
        this.args = args;
    }

    @Override
    public void execute(Context context) {
        for (String arg : args) {
            if (context.variables.containsKey(arg)) {
                Object value = context.get(arg);
                if (value instanceof Double) {
                    double d = (Double) value;
                    if (d == Math.floor(d)) {
                        System.out.print((int) d);
                    } else {
                        System.out.print(d);
                    }
                } else {
                    System.out.print(value);
                }
            } else {
                if (arg.startsWith("'") && arg.endsWith("'")) {
                    System.out.print(arg.substring(1, arg.length() - 1));
                } else {
                    System.out.print(arg);
                }
            }
        }
        System.out.println();
    }
}

class InputNode extends Node {
    private final String varName;
    private final String prompt;
    private final String type;

    public InputNode(String varName, String prompt, String type) {
        this.varName = varName;
        this.prompt = prompt;
        this.type = type;
    }

    @Override
    public void execute(Context context) {
        System.out.print(prompt.replace("'", ""));
        String input = context.scanner.nextLine();
        Object value;

        switch (type) {
            case "num":
                try {
                    value = input.contains(".") ? Double.parseDouble(input) : Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    throw new InterpreterException("Invalid input for number variable '" + varName + "'");
                }
                break;
            case "bln":
                value = input.trim().equalsIgnoreCase("true");
                break;
            default:
                value = input;
        }
        context.set(varName, value);
    }
}

class AssignmentNode extends Node {
    private final String varName;
    private final String expression;
    private String declaredType;

    public AssignmentNode(String varName, String expression, String declaredType) {
        this.varName = varName;
        this.expression = expression;
        this.declaredType = declaredType;
    }

    @Override
    public void execute(Context context) {
        Object result;

        if ("str".equals(declaredType)) {
            result = ExpressionEvaluator.evaluateValue(expression, context);
            if (!(result instanceof String)) {
                result = result.toString();
            }
        } else if ("bln".equals(declaredType)) {
            if ("true".equalsIgnoreCase(expression.trim())) {
                result = true;
            } else if ("false".equalsIgnoreCase(expression.trim())) {
                result = false;
            } else {
                result = ExpressionEvaluator.evaluate(expression, context);
            }
        } else if ("num".equals(declaredType)) {
            Object evalResult;
            try {
                evalResult = ExpressionEvaluator.evaluateValue(expression, context);
            } catch (Exception e) {
                throw new InterpreterException("Invalid number assignment to '" + varName + "'");
            }
            if (!(evalResult instanceof Number)) {
                throw new InterpreterException("Type mismatch: expected a number for variable '" + varName + "'");
            }
            result = evalResult;
        } else {
            result = ExpressionEvaluator.evaluateValue(expression, context);
        }

        if (declaredType == null) {
            if (result instanceof String) {
                declaredType = "str";
            } else if (result instanceof Boolean) {
                declaredType = "bln";
            } else {
                declaredType = "num";
            }
        }

        Object existing = context.get(varName);
        if (existing != null) {
            if ("num".equals(declaredType) && !(existing instanceof Number)) {
                throw new InterpreterException("Type mismatch: '" + varName + "' was previously not a number");
            }
            if ("str".equals(declaredType) && !(existing instanceof String)) {
                throw new InterpreterException("Type mismatch: '" + varName + "' was previously not a string");
            }
            if ("bln".equals(declaredType) && !(existing instanceof Boolean)) {
                throw new InterpreterException("Type mismatch: '" + varName + "' was previously not a boolean");
            }
        }

        if ("num".equals(declaredType) && !(result instanceof Number)) {
            throw new InterpreterException("Type mismatch: expected num for '" + varName + "'");
        }
        if ("str".equals(declaredType) && !(result instanceof String)) {
            throw new InterpreterException("Type mismatch: expected str for '" + varName + "'");
        }
        if ("bln".equals(declaredType) && !(result instanceof Boolean)) {
            throw new InterpreterException("Type mismatch: expected bln for '" + varName + "'");
        }   
        context.set(varName, result);
    }
}

class IfNode extends Node {
    private final List<ConditionBlock> blocks;

    public IfNode(List<ConditionBlock> blocks) {
        this.blocks = blocks;
    }

    @Override
    public void execute(Context context) {
        for (ConditionBlock block : blocks) {
            boolean matched = block.condition == null || ExpressionEvaluator.evaluate(block.condition, context);
            if (matched) {
                for (Node node : block.body) {
                    node.execute(context);
                }
                return;
            }
        }
    }

    public static class ConditionBlock {
        public final String condition;
        public final List<Node> body;

        public ConditionBlock(String condition, List<Node> body) {
            this.condition = condition;
            this.body = body;
        }
    }
}

class LoopNode extends Node {
    private final String condition;
    private final List<Node> body;

    public LoopNode(String condition, List<Node> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void execute(Context context) {
        try {
            while (ExpressionEvaluator.evaluate(condition, context)) {
                for (Node node : body) {
                    node.execute(context);
                }
            }
        } catch (StopException e) {
            // Exit the loop immediately when stop is executed
        }
    }
}

class StopNode extends Node {
    @Override
    public void execute(Context context) {
        throw new StopException();
    }
}
class StopException extends RuntimeException {}

class Parser {
    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Node> parse() {
        List<Node> nodes = new ArrayList<>();
        while (position < tokens.size()) {
            Token token = peek();
            switch (token.value) {
                case "str":
                case "num":
                case "bln":
                    nodes.add(parseAssignmentWithInput());
                    continue;
                case "show":
                    nodes.add(parseShow());
                    continue;
                case "if":
                    nodes.add(parseIf());
                    continue;
                case "loop":
                    nodes.add(parseLoop());
                    continue;
                case "|":
                    position++;
                    continue;
                case "stop":
                    next();
                    if (peek().value.equals("|")) {
                        next();
                    }
                    nodes.add(new StopNode());
                    continue;
                default:
                    position++;
            }
        }
        return nodes;
    }

    private Node parseStatement() {
        if (peek().value.matches("str|num|bln")) return parseAssignmentWithInput();
        if (peek().type == Token.Type.IDENTIFIER && position + 1 < tokens.size()) {
            Token nextToken = tokens.get(position + 1);
            if (nextToken.value.equals("=") || nextToken.value.matches("\\+=|-=|\\*=|/=|%=|\\^=")) {
                return parseAssignment();
            }
        }
        switch (peek().value) {
            case "if": return parseIf();
            case "show": return parseShow();
            case "loop": return parseLoop();
            case "stop":
                next();
                if (peek().value.equals("|")) next();
                return new StopNode();
            default: position++; return null;
        }
    }

    private Node parseAssignmentWithInput() {
        Token typeToken = next();
        expect("(");
        Token nameToken = next();
        expect(")");
        expect("=");
        Token nextToken = peek();

        if (nextToken.value.equals("roi")) {
            next();
            expect("(");

            StringBuilder promptBuilder = new StringBuilder();
            while (!peek().value.equals(")")) {
                promptBuilder.append(next().value);
                if (!peek().value.equals(")")) promptBuilder.append(" ");
            }
            expect(")");
            if (peek().value.equals("|")) {
                next();
            }

            String prompt = promptBuilder.toString().trim();
            return new InputNode(nameToken.value, prompt, typeToken.value);
        } else {
            StringBuilder expr = new StringBuilder();
            while (position < tokens.size() && !peek().value.equals("|") &&
                !peek().value.matches("show|roi|if|elseif|else|loop|str|num|bln")) {
                expr.append(next().value).append(" ");
            }
            if (peek().value.equals("|")) next();
            String expression = expr.toString().trim();
            return new AssignmentNode(nameToken.value, expression, typeToken.value);
        }
    }

    private Node parseAssignment() {
        Token varToken = next();
        Token opToken = next();

        StringBuilder expr = new StringBuilder();
        while (position < tokens.size() && !peek().value.equals("|") &&
            !peek().value.matches("show|roi|if|elseif|else|loop|str|num|bln")) {
            Token token = next();
            expr.append(token.value).append(" ");
        }
        if (peek().value.equals("|")) next();

        String expression = expr.toString().trim();
        return new AssignmentNode(varToken.value, expression, /* default or detected type */ null);
    }

    private Node parseShow() {
        position++;
        expect("(");
        List<String> args = new ArrayList<>();
        while (!peek().value.equals(")")) {
            Token token = next();
            if (!token.value.equals(",")) {
                args.add(token.value);
            }
        }
        expect(")");
        if (peek().value.equals("|")) next();
        return new PrintNode(args);
    }

    private Token next() {
        if (position >= tokens.size()) {
            return new Token(Token.Type.SYMBOL, "EOF");
        }
        return tokens.get(position++);
    }

    private Token peek() {
        if (position >= tokens.size()) {
            return new Token(Token.Type.SYMBOL, "EOF");
        }
        return tokens.get(position);
    }

    private void expect(String symbol) {
        Token token = next();
        if (!token.value.equals(symbol)) {
            throw new InterpreterException("Expected '" + symbol + "' but got '" + token.value + "'");
        }
    }

    private Node parseIf() {
        List<IfNode.ConditionBlock> blocks = new ArrayList<>();
        boolean first = true;

        while (position < tokens.size()) {
            String keyword = peek().value;

            if (keyword.equals("if") && first) {
                next();
                first = false;
            } else if (keyword.equals("elseif")) {
                next();
            } else if (keyword.equals("else")) {
                next();
                expect(":");

                List<Node> elseBody = new ArrayList<>();
                while (position < tokens.size()
                        && !peek().value.equals("|")
                        && !peek().value.equals("elseif")
                        && !peek().value.equals("else")
                        && !peek().value.equals("if")) {
                    Node stmt = parseStatement();
                    if (stmt != null) elseBody.add(stmt);
                }

                if (peek().value.equals("|")) next();
                blocks.add(new IfNode.ConditionBlock(null, elseBody));
                break;
            } else {
                break;
            }

            expect("(");
            StringBuilder conditionBuilder = new StringBuilder();
            int parenCount = 1;
            while (parenCount > 0 && position < tokens.size()) {
                Token token = next();
                if (token.value.equals("(")) parenCount++;
                else if (token.value.equals(")")) parenCount--;
                if (parenCount > 0) {
                    conditionBuilder.append(token.value).append(" ");
                }
            }
            String condition = conditionBuilder.toString().trim();
            expect(":");

            List<Node> body = new ArrayList<>();
            while (position < tokens.size()
                    && !peek().value.equals("|")
                    && !peek().value.equals("elseif")
                    && !peek().value.equals("else")
                    && !peek().value.equals("if")) {
                Node stmt = parseStatement();
                if (stmt != null) body.add(stmt);
            }

            if (peek().value.equals("|")) next();
            blocks.add(new IfNode.ConditionBlock(condition, body));
        }
        return new IfNode(blocks);
    }

    private Node parseLoop() {
        next();
        expect("(");
        StringBuilder conditionBuilder = new StringBuilder();
        while (!peek().value.equals(")")) {
            conditionBuilder.append(next().value).append(" ");
        }
        expect(")");
        expect(":");

        List<Node> body = new ArrayList<>();
        while (true) {
            if (position >= tokens.size()) break;
            if (peek().value.equals("|")) {
                next();
                if (position < tokens.size() && peek().value.matches("show|str|num|bln|if|elseif|else|loop|roi")) {
                    break;
                }
            }
            Node stmt = parseStatement();
            if (stmt == null) break;
            body.add(stmt);
        }
        return new LoopNode(conditionBuilder.toString().trim(), body);
    }
}

class ExpressionEvaluator {
    public static List<String> tokenizeExpression(String expr) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("'[^']*'|\\d+\\.\\d+|\\d+|[a-zA-Z_][a-zA-Z0-9_]*|[()!]|&&|\\|\\||[<>]=?|==|!=|[+\\-*/%^]").matcher(expr);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    public static List<String> toPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        boolean expectUnary = true;
        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("^", 5);
        precedence.put("u-", 4);
        precedence.put("!", 4);
        precedence.put("*", 3);
        precedence.put("/", 3);
        precedence.put("%", 3);
        precedence.put("+", 2);
        precedence.put("-", 2);
        precedence.put(">", 1);
        precedence.put("<", 1);
        precedence.put(">=", 1);
        precedence.put("<=", 1);
        precedence.put("==", 1);
        precedence.put("!=", 1);
        precedence.put("&&", 0);
        precedence.put("||", -1);

        for (String token : tokens) {
            if (token.matches("\\d+\\.\\d+|\\d+|[a-zA-Z_][a-zA-Z0-9_]*|'[^']*'")) {
                output.add(token);
                expectUnary = false;
            } else if (token.equals("(")) {
                operators.push(token);
                expectUnary = true;
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty()) operators.pop();
                expectUnary = false;
            } else {
                if (token.equals("-") && expectUnary) {
                    token = "u-";
                }
                while (!operators.isEmpty() && precedence.containsKey(operators.peek()) &&
                       precedence.get(token) <= precedence.get(operators.peek())) {
                    output.add(operators.pop());
                }
                operators.push(token);
                expectUnary = true;
            }
        }
        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        return output;
    }

    public static Object evaluatePostfix(List<String> postfix, Context context) {
        Stack<Object> stack = new Stack<>();

        for (String token : postfix) {
            if (token.matches("\\d+\\.\\d+|\\d+")) {
                stack.push(Double.parseDouble(token));
            } else if (token.startsWith("'") && token.endsWith("'")) {
                stack.push(token.substring(1, token.length() - 1));
            } else if ("true".equalsIgnoreCase(token)) {
                stack.push(true);
            } else if ("false".equalsIgnoreCase(token)) {
                stack.push(false);
            } else if (context.variables.containsKey(token)) {
                stack.push(context.get(token));
            } else if (token.equals("!")) {
                Object val = stack.pop();
                if (val instanceof Boolean) {
                    stack.push(!(Boolean) val);
                } else {
                    throw new InterpreterException("Invalid operand for '!': " + val);
                }
            } else if (token.equals("&&") || token.equals("||")) {
                boolean b = (Boolean) stack.pop();
                boolean a = (Boolean) stack.pop();
                stack.push(token.equals("&&") ? a && b : a || b);
            } else if (token.matches("==|!=|>|<|>=|<=")) {
                Object b = stack.pop();
                Object a = stack.pop();
                stack.push(compare(a, b, token));
            } else if (token.equals("u-")) {
                double val = toDouble(stack.pop());
                stack.push(-val);
            } else if (token.equals("+")) {
                Object b = stack.pop();
                Object a = stack.pop();
                if (a instanceof String || b instanceof String) {
                    stack.push(a.toString() + b.toString());
                } else {
                    stack.push(toDouble(a) + toDouble(b));
                }
            } else if (token.equals("-")) {
                double b = toDouble(stack.pop());
                double a = toDouble(stack.pop());
                stack.push(a - b);
            } else if (token.equals("*")) {
                double b = toDouble(stack.pop());
                double a = toDouble(stack.pop());
                stack.push(a * b);
            } else if (token.equals("/")) {
                double b = toDouble(stack.pop());
                double a = toDouble(stack.pop());
                if (b == 0) {
                    throw new InterpreterException("Division by zero error");
                }
                stack.push(a / b);
            } else if (token.equals("%")) {
                double b = toDouble(stack.pop());
                double a = toDouble(stack.pop());
                stack.push(a % b);
            } else if (token.equals("^")) {
                double b = toDouble(stack.pop());
                double a = toDouble(stack.pop());
                stack.push(Math.pow(a, b));
            } else {
                throw new InterpreterException("Unexpected token in expression: " + token);
            }
        }
        return stack.size() == 1 ? stack.pop() : null;
    }

    private static double toDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else {
            throw new InterpreterException("Expected a number but got: " + obj);
        }
    }

    private static boolean compare(Object a, Object b, String op) {
        try {
            double x = toDouble(a);
            double y = toDouble(b);
            return switch (op) {
                case ">" -> x > y;
                case "<" -> x < y;
                case ">=" -> x >= y;
                case "<=" -> x <= y;
                case "==" -> x == y;
                case "!=" -> x != y;
                default -> false;
            };
        } catch (Exception e) {
            String sa = a.toString();
            String sb = b.toString();
            return switch (op) {
                case "==" -> sa.equals(sb);
                case "!=" -> !sa.equals(sb);
                default -> false;
            };
        }
    }

    public static boolean evaluate(String expr, Context context) {
        expr = expr.trim();
        try {
            List<String> tokens = tokenizeExpression(expr);
            List<String> postfix = toPostfix(tokens);
            Object result = evaluatePostfix(postfix, context);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            if (result instanceof Number) {
                return ((Number) result).doubleValue() != 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static Object evaluateValue(String expr, Context context) {
        List<String> tokens = tokenizeExpression(expr);
        List<String> postfix = toPostfix(tokens);
        return evaluatePostfix(postfix, context);
    }
}

class InterpreterException extends RuntimeException {
    public InterpreterException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {
        try {
            String code = Files.readString(Path.of("C:\\Byry\\Codes\\ProgLang Project\\sample.sting"));
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.tokenize(code);

            Parser parser = new Parser(tokens);
            List<Node> nodes = parser.parse();

            Context context = new Context();
            for (Node node : nodes) {
                node.execute(context);
            }
        } catch (InterpreterException e) {
            System.err.println("Interpreter error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}