package me.jkowalc.zephyr;

import me.jkowalc.zephyr.input.TextPrinter;

public class MockTextPrinter implements TextPrinter {
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void print(String text) {
        stringBuilder.append(text);
    }
    public String getText() {
        return stringBuilder.toString();
    }
}
