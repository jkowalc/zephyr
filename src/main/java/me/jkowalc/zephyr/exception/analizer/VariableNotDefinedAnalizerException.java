package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class VariableNotDefinedAnalizerException extends AnalizerException {
    public VariableNotDefinedAnalizerException(String name, TextPosition position) {
        super("Variable " + name + " not defined", position);
    }
}
