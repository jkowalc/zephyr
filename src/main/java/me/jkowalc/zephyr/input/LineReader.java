package me.jkowalc.zephyr.input;

import me.jkowalc.zephyr.util.TextPosition;

import java.io.IOException;
import java.io.InputStreamReader;

public class LineReader {
    private final ZephyrBufferedReader bufferedReader;
    private int currentLine = 1;
    private int currentColumn = 0;
    private char currentChar;
    private boolean resetColumnFlag = false;
    public LineReader(InputStreamReader inputStreamReader) {
        this.bufferedReader = new CharacterReplacer(inputStreamReader);
    }
    public TextPosition getPosition() {
        return new TextPosition(currentLine, currentColumn);
    }
    public void next() throws IOException {
        char nextChar = bufferedReader.read();
        if (resetColumnFlag) {
            currentLine++;
            currentColumn = 1;
            resetColumnFlag = false;
        } else {
            currentColumn++;
        }
        if (nextChar == '\n') resetColumnFlag = true;
        currentChar = nextChar;
    }
    public char peek() throws IOException {
        return bufferedReader.peek();
    }

    public char getCurrentChar() {
        return currentChar;
    }
}
