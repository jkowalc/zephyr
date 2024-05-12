package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.SyntaxException;
import me.jkowalc.zephyr.lexer.LexerInterface;
import me.jkowalc.zephyr.util.SimpleMap;
import me.jkowalc.zephyr.util.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final TokenReader reader;
    public Parser(LexerInterface lexer) throws LexicalException, IOException {
        this.reader = new TokenReader(lexer);
    }

    // program = {struct_definition | union_definition | function_definition };
    public Program parseProgram() {
        HashMap<String, TypeDefinition> types = new HashMap<>();
        HashMap<String, FunctionDefinition> functions = new HashMap<>();
        boolean isEnd = false;
        while(!isEnd) {
            StructDefinition structDefinition = parseStructDefinition();
            if (structDefinition != null) {
                types.put(structDefinition.getName(), structDefinition);
                continue;
            }
            UnionDefinition unionDefinition = parseUnionDefinition();
            if (unionDefinition != null) {
                types.put(unionDefinition.getName(), unionDefinition);
                continue;
            }
            FunctionDefinition functionDefinition = parseFunctionDefinition();
            if (functionDefinition != null) {
                functions.put(functionDefinition.getName(), functionDefinition);
                continue;
            }
            isEnd = true;
        }
        return new Program(functions, types);
    }

    // struct_definition = "struct", identifier, "{", struct_members, "}";
    private StructDefinition parseStructDefinition() {
        return null;
    }

    // union_definition = "union", identifier, "{", union_members, "}";
    private UnionDefinition parseUnionDefinition() {
        return null;
    }

    private FunctionDefinition parseFunctionDefinition() {
        return null;
    }

    // function_call = identifier, "(", [expression, {",", expression}], ")";
    private Expression parseFunctionCallOrVariableReference() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.IDENTIFIER) return null;
        IdentifierToken identifierToken = (IdentifierToken) reader.getToken();
        reader.next();
        if(reader.getType() != TokenType.OPEN_PARENTHESIS) {
            return new VariableReference(identifierToken.getStartPosition(), identifierToken.getEndPosition(), identifierToken.getValue());
        }
        List<Expression> parameters = new ArrayList<>();
        do {
            reader.next();
            parameters.add(parseExpression());
        } while(reader.getType() == TokenType.COMMA);
        if(reader.getType() != TokenType.CLOSE_PARENTHESIS) {
            throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
        }
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new FunctionCall(identifierToken.getStartPosition(), endPosition, identifierToken.getValue(), parameters);
    }

    // expression = and_term, {"or", and_term};
    public Expression parseExpression() throws LexicalException, IOException, SyntaxException {
        Expression left = parseAndTerm();
        while(reader.getType() == TokenType.OR) {
            reader.next();
            left = new OrExpression(left, parseAndTerm());
        }
        return left;
    }

    // and_term = comparison_expression, {"and", comparison_expression};
    private Expression parseAndTerm() throws LexicalException, IOException, SyntaxException {
        Expression left = parseComparisonExpression();
        while(reader.getType() == TokenType.AND) {
            reader.next();
            left = new AndExpression(left, parseComparisonExpression());
        }
        return left;
    }

    // comparison_expression = additive_term, [(">" | "<" | ">=" | "<=" | "==" | "!="), additive_term];
    private Expression parseComparisonExpression() throws LexicalException, IOException, SyntaxException {
        List<TokenType> comparisonOperators = List.of(TokenType.GREATER, TokenType.LESS, TokenType.GREATER_EQUALS, TokenType.LESS_EQUALS, TokenType.EQUALS, TokenType.NOT_EQUALS);
        Expression left = parseAdditiveTerm();
        if(comparisonOperators.contains(reader.getType())) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case GREATER -> new GreaterExpression(left, parseAdditiveTerm());
                case LESS -> new LessExpression(left, parseAdditiveTerm());
                case GREATER_EQUALS -> new GreaterEqualExpression(left, parseAdditiveTerm());
                case LESS_EQUALS -> new LessEqualExpression(left, parseAdditiveTerm());
                case EQUALS -> new EqualExpression(left, parseAdditiveTerm());
                case NOT_EQUALS -> new NotEqualExpression(left, parseAdditiveTerm());
                default -> throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
            };
        }
        if(comparisonOperators.contains(reader.getType())) {
            throw new SyntaxException("Multiple comparison expressions are not allowed", reader.getToken().getStartPosition());
        }
        return left;
    }

    // additive_term = term, {("+" | "-"), term};
    private Expression parseAdditiveTerm() throws LexicalException, IOException, SyntaxException {
        Expression left = parseTerm();
        while(reader.getType() == TokenType.PLUS || reader.getType() == TokenType.MINUS) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case PLUS -> new AddExpression(left, parseTerm());
                case MINUS -> new SubtractExpression(left, parseTerm());
                default -> throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
            };
        }
        return left;
    }

    // term = factor, {("*" | "/"), factor};
    private Expression parseTerm() throws LexicalException, IOException, SyntaxException {
        Expression left = parseFactor();
        while(reader.getType() == TokenType.MULTIPLY || reader.getType() == TokenType.DIVIDE) {
            TokenType operator = reader.getType();
            reader.next();
            left = switch (operator) {
                case MULTIPLY -> new MultiplyExpression(left, parseFactor());
                case DIVIDE -> new DivideExpression(left, parseFactor());
                default -> throw new SyntaxException("Unexpected token", reader.getToken().getStartPosition());
            };
        }
        return left;
    }
    // factor = ["-" | "!"], (dot_expression | factor);
    private Expression parseFactor() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() == TokenType.MINUS) {
            reader.next();
            return new NegationExpression(parseFactor());
        }
        if(reader.getType() == TokenType.NOT) {
            reader.next();
            return new NotExpression(parseFactor());
        }
        return parseDotExpression();
    }

    // dot_expression = elementary_expression, {".", elementary_expression};
    private Expression parseDotExpression() throws LexicalException, IOException, SyntaxException {
        Expression left = parseElementaryExpression();
        if(left instanceof StringLiteral || left instanceof IntegerLiteral || left instanceof FloatLiteral || left instanceof BooleanLiteral) {
            if(reader.getType() == TokenType.DOT) {
                throw new SyntaxException("Only structs can have fields", reader.getToken().getStartPosition());
            }
            return left;
        }
        while(reader.getType() == TokenType.DOT) {
            reader.next();
            Expression right = parseElementaryExpression();
            if(!(right instanceof VariableReference)) {
                throw new SyntaxException("Expected identifier", reader.getToken().getStartPosition());
            }
            left = new DotExpression(left, ((VariableReference) right).getName());
        }
        return left;
    }

    // elementary_expresssion = identifier
    //                       | "(", expression, ")"
    //                       | literal
    //                       | function_call;
    private Expression parseElementaryExpression() throws LexicalException, IOException, SyntaxException {
        Expression expression;
        if(reader.getType() == TokenType.OPEN_PARENTHESIS) {
            reader.next();
            expression = parseExpression();
            if (reader.getType() != TokenType.CLOSE_PARENTHESIS) {
                throw new SyntaxException("Expected ')'", reader.getToken().getStartPosition());
            }
            reader.next();
            return expression;
        }
        if((expression = parseFunctionCallOrVariableReference()) != null) {
            return expression;
        }
        if((expression = parseLiteral()) != null) {
            return expression;
        }
        throw new SyntaxException("Expected expression", reader.getToken().getStartPosition());
    }

    // literal = int_literal | float_literal | string_literal | bool_literal;
    private Literal parseLiteral() throws SyntaxException, LexicalException, IOException {
        StructLiteral structLiteral = parseStructLiteral();
        if(structLiteral != null) {
            return structLiteral;
        }
        switch(reader.getType()) {
            case INTEGER_LITERAL -> {
                IntegerLiteralToken token = (IntegerLiteralToken) reader.getToken();
                reader.next();
                return new IntegerLiteral(token.getStartPosition(), token.getEndPosition(), token.getValue());
            }
            case FLOAT_LITERAL -> {
                FloatLiteralToken token = (FloatLiteralToken) reader.getToken();
                reader.next();
                return new FloatLiteral(token.getStartPosition(), token.getEndPosition(), token.getValue());
            }
            case STRING_LITERAL -> {
                StringLiteralToken token = (StringLiteralToken) reader.getToken();
                reader.next();
                return new StringLiteral(token.getStartPosition(), token.getEndPosition(), token.getValue());
            }
            case TRUE -> {
                reader.next();
                return new BooleanLiteral(reader.getToken().getStartPosition(), reader.getToken().getEndPosition(), true);
            }
            case FALSE -> {
                reader.next();
                return new BooleanLiteral(reader.getToken().getStartPosition(), reader.getToken().getEndPosition(), false);
            }
            default -> {return null;}
        }
    }
    // struct_literal = "{", {struct_literal_member}, "}";
    private StructLiteral parseStructLiteral() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() != TokenType.OPEN_BRACE) {
            return null;
        }
        Map<String, Literal> fields = new SimpleMap<>();
        TextPosition startPosition = reader.getToken().getStartPosition();
        IdentifierToken identifierToken;
        do {
            reader.next();
            if(reader.getType() != TokenType.IDENTIFIER) {
                throw new SyntaxException("Expected identifier", reader.getToken().getStartPosition());
            }
            identifierToken = (IdentifierToken) reader.getToken();
            reader.next();
            if(reader.getType() != TokenType.COLON) {
                throw new SyntaxException("Expected ':'", reader.getToken().getStartPosition());
            }
            reader.next();
            fields.put(identifierToken.getValue(), parseLiteral());
        } while (reader.getType() == TokenType.COMMA);
        if(reader.getType() != TokenType.CLOSE_BRACE) {
            throw new SyntaxException("Expected '}'", reader.getToken().getStartPosition());
        }
        TextPosition endPosition = reader.getToken().getEndPosition();
        reader.next();
        return new StructLiteral(startPosition, endPosition, fields);
    }
}
