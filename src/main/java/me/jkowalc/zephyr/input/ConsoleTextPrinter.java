package me.jkowalc.zephyr.input;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class ConsoleTextPrinter implements TextPrinter {
    private final OutputStreamWriter writer = new OutputStreamWriter(System.out);
    @Override
    public void print(String text) {
        try {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
