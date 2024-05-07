package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.TokenType;

import java.util.Map;

import static java.util.Map.entry;

public class KeywordMapper {
    private static final Map<String, TokenType> keywords = Map.ofEntries(
            entry("struct", TokenType.STRUCT),
            entry("mut", TokenType.MUT),
            entry("ref", TokenType.REF),
            entry("mref", TokenType.MREF),
            entry("return", TokenType.RETURN),
            entry("while", TokenType.WHILE),
            entry("if", TokenType.IF),
            entry("elif", TokenType.ELIF),
            entry("else", TokenType.ELSE),
            entry("match", TokenType.MATCH),
            entry("case", TokenType.CASE),
            entry("union", TokenType.UNION),
            entry("true", TokenType.TRUE),
            entry("false", TokenType.FALSE),
            entry("or", TokenType.OR),
            entry("and", TokenType.AND)
    );
    public static TokenType mapKeyword(String keyword) {
        return keywords.get(keyword);
    }
}
