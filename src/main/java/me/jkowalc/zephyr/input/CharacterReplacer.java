package me.jkowalc.zephyr.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CharacterReplacer implements ZephyrBufferedReader {
    private final BufferedReader bufferedReader;
    public CharacterReplacer(InputStreamReader inputStreamReader) {
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    @Override
    public char peek() throws IOException {
        bufferedReader.mark(1);
        int nextChar = bufferedReader.read();
        bufferedReader.reset();
        if(nextChar == -1) return Character.UNASSIGNED;
        if(nextChar == '\r') {
            return '\n';
        }
        return (char) nextChar;
    }

    @Override
    public char read() throws IOException {
        int c = bufferedReader.read();
        if (c == -1) {
            return Character.UNASSIGNED;
        }
        if (c == '\r') {
            bufferedReader.mark(1);
            int nextChar = bufferedReader.read();
            if (nextChar != '\n') {
                bufferedReader.reset();
            }
            return '\n';
        }
        return (char) c;
    }
}
