package me.jkowalc.zephyr.domain.node.program;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Map;

@Getter
public final class Program extends Node {
    private final Map<String, FunctionDefinition> functions;
    private final Map<String, TypeDefinition> types;

    public Program(TextPosition startPosition, TextPosition endPosition, Map<String, FunctionDefinition> functions, Map<String, TypeDefinition> types) {
        super(startPosition, endPosition);
        this.functions = functions;
        this.types = types;
    }
}