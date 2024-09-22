package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.util.TextPosition;

public class RecursiveTypeDefinitionException extends AnalyzerException {
    public RecursiveTypeDefinitionException(String name, TextPosition position) {
        super("Type " + name + " is referenced recursively in type definition", position);
    }
}
