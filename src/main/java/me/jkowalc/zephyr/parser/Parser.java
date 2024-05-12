package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.domain.node.expression.literal.BooleanLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.FloatLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.IntegerLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.StringLiteral;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.SyntaxException;
import me.jkowalc.zephyr.lexer.LexerInterface;

import java.io.IOException;
import java.util.HashMap;
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
    // expression = and_term, {"or", and_term};
    public Expression parseExpression() throws LexicalException, IOException, SyntaxException {
        return parseAndTerm();
    }

    // and_term = comparison_expression, {"and", comparison_expression};
    private Expression parseAndTerm() throws LexicalException, IOException, SyntaxException {
        return parseComparisonExpression();
    }

    // comparison_expression = additive_term, [(">" | "<" | ">=" | "<=" | "==" | "!="), additive_term];
    private Expression parseComparisonExpression() throws LexicalException, IOException, SyntaxException {
        return parseAdditiveTerm();
    }

    // additive_term = term, {("+" | "-"), term};
    private Expression parseAdditiveTerm() throws LexicalException, IOException, SyntaxException {
        return parseTerm();
    }

    // term = factor, {("*" | "/"), factor};
    private Expression parseTerm() throws LexicalException, IOException, SyntaxException {
        return parseFactor();
    }
    // factor = {"-" | "!"}, (dot_expression | literal);
    private Expression parseFactor() throws LexicalException, IOException, SyntaxException {
        if(reader.getType() == TokenType.MINUS) {
            reader.next();
            return new NegationExpression(parseFactor());
        }
        if(reader.getType() == TokenType.NOT) {
            reader.next();
            return new NotExpression(parseFactor());
        }
        return parseLiteral();
    }
    // literal = int_literal | float_literal | string_literal | bool_literal;
    private Expression parseLiteral() throws SyntaxException, LexicalException, IOException {
        switch(reader.getType()) {
            case INTEGER_LITERAL -> {
                IntegerLiteralToken token = (IntegerLiteralToken) reader.getToken();
                reader.next();
                return new IntegerLiteral(token.getValue());
            }
            case FLOAT_LITERAL -> {
                FloatLiteralToken token = (FloatLiteralToken) reader.getToken();
                reader.next();
                return new FloatLiteral(token.getValue());
            }
            case STRING_LITERAL -> {
                StringLiteralToken token = (StringLiteralToken) reader.getToken();
                reader.next();
                return new StringLiteral(token.getValue());
            }
            case TRUE -> {
                reader.next();
                return new BooleanLiteral(true);
            }
            case FALSE -> {
                reader.next();
                return new BooleanLiteral(false);
            }
            default -> throw new SyntaxException("Unexpected token: " + reader.getToken().getType(), reader.getToken().getStartPosition());
        }
    }
}
