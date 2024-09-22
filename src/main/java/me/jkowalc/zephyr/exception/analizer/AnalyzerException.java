package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.TextPosition;

public class AnalyzerException extends ZephyrException {
    public AnalyzerException(String message, TextPosition position) {
        super(message, position);
    }
}
