package me.jkowalc.zephyr.input;

import me.jkowalc.zephyr.util.TextPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LineReader {
    private final BufferedReader bufferedReader;
    private int currentLine = 1;
    private int currentColumn = 0;
    private char currentChar;
    private boolean resetColumnFlag = false;
    private char castToChar(int i) {
        if(i == -1) return Character.UNASSIGNED;
        return (char) i;
    }
    public LineReader(InputStreamReader inputStreamReader) {
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }
    public TextPosition getPosition() {
        return new TextPosition(currentLine, currentColumn);
    }
    public void next() throws IOException {
        int nextChar = bufferedReader.read();
        if (resetColumnFlag) {
            currentLine++;
            currentColumn = 1;
            resetColumnFlag = false;
        } else {
            currentColumn++;
        }
        if (nextChar == '\n') resetColumnFlag = true;
        currentChar = castToChar(nextChar);
    }
    public int peek() throws IOException {
        bufferedReader.mark(1);
        int nextChar = bufferedReader.read();
        bufferedReader.reset();
        return castToChar(nextChar);
    }

    public char getCurrentChar() {
        return currentChar;
    }
}
