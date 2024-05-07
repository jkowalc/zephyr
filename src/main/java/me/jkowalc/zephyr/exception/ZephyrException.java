package me.jkowalc.zephyr.exception;

import me.jkowalc.zephyr.util.TextPosition;

public class ZephyrException extends Exception {
    private final TextPosition position;
    public ZephyrException(String message, TextPosition position) {
        super(message);
        this.position = position;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getMessage() + " at " + position;
    }
}