package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.TokenType;

import java.util.Map;

import static java.util.Map.entry;

public class OperatorMapper {
    private static final Map<Character, TokenType> singleCharOperators = Map.ofEntries(
            entry('=', TokenType.ASSIGNMENT),
            entry('!', TokenType.NOT),
            entry('+', TokenType.PLUS),
            entry('-', TokenType.MINUS),
            entry('*', TokenType.MULTIPLY),
            entry('/', TokenType.DIVIDE),
            entry('.', TokenType.DOT),
            entry(',', TokenType.COMMA),
            entry(';', TokenType.SEMICOLON),
            entry(':', TokenType.COLON),
            entry('(', TokenType.OPEN_PARENTHESIS),
            entry(')', TokenType.CLOSE_PARENTHESIS),
            entry('{', TokenType.OPEN_BRACE),
            entry('}', TokenType.CLOSE_BRACE),
            entry('>', TokenType.GREATER),
            entry('<', TokenType.LESS)
    );
    private static final Map<String, TokenType> doubleCharOperators = Map.ofEntries(
            entry("==", TokenType.EQUALS),
            entry("!=", TokenType.NOT_EQUALS),
            entry(">=", TokenType.GREATER_EQUALS),
            entry("<=", TokenType.LESS_EQUALS),
            entry("->", TokenType.ARROW)
    );
    public static TokenType mapSingleCharOperator(char operator) {
        return singleCharOperators.get(operator);
    }
    public static TokenType mapDoubleCharOperator(String operator) {
        return doubleCharOperators.get(operator);
    }
}
