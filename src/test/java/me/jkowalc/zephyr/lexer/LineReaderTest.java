package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.Utils;
import me.jkowalc.zephyr.input.LineReader;
import me.jkowalc.zephyr.util.TextPosition;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineReaderTest {
    private void assertCharLineColumn(LineReader reader, char c, int line, int column) {
        assertEquals(c, reader.getChar());
        assertEquals(new TextPosition(line, column), reader.getPosition());
    }
    @Test
    void standardText() throws IOException {
        LineReader reader = new LineReader(Utils.getStringAsInputStreamReader("ab"));
        assertCharLineColumn(reader, 'a', 1, 1);
        reader.next();
        assertCharLineColumn(reader, 'b', 1, 2);
        reader.next();
        assertEquals(Character.UNASSIGNED, reader.getChar());
    }
    @Test
    void asciiText() throws IOException {
        LineReader reader = new LineReader(Utils.getStringAsInputStreamReader("ą"));
        assertEquals('ą', reader.getChar());
    }
    @Test
    void newline() throws IOException {
        LineReader reader = new LineReader(Utils.getStringAsInputStreamReader("ab\nc"));
        reader.next(); // on b now
        reader.next(); // on \n now
        assertCharLineColumn(reader, '\n', 1, 3);
        reader.next(); // on c now
        assertCharLineColumn(reader, 'c', 2, 1);
    }
    @Test
    void doubleNewline() throws IOException {
        LineReader reader = new LineReader(Utils.getStringAsInputStreamReader("a\n\nc"));
        reader.next();
        assertCharLineColumn(reader, '\n', 1, 2);
        reader.next();
        assertCharLineColumn(reader, '\n', 2, 1);
        reader.next();
        assertCharLineColumn(reader, 'c', 3, 1);
    }
    @Test
    void peekTest() throws IOException {
        LineReader reader = new LineReader(Utils.getStringAsInputStreamReader("ab"));
        assertCharLineColumn(reader, 'a', 1, 1);
        assertEquals('b', reader.peek());
        assertCharLineColumn(reader, 'a', 1, 1);
        reader.next();
        assertCharLineColumn(reader, 'b', 1, 2);
        assertEquals(Character.UNASSIGNED, reader.peek());
    }
    @Test
    void newlineAtStart() throws IOException {
        LineReader reader = new LineReader(Utils.getStringAsInputStreamReader("\na"));
        assertCharLineColumn(reader, '\n', 1, 1);
        reader.next();
        assertCharLineColumn(reader, 'a', 2, 1);
    }
}
