package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class FunctionAlreadyDefinedException extends AnalizerException {
    private static String generateMessage(String name, boolean isBuiltIn) {
        if(isBuiltIn) {
            return "Function " + name + " is already defined as built-in function";
        } else {
            return "Function " + name + " is already defined";
        }
    }
    public FunctionAlreadyDefinedException(String name, boolean isBuiltIn, TextPosition position) {
        super(generateMessage(name, isBuiltIn), position);
    }
}
