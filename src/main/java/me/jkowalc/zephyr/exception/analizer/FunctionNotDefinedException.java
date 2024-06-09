package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class FunctionNotDefinedException extends AnalizerException {
    public FunctionNotDefinedException(String name, TextPosition position) {
        super("Function " + name + " does not exist", position);
    }
}
