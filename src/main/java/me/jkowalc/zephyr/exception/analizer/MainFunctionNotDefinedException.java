package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class MainFunctionNotDefinedException extends AnalyzerException {
    public MainFunctionNotDefinedException(TextPosition position) {
        super("Main function not defined in program", position);
    }
}
