package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.exception.LexicalException;
import me.jkowalc.zephyr.input.LineReader;

import java.io.IOException;
import java.io.InputStreamReader;

public class Lexer {
    private StringBuilder buffer = new StringBuilder();
    private final LineReader reader;
    public Lexer(InputStreamReader inputStreamReader) {
        this.reader = new LineReader(inputStreamReader);
    }
    private void skipWhitespace() throws IOException {
        do {
            reader.next();
        } while (Character.isWhitespace(reader.getCurrentChar()));
    }
    private Token readIdentifierOrKeyword() {
        return null;
    }
    private Token readNumber() {
        return null;
    }
    private Token readString() {
        return null;
    }
    private Token readOperator() {
        return null;
    }
    private Token readComment() {
        return null;
    }
    public Token nextToken() throws IOException, LexicalException {
        buffer.setLength(0);
        skipWhitespace();
        Token token;
        token = readIdentifierOrKeyword();
        if(reader.getCurrentChar() == Character.UNASSIGNED) {
            return new Token(reader.getPosition(), TokenType.EOF);
        }
        if (token != null) {return token;}
        token = readNumber();
        if (token != null) {return token;}
        token = readString();
        if (token != null) {return token;}
        token = readOperator();
        if (token != null) {return token;}
        token = readComment();
        if (token != null) {return token;}

        throw new LexicalException("Invalid character " + reader.getCurrentChar(), reader.getPosition());
    }
}
