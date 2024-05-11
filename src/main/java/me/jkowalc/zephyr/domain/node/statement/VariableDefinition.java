package me.jkowalc.zephyr.domain.node.statement;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class VariableDefinition extends Node implements Statement {
    private final String name;
    private final String typeName;

    public VariableDefinition(String name, String typeName) {
        super(null, null);
        this.name = name;
        this.typeName = typeName;
    }

    public VariableDefinition(TextPosition startPosition, TextPosition endPosition, String name, String typeName) {
        super(startPosition, endPosition);
        this.name = name;
        this.typeName = typeName;
    }

}
