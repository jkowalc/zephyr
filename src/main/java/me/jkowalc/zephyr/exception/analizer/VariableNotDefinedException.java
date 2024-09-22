package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class VariableNotDefinedException extends AnalyzerException {
    public VariableNotDefinedException(String name, TextPosition position) {
        super("Variable " + name + " not defined", position);
    }
}
