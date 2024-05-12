package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.exception.lexical.LexicalException;

import java.io.IOException;

public interface LexerInterface {
    Token nextToken() throws IOException, LexicalException;
}
