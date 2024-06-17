package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class VariableNotInitializedException extends AnalizerException {
    public VariableNotInitializedException(String name, TextPosition position) {
        super("Variable " + name + " was not initialized", position);
    }
}
