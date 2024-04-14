package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.exception.LexicalException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleTokenTest {
    private void testToken(String input, Token expected) throws IOException, LexicalException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        Token token = lexer.nextToken();
        assertEquals(expected, token);
    }

    @Test
    public void testIdentifier() throws IOException, LexicalException {
        testToken("abc", new IdentifierToken("abc"));
    }
}
