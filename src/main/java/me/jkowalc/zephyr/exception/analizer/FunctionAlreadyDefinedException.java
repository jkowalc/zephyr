package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class FunctionAlreadyDefinedException extends AnalyzerException {
    public FunctionAlreadyDefinedException(String name, TextPosition position) {
        super("Function " + name + " shadows built-in function", position);
    }
}
