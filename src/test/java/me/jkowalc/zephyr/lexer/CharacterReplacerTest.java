package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.input.CharacterReplacer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterReplacerTest {
    @Test
    public void endOfFileTest() throws IOException {
        String input = "a";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.read());
        assertEquals(Character.UNASSIGNED, characterReplacer.read());
    }
    @Test
    public void basicNewlineTest() throws IOException {
        String input = "a\nb";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.read());
        assertEquals('\n', characterReplacer.read());
        assertEquals('b', characterReplacer.read());
    }
    @Test
    public void carriageReturnTest() throws IOException {
        String input = "a\rb";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.read());
        assertEquals('\n', characterReplacer.read());
        assertEquals('b', characterReplacer.read());
    }
    @Test
    public void carriageReturnNewlineTest() throws IOException {
        String input = "a\r\nb";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.read());
        assertEquals('\n', characterReplacer.read());
        assertEquals('b', characterReplacer.read());
    }
    @Test
    public void basicPeekTest() throws IOException {
        String input = "ab";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.peek());
        assertEquals('a', characterReplacer.read());
        assertEquals('b', characterReplacer.peek());
        assertEquals('b', characterReplacer.read());
        assertEquals(Character.UNASSIGNED, characterReplacer.peek());
    }
    @Test
    public void peekEndOfFileTest() throws IOException {
        String input = "a";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.peek());
        assertEquals('a', characterReplacer.read());
        assertEquals(Character.UNASSIGNED, characterReplacer.peek());
    }
    @Test
    public void peekNewlineTest() throws IOException {
        String input = "a\nb";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.read());
        assertEquals('\n', characterReplacer.peek());
        assertEquals('\n', characterReplacer.read());
        assertEquals('b', characterReplacer.read());
    }
    @Test
    public void peekCarriageReturnTest() throws IOException {
        String input = "a\rb";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.read());
        assertEquals('\n', characterReplacer.peek());
        assertEquals('\n', characterReplacer.read());
        assertEquals('b', characterReplacer.read());
    }
    @Test
    public void peekCarriageReturnNewlineTest() throws IOException {
        String input = "a\r\nb";
        CharacterReplacer characterReplacer = new CharacterReplacer(getStringAsInputStreamReader(input));
        assertEquals('a', characterReplacer.read());
        assertEquals('\n', characterReplacer.peek());
        assertEquals('\n', characterReplacer.read());
        assertEquals('b', characterReplacer.read());
    }
}
