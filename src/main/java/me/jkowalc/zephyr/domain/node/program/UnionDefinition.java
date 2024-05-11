package me.jkowalc.zephyr.domain.node.program;

import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

public final class UnionDefinition extends Node implements TypeDefinition {
    private final String name;
    private final List<String> typeNames;

    public UnionDefinition(String name, List<String> typeNames) {
        super(null, null);
        this.name = name;
        this.typeNames = typeNames;
    }

    public UnionDefinition(TextPosition startPosition, TextPosition endPosition, String name, List<String> typeNames) {
        super(startPosition, endPosition);
        this.name = name;
        this.typeNames = typeNames;
    }
}
