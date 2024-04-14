package me.jkowalc.zephyr.input;

import java.io.IOException;

public interface ZephyrBufferedReader {
    char peek() throws IOException;
    char read() throws IOException;
}
