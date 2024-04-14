package me.jkowalc.zephyr.util;


public record TextPosition(int line, int column) {
    @Override
    public String toString() {
        return line + ":" + column;
    }

    public TextPosition addColumn(int count) {
        return new TextPosition(line, column + count);
    }
    public TextPosition subtractColumn(int count) {
        return new TextPosition(line, column - count);
    }
}
