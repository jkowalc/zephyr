package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class DuplicateStructFieldException extends AnalyzerException{
    public DuplicateStructFieldException(String fieldName, TextPosition position) {
        super("Field \"" + fieldName + "\" is duplicated", position);
    }
}
