package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class TypeNotDefinedException extends AnalyzerException {
    public TypeNotDefinedException(String name, TextPosition position) {
        super("Type " + name + " is not defined", position);
    }
}
