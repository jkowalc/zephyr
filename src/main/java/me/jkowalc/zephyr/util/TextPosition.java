package me.jkowalc.zephyr.util;


public record TextPosition(int line, int column) implements Comparable<TextPosition> {
    @Override
    public String toString() {
        return line + ":" + column;
    }

    @Override
    public int compareTo(TextPosition other) {
        if (line == other.line) {
            return Integer.compare(column, other.column);
        }
        return Integer.compare(line, other.line);
    }
}
