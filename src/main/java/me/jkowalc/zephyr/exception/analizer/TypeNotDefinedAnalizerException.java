package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class TypeNotDefinedAnalizerException extends AnalizerException {
    public TypeNotDefinedAnalizerException(String name, TextPosition position) {
        super("Type " + name + " is not defined", position);
    }
}
