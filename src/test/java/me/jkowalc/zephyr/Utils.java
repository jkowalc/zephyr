package me.jkowalc.zephyr;

import java.io.InputStreamReader;

public class Utils {
    public static InputStreamReader getStringAsInputStreamReader(String string) {
        return new InputStreamReader(new java.io.ByteArrayInputStream(string.getBytes()));
    }
}
