package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class InvalidArgumentCountException extends AnalizerException {
    private static String generateMessage(String functionName, int expected, int got) {
        return got < expected ?
                "Too few arguments for function " + functionName + ". Expected " + expected + ", got " + got + "."
                : "Too many arguments for function " + functionName + ". Expected " + expected + ", got " + got + ".";
    }
    public InvalidArgumentCountException(String functionName, int expected, int got, TextPosition position) {
        super(generateMessage(functionName, got, expected), position);
    }
}
