package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.TextPosition;

public class AnalizerException extends ZephyrException {
    public AnalizerException(String message, TextPosition position) {
        super(message, position);
    }
}
