package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class VariableAlreadyDefinedAnalizerException extends AnalizerException {
    public VariableAlreadyDefinedAnalizerException(String name, TextPosition position) {
        super("Variable " + name + " is already defined", position);
    }
}
