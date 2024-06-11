package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class VariableAlreadyDefinedException extends AnalizerException {
    public VariableAlreadyDefinedException(String name, TextPosition position) {
        super("Variable " + name + " is already defined", position);
    }
}
