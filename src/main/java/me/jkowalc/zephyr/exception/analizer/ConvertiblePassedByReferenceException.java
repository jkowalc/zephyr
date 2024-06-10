package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class ConvertiblePassedByReferenceException extends AnalizerException {
    public ConvertiblePassedByReferenceException(TextPosition position) {
        super("Type imposing conversion cannot be passed by reference", position);
    }
}
