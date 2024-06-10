package me.jkowalc.zephyr.exception.runtime;

import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.TextPosition;

public class ZephyrRuntimeException extends ZephyrException {
    public ZephyrRuntimeException(String message, TextPosition position) {
        super(message, position);
    }
}
